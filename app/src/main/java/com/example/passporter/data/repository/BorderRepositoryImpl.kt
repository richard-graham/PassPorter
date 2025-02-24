package com.example.passporter.data.repository

import android.os.Build
import androidx.annotation.RequiresApi
import com.example.passporter.data.local.dao.BorderDao
import com.example.passporter.data.mapper.BorderPointMapper
import com.example.passporter.data.mapper.BorderUpdateMapper
import com.example.passporter.data.remote.api.FirestoreService
import com.example.passporter.di.DispatcherProvider
import com.example.passporter.domain.entity.BorderPoint
import com.example.passporter.domain.entity.BorderUpdate
import com.example.passporter.domain.repository.BorderRepository
import com.example.passporter.presentation.util.ResultUtil
import com.google.android.gms.maps.model.LatLngBounds
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

class BorderRepositoryImpl @Inject constructor(
    private val firestoreService: FirestoreService,
    private val borderDao: BorderDao,
    private val borderPointMapper: BorderPointMapper,
    private val borderUpdateMapper: BorderUpdateMapper,
    private val dispatcherProvider: DispatcherProvider
) : BorderRepository {

    @RequiresApi(Build.VERSION_CODES.O)
    override fun syncBorderPoints(): Flow<List<BorderPoint>> = channelFlow {
        // If database was empty, fetch from network
        if (borderDao.getBorderPoints().first().isEmpty()) {
            firestoreService.getBorderPoints()
                .map { dtos -> dtos.map(borderPointMapper::toDomain) }
                .collect { borderPoints ->
                    withContext(dispatcherProvider.io) {
                        borderPoints.forEach {
                            borderDao.insertBorderPoint(borderPointMapper.toEntity(it))
                        }
                    }
                    send(emptyList<BorderPoint>())
                }
        } else {
            send(emptyList())
        }
    }.flowOn(dispatcherProvider.io)

    @RequiresApi(Build.VERSION_CODES.O)
    override fun getBorderPointsByCoordinates(bounds: LatLngBounds): Flow<List<BorderPoint>> = channelFlow {
        launch(dispatcherProvider.io) {
            val borderPoints = borderDao.getBorderPointsInBounds(
                south = bounds.southwest.latitude,
                north = bounds.northeast.latitude,
                west = bounds.southwest.longitude,
                east = bounds.northeast.longitude
            ).first()
            if (borderPoints.isNotEmpty()) {
                send(borderPoints.map(borderPointMapper::toDomain))
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override suspend fun getBorderPointById(id: String): ResultUtil<BorderPoint> =
        withContext(dispatcherProvider.io) {
            try {
                val borderPoint = borderDao.getBorderPointById(id).first()
                if (borderPoint != null) {
                    ResultUtil.Success(borderPointMapper.toDomain(borderPoint))
                } else {
                   ResultUtil.Error(Throwable("Border point not found"))
                }
            } catch (e: Exception) {
                ResultUtil.Error(e)
            }
        }

    @RequiresApi(Build.VERSION_CODES.O)
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

    @RequiresApi(Build.VERSION_CODES.O)
    override suspend fun updateBorderPoint(borderPoint: BorderPoint): ResultUtil<Unit> {
        return try {
            // Update in Firestore
            firestoreService.updateBorderPoint(borderPointMapper.toDto(borderPoint))

            // Update local cache
            borderDao.updateBorderPoint(borderPointMapper.toEntity(borderPoint))

            ResultUtil.Success(Unit)
        } catch (e: Exception) {
            ResultUtil.Error(e)
        }
    }
}