package com.example.passporter.domain.usecase.auth

import com.example.passporter.domain.entity.User
import com.example.passporter.domain.repository.AuthRepository
import javax.inject.Inject

class SignInWithFacebookUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(accessToken: String): Result<User> {
        if (accessToken.isBlank()) {
            return Result.failure(IllegalArgumentException("Facebook access token cannot be empty"))
        }
        return authRepository.signInWithFacebook(accessToken)
    }
}
