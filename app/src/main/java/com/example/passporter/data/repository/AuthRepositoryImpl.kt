package com.example.passporter.data.repository

import com.example.passporter.data.local.dao.AuthDao
import com.example.passporter.data.mapper.UserMapper
import com.example.passporter.data.remote.api.AuthService
import com.example.passporter.data.remote.model.UserDto
import com.example.passporter.di.DispatcherProvider
import com.example.passporter.domain.entity.User
import com.example.passporter.domain.repository.AuthRepository
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val authService: AuthService,
    private val authDao: AuthDao,
    private val userMapper: UserMapper,
    private val dispatcherProvider: DispatcherProvider
) : AuthRepository {
    override val currentUser: Flow<User?> = authDao.getCurrentUser()
        .map { it?.let { userMapper.mapToDomain(it) } }

    override suspend fun signInWithEmail(email: String, password: String): Result<User> =
        withContext(dispatcherProvider.io) {
            try {
                authService.signInWithEmail(email, password)
                    .map { userMapper.mapToDomain(it) }
                    .onSuccess { user ->
                        authDao.insertUser(userMapper.mapToEntity(user))
                    }
            } catch (e: Exception) {
                Result.failure(mapToAuthError(e))
            }
        }

    override suspend fun signInWithGoogle(idToken: String): Result<User> =
        withContext(dispatcherProvider.io) {
            try {
                authService.signInWithGoogle(idToken)
                    .map { userMapper.mapToDomain(it) }
                    .onSuccess { user ->
                        authDao.insertUser(userMapper.mapToEntity(user))
                    }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }

    override suspend fun signInWithFacebook(accessToken: String): Result<User> =
        withContext(dispatcherProvider.io) {
            try {
                authService.signInWithFacebook(accessToken)
                    .map { userMapper.mapToDomain(it) }
                    .onSuccess { user ->
                        authDao.insertUser(userMapper.mapToEntity(user))
                    }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }

    override suspend fun registerWithEmail(
        email: String,
        password: String,
        displayName: String,
        phoneNumber: String?,
        preferredLanguage: String
    ): Result<User> =
        withContext(dispatcherProvider.io) {
            try {
                authService.registerWithEmail(
                    email = email,
                    password = password,
                    displayName = displayName,
                    phoneNumber = phoneNumber,
                    preferredLanguage = preferredLanguage
                ).map { userMapper.mapToDomain(it) }
                    .onSuccess { user ->
                        authDao.insertUser(userMapper.mapToEntity(user))
                    }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }

    override suspend fun resetPassword(email: String): Result<Unit> =
        withContext(dispatcherProvider.io) {
            try {
                authService.resetPassword(email)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }

    override suspend fun signOut(): Result<Unit> =
        withContext(dispatcherProvider.io) {
            try {
                authService.signOut()
                authDao.deleteAllUsers()
                Result.success(Unit)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }

    private fun mapToAuthError(error: Exception): AuthError = when (error) {
        is FirebaseAuthInvalidCredentialsException -> AuthError.InvalidCredentials
        is FirebaseNetworkException -> AuthError.NetworkError
        is FirebaseAuthUserCollisionException -> AuthError.UserCollision
        else -> AuthError.UnknownError(error)
    }

    // Add companion object for error handling
    companion object {
        sealed class AuthError : Exception() {
            object InvalidCredentials : AuthError() {
                private fun readResolve(): Any = InvalidCredentials
            }

            object NetworkError : AuthError() {
                private fun readResolve(): Any = NetworkError
            }

            object UserCollision : AuthError() {
                private fun readResolve(): Any = UserCollision
            }

            class UnknownError(val originalError: Exception) : AuthError()
        }
    }
}