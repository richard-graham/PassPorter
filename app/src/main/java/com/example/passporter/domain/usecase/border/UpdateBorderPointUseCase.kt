package com.example.passporter.domain.usecase.border

import com.example.passporter.di.DispatcherProvider
import com.example.passporter.domain.entity.BorderPoint
import com.example.passporter.domain.repository.BorderRepository
import com.example.passporter.presentation.util.ResultUtil
import kotlinx.coroutines.withContext
import javax.inject.Inject

class UpdateBorderPointUseCase @Inject constructor(
    private val borderRepository: BorderRepository,
    private val dispatchers: DispatcherProvider
) {
    suspend operator fun invoke(borderPoint: BorderPoint): ResultUtil<String> = withContext(dispatchers.io) {
        return@withContext try {
            borderRepository.updateBorderPoint(borderPoint)
            ResultUtil.Success(borderPoint.id)
        } catch (e: Exception) {
            ResultUtil.Error(e)
        }
    }
}