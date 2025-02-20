package com.example.passporter.domain.error

sealed class AuthError : Exception() {
    data object InvalidCredentials : AuthError() {
        private fun readResolve(): Any = InvalidCredentials
    }

    data object NetworkError : AuthError() {
        private fun readResolve(): Any = NetworkError
    }

    data object UserCollision : AuthError() {
        private fun readResolve(): Any = UserCollision
    }

    data class UnknownError(val originalError: Exception) : AuthError()
}