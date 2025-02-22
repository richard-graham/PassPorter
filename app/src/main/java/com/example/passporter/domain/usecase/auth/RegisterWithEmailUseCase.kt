package com.example.passporter.domain.usecase.auth

import com.example.passporter.domain.entity.User
import com.example.passporter.domain.repository.AuthRepository
import javax.inject.Inject

class RegisterWithEmailUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(
        email: String,
        password: String,
        displayName: String,
        preferredLanguage: String
    ): Result<User> {
        // Validation
        if (!isValidEmail(email)) {
            return Result.failure(IllegalArgumentException("Invalid email format"))
        }

        if (!isValidPassword(password)) {
            return Result.failure(IllegalArgumentException("Password must be at least 8 characters long and contain at least one number, one uppercase letter, and one special character"))
        }

        if (displayName.length < 2) {
            return Result.failure(IllegalArgumentException("Display name must be at least 2 characters long"))
        }

        if (preferredLanguage.isBlank()) {
            return Result.failure(IllegalArgumentException("Preferred language must be selected"))
        }

        return authRepository.registerWithEmail(
            email = email,
            password = password,
            displayName = displayName,
            preferredLanguage = preferredLanguage
        )
    }

    private fun isValidEmail(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    private fun isValidPassword(password: String): Boolean {
        val passwordPattern = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,}$"
        return password.matches(passwordPattern.toRegex())
    }
}