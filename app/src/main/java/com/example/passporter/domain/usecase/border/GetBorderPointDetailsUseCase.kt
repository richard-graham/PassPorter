package com.example.passporter.domain.usecase.border

import com.example.passporter.domain.entity.BorderPoint
import com.example.passporter.domain.repository.BorderRepository
import com.example.passporter.presentation.util.ResultUtil
import javax.inject.Inject

class GetBorderPointDetailsUseCase @Inject constructor(
    private val borderRepository: BorderRepository
) {
    suspend operator fun invoke(borderId: String): ResultUtil<BorderPoint> {
        return borderRepository.getBorderPointById(borderId)
    }
}