package com.example.passporter.domain.repository

import com.example.passporter.domain.entity.User
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    val currentUser: Flow<User?>

    suspend fun signInWithEmail(email: String, password: String): Result<User>
    suspend fun signInWithGoogle(idToken: String): Result<User>
    suspend fun signInWithFacebook(accessToken: String): Result<User>
    suspend fun registerWithEmail(
        email: String,
        password: String,
        displayName: String,
        phoneNumber: String?,
        preferredLanguage: String
    ): Result<User>
    suspend fun resetPassword(email: String): Result<Unit>
    suspend fun signOut(): Result<Unit>
}