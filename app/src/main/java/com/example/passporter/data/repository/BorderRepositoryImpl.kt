package com.example.passporter.data.repository

import com.example.passporter.data.local.dao.BorderDao
import com.example.passporter.data.mapper.BorderPointMapper
import com.example.passporter.data.mapper.BorderUpdateMapper
import com.example.passporter.data.remote.api.FirestoreService
import com.example.passporter.di.DispatcherProvider
import com.example.passporter.domain.entity.BorderPoint
import com.example.passporter.domain.entity.BorderUpdate
import com.example.passporter.domain.repository.BorderRepository
import com.example.passporter.presentation.util.ResultUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.withContext
import javax.inject.Inject

class BorderRepositoryImpl @Inject constructor(
    private val firestoreService: FirestoreService,
    private val borderDao: BorderDao,
    private val borderPointMapper: BorderPointMapper,
    private val borderUpdateMapper: BorderUpdateMapper,
    private val dispatcherProvider: DispatcherProvider
) : BorderRepository {

    override fun getBorderPoints(): Flow<List<BorderPoint>> =
        firestoreService.getBorderPoints()
            .map { dtos -> dtos.map(borderPointMapper::toDomain) }
            .onEach { borderPoints ->
                withContext(dispatcherProvider.io) {
                    borderPoints.forEach {
                        borderDao.insertBorderPoint(borderPointMapper.toEntity(it))
                    }
                }
            }
            .flowOn(dispatcherProvider.io)

    override suspend fun addBorderPoint(borderPoint: BorderPoint): ResultUtil<Unit> =
        withContext(dispatcherProvider.io) {
            try {
                val dto = borderPointMapper.toDto(borderPoint)
                firestoreService.addBorderPoint(dto)
                borderDao.insertBorderPoint(borderPointMapper.toEntity(borderPoint))
                ResultUtil.Success(Unit)
            } catch (e: Exception) {
                ResultUtil.Error(e)
            }
        }

    override fun getBorderUpdates(borderPointId: String): Flow<List<BorderUpdate>> =
        firestoreService.getBorderUpdates(borderPointId)
            .map { dtos -> dtos.map(borderUpdateMapper::toDomain) }
            .onEach { updates ->
                withContext(dispatcherProvider.io) {
                    updates.forEach {
                        borderDao.insertBorderUpdate(borderUpdateMapper.toEntity(it))
                    }
                }
            }
            .flowOn(dispatcherProvider.io)

    override suspend fun addBorderUpdate(update: BorderUpdate): ResultUtil<Unit> =
        withContext(dispatcherProvider.io) {
            try {
                val dto = borderUpdateMapper.toDto(update)
                firestoreService.addBorderUpdate(dto)
                borderDao.insertBorderUpdate(borderUpdateMapper.toEntity(update))
                ResultUtil.Success(Unit)
            } catch (e: Exception) {
                ResultUtil.Error(e)
            }
        }

    override suspend fun subscribeToBorderPoint(
        userId: String,
        borderPointId: String
    ): ResultUtil<Unit> =
        withContext(dispatcherProvider.io) {
            try {
                firestoreService.subscribeToBorderPoint(userId, borderPointId)
                ResultUtil.Success(Unit)
            } catch (e: Exception) {
                ResultUtil.Error(e)
            }
        }
}