package com.example.passporter.domain.repository

import com.example.passporter.domain.entity.BorderPoint
import com.example.passporter.domain.entity.BorderUpdate
import com.example.passporter.presentation.util.ResultUtil
import com.google.android.gms.maps.model.LatLngBounds
import kotlinx.coroutines.flow.Flow

interface BorderRepository {
    fun syncBorderPoints(): Flow<List<BorderPoint>>
    fun getBorderPointsByCoordinates(bounds: LatLngBounds): Flow<List<BorderPoint>>
    suspend fun getBorderPointById(id: String): ResultUtil<BorderPoint>
    suspend fun addBorderPoint(borderPoint: BorderPoint): ResultUtil<String>
    fun getBorderUpdates(borderPointId: String): Flow<List<BorderUpdate>>
    suspend fun addBorderUpdate(update: BorderUpdate): ResultUtil<Unit>
    suspend fun subscribeToBorderPoint(userId: String, borderPointId: String): ResultUtil<Unit>
    suspend fun updateBorderPoint(borderPoint: BorderPoint): ResultUtil<String>
    suspend fun markBorderPointAsDeleted(id: String, userId: String): Result<Unit>
}