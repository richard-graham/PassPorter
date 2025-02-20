package com.example.passporter.domain.usecase.auth

import com.example.passporter.domain.entity.User
import com.example.passporter.domain.repository.AuthRepository
import javax.inject.Inject

class SignInWithEmailUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(email: String, password: String): Result<User> =
        authRepository.signInWithEmail(email, password)
}