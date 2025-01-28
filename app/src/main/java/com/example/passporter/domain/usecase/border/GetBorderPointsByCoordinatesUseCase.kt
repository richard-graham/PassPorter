package com.example.passporter.domain.usecase.border

import com.example.passporter.domain.entity.BorderPoint
import com.example.passporter.domain.repository.BorderRepository
import com.example.passporter.presentation.util.ResultUtil
import com.google.android.gms.maps.model.LatLngBounds
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetBorderPointsByCoordinatesUseCase @Inject constructor(
    private val borderRepository: BorderRepository
) {
    operator fun invoke(bounds: LatLngBounds): Flow<ResultUtil<List<BorderPoint>>> = flow {
        try {
            borderRepository.getBorderPointsByCoordinates(bounds = bounds)
                .map { ResultUtil.Success(it) }
                .collect { emit(it) }
        } catch (e: Exception) {
            emit(ResultUtil.Error(e))
        }
    }
}