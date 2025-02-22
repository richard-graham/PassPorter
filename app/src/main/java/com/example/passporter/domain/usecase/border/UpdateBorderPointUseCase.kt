package com.example.passporter.domain.usecase.border

import com.example.passporter.domain.entity.BorderPoint
import com.example.passporter.domain.repository.BorderRepository
import com.example.passporter.presentation.util.ResultUtil
import javax.inject.Inject

class UpdateBorderPointUseCase @Inject constructor(
    private val borderRepository: BorderRepository
) {
    suspend operator fun invoke(borderPoint: BorderPoint): ResultUtil<Unit> {
        return borderRepository.updateBorderPoint(borderPoint)
    }
}