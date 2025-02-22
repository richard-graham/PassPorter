package com.example.passporter.domain.error

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

    object UserNotFound : AuthError() {
        private fun readResolve(): Any = UserNotFound
    }

    object InvalidEmail : AuthError() {
        private fun readResolve(): Any = InvalidEmail
    }

    class UnknownError(val originalError: Exception) : AuthError()
}