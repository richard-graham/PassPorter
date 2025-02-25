package com.example.passporter.domain.usecase.border

import com.example.passporter.domain.repository.AuthRepository
import com.example.passporter.domain.repository.BorderRepository
import com.example.passporter.presentation.util.ResultUtil
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class DeleteBorderPointUseCase @Inject constructor(
    private val borderRepository: BorderRepository,
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(id: String): ResultUtil<Unit> {
        return try {
            // Get current user from Flow
            val currentUser = authRepository.currentUser.first()
            val userId = currentUser?.id ?: "unknown"

            val result = borderRepository.markBorderPointAsDeleted(id, userId)

            if (result.isSuccess) {
                ResultUtil.Success(Unit)
            } else {
                ResultUtil.Error(result.exceptionOrNull() ?: Exception("Unknown error"))
            }
        } catch (e: Exception) {
            ResultUtil.Error(e)
        }
    }
}