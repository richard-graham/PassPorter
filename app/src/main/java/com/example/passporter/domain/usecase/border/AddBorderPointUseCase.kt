package com.example.passporter.domain.usecase.border

import com.example.passporter.di.DispatcherProvider
import com.example.passporter.domain.entity.BorderPoint
import com.example.passporter.domain.repository.BorderRepository
import kotlinx.coroutines.withContext
import javax.inject.Inject

class AddBorderPointUseCase @Inject constructor(
    private val borderRepository: BorderRepository,
    private val dispatchers: DispatcherProvider
) {
    suspend operator fun invoke(borderPoint: BorderPoint): Result<Unit> = withContext(dispatchers.io) {
        return@withContext try {
            borderRepository.addBorderPoint(borderPoint)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}