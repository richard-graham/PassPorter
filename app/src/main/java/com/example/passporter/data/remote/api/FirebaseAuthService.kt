package com.example.passporter.data.remote.api

import com.example.passporter.data.remote.model.UserDto
import com.example.passporter.domain.error.AuthError
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class FirebaseAuthService @Inject constructor(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) : AuthService {

    override suspend fun signInWithEmail(email: String, password: String): Result<UserDto> =
        try {
            val result = auth.signInWithEmailAndPassword(email, password).await()
            result.user?.let { firebaseUser ->
                getUserData(firebaseUser.uid)
            } ?: Result.failure(AuthError.InvalidCredentials)
        } catch (e: Exception) {
            mapToAuthError(e)
        }

    override suspend fun signInWithGoogle(idToken: String): Result<UserDto> =
        try {
            val credential = GoogleAuthProvider.getCredential(idToken, null)
            val result = auth.signInWithCredential(credential).await()

            result.user?.let { firebaseUser ->
                // Check if this is a new user
                if (result.additionalUserInfo?.isNewUser == true) {
                    // Create user data in Firestore
                    createUserData(
                        userId = firebaseUser.uid,
                        email = firebaseUser.email ?: "",
                        displayName = firebaseUser.displayName ?: "",
                        phoneNumber = firebaseUser.phoneNumber,
                        preferredLanguage = "en" // Default to English for social sign-in
                    )
                } else {
                    getUserData(firebaseUser.uid)
                }
            } ?: Result.failure(AuthError.InvalidCredentials)
        } catch (e: Exception) {
            mapToAuthError(e)
        }

    override suspend fun signInWithFacebook(accessToken: String): Result<UserDto> =
        try {
            val credential = FacebookAuthProvider.getCredential(accessToken)
            val result = auth.signInWithCredential(credential).await()

            result.user?.let { firebaseUser ->
                // Check if this is a new user
                if (result.additionalUserInfo?.isNewUser == true) {
                    // Create user data in Firestore
                    createUserData(
                        userId = firebaseUser.uid,
                        email = firebaseUser.email ?: "",
                        displayName = firebaseUser.displayName ?: "",
                        phoneNumber = firebaseUser.phoneNumber,
                        preferredLanguage = "en" // Default to English for social sign-in
                    )
                } else {
                    getUserData(firebaseUser.uid)
                }
            } ?: Result.failure(AuthError.InvalidCredentials)
        } catch (e: Exception) {
            mapToAuthError(e)
        }

    override suspend fun registerWithEmail(
        email: String,
        password: String,
        displayName: String,
        phoneNumber: String?,
        preferredLanguage: String
    ): Result<UserDto> =
        try {
            val result = auth.createUserWithEmailAndPassword(email, password).await()

            result.user?.let { firebaseUser ->
                // Update display name in Firebase Auth
                val profileUpdates = UserProfileChangeRequest.Builder()
                    .setDisplayName(displayName)
                    .build()
                firebaseUser.updateProfile(profileUpdates).await()

                // Create user data in Firestore
                createUserData(
                    userId = firebaseUser.uid,
                    email = email,
                    displayName = displayName,
                    phoneNumber = phoneNumber,
                    preferredLanguage = preferredLanguage
                )
            } ?: Result.failure(AuthError.InvalidCredentials)
        } catch (e: Exception) {
            mapToAuthError(e)
        }

    override suspend fun resetPassword(email: String): Result<Unit> =
        try {
            auth.sendPasswordResetEmail(email).await()
            Result.success(Unit)
        } catch (e: Exception) {
            mapToAuthError(e)
        }

    override suspend fun signOut() {
        auth.signOut()
    }

    private suspend fun createUserData(
        userId: String,
        email: String,
        displayName: String,
        phoneNumber: String?,
        preferredLanguage: String
    ): Result<UserDto> {
        val userData = UserDto(
            id = userId,
            email = email,
            displayName = displayName,
            photoUrl = auth.currentUser?.photoUrl?.toString(),
            phoneNumber = phoneNumber ?: "",
            createdAt = System.currentTimeMillis(),
            preferredLanguage = preferredLanguage,
            notificationsEnabled = true
        )

        return try {
            firestore.collection("users")
                .document(userId)
                .set(userData)
                .await()

            Result.success(userData)
        } catch (e: Exception) {
            Result.failure(AuthError.UnknownError(e))
        }
    }

    private suspend fun getUserData(userId: String): Result<UserDto> =
        try {
            val document = firestore.collection("users")
                .document(userId)
                .get()
                .await()

            if (document.exists()) {
                Result.success(document.toObject(UserDto::class.java)!!)
            } else {
                // If user document doesn't exist, create it with default values
                auth.currentUser?.let { firebaseUser ->
                    createUserData(
                        userId = firebaseUser.uid,
                        email = firebaseUser.email ?: "",
                        displayName = firebaseUser.displayName ?: "",
                        phoneNumber = firebaseUser.phoneNumber,
                        preferredLanguage = "en"
                    )
                } ?: Result.failure(AuthError.InvalidCredentials)
            }
        } catch (e: Exception) {
            Result.failure(AuthError.UnknownError(e))
        }

    private fun mapToAuthError(e: Exception): Result<Nothing> = when (e) {
        is com.google.firebase.auth.FirebaseAuthInvalidCredentialsException ->
            Result.failure(AuthError.InvalidCredentials)
        is com.google.firebase.auth.FirebaseAuthInvalidUserException ->
            Result.failure(AuthError.InvalidCredentials)
        is com.google.firebase.auth.FirebaseAuthUserCollisionException ->
            Result.failure(AuthError.UserCollision)
        is com.google.firebase.FirebaseNetworkException ->
            Result.failure(AuthError.NetworkError)
        else -> Result.failure(AuthError.UnknownError(e))
    }
}