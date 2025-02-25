package com.example.passporter.domain.usecase.border

import com.example.passporter.domain.entity.User
import com.example.passporter.domain.repository.AuthRepository
import com.example.passporter.presentation.util.ResultUtil
import javax.inject.Inject

class GetUserByIdUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(userId: String): ResultUtil<User> {
        return try {
            val result = authRepository.getUserById(userId)
            when {
                result.isSuccess -> {
                    val user = result.getOrNull()
                    if (user != null) {
                        ResultUtil.Success(user)
                    } else {
                        ResultUtil.Error(Exception("User not found"))
                    }
                }
                else -> ResultUtil.Error(result.exceptionOrNull() ?: Exception("Unknown error"))
            }
        } catch (e: Exception) {
            ResultUtil.Error(e)
        }
    }
}