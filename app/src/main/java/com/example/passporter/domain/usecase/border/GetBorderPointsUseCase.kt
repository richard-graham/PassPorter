package com.example.passporter.domain.usecase.border

import com.example.passporter.domain.entity.BorderPoint
import com.example.passporter.domain.repository.BorderRepository
import com.example.passporter.presentation.util.ResultUtil
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetBorderPointsUseCase @Inject constructor(
    private val borderRepository: BorderRepository
) {
    operator fun invoke(): Flow<ResultUtil<List<BorderPoint>>> = flow {
        try {
            borderRepository.getBorderPoints()
                .map { ResultUtil.Success(it) }
                .collect { emit(it) }
        } catch (e: Exception) {
            emit(ResultUtil.Error(e))
        }
    }
}