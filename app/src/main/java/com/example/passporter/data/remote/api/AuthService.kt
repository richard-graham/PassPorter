package com.example.passporter.data.remote.api

import com.example.passporter.data.remote.model.UserDto

interface AuthService {
    suspend fun signInWithEmail(email: String, password: String): Result<UserDto>
    suspend fun registerWithEmail(
        email: String,
        password: String,
        displayName: String,
        preferredLanguage: String
    ): Result<UserDto>
    suspend fun resetPassword(email: String): Result<Unit>
    suspend fun signOut()
    suspend fun getUserData(userId: String): Result<UserDto>
}