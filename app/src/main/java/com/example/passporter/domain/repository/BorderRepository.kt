package com.example.passporter.domain.repository

import com.example.passporter.domain.entity.BorderPoint
import com.example.passporter.domain.entity.BorderUpdate
import com.example.passporter.presentation.util.ResultUtil
import kotlinx.coroutines.flow.Flow

interface BorderRepository {
    fun getBorderPoints(): Flow<List<BorderPoint>>
    suspend fun addBorderPoint(borderPoint: BorderPoint): ResultUtil<Unit>
    fun getBorderUpdates(borderPointId: String): Flow<List<BorderUpdate>>
    suspend fun addBorderUpdate(update: BorderUpdate): ResultUtil<Unit>
    suspend fun subscribeToBorderPoint(userId: String, borderPointId: String): ResultUtil<Unit>
}