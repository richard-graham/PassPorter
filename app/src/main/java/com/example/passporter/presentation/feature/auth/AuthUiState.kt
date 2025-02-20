package com.example.passporter.presentation.feature.auth

import com.example.passporter.domain.entity.User

sealed class AuthUiState {
    data object Initial : AuthUiState()
    data object Loading : AuthUiState()
    data class Success(val user: User) : AuthUiState()
    data class Error(
        val message: String,
        val errorType: AuthErrorType = AuthErrorType.UNKNOWN
    ) : AuthUiState()
}

enum class AuthErrorType {
    INVALID_CREDENTIALS,
    NETWORK_ERROR,
    USER_COLLISION,
    VALIDATION_ERROR,
    UNKNOWN
}