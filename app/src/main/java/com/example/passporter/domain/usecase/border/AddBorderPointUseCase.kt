package com.example.passporter.domain.usecase.border

import com.example.passporter.domain.entity.BorderPoint
import com.example.passporter.domain.entity.BorderStatus
import com.example.passporter.domain.repository.BorderRepository
import com.example.passporter.presentation.util.ResultUtil
import java.util.UUID
import javax.inject.Inject

class AddBorderPointUseCase @Inject constructor(
    private val borderRepository: BorderRepository
) {
    suspend operator fun invoke(
        name: String,
        latitude: Double,
        longitude: Double,
        countryA: String,
        countryB: String,
        description: String
    ): ResultUtil<Unit> {
        val borderPoint = BorderPoint(
            id = UUID.randomUUID().toString(),
            name = name,
            latitude = latitude,
            longitude = longitude,
            countryA = countryA,
            countryB = countryB,
            status = BorderStatus.OPEN,
            lastUpdate = System.currentTimeMillis(),
            createdBy = "current_user_id", // Inject user service for this
            description = description
        )
        return borderRepository.addBorderPoint(borderPoint)
    }
}