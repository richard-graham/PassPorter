package com.example.passporter.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.passporter.data.local.entity.BorderPointEntity
import com.example.passporter.data.local.entity.BorderUpdateEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface BorderDao {
    @Query("SELECT * FROM border_points")
    fun getBorderPoints(): Flow<List<BorderPointEntity>>

    @Query("SELECT * FROM border_points WHERE " +
            "latitude BETWEEN :south AND :north AND " +
            "longitude BETWEEN :west AND :east AND " +
            "deleted = 0")
    fun getBorderPointsInBounds(
        south: Double,
        north: Double,
        west: Double,
        east: Double
    ): Flow<List<BorderPointEntity>>

    @Query("SELECT * FROM border_points WHERE id = :id")
    fun getBorderPointById(id: String): Flow<BorderPointEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBorderPoint(borderPoint: BorderPointEntity)

    @Query("SELECT * FROM border_updates WHERE borderPointId = :borderPointId ORDER BY timestamp DESC")
    fun getBorderUpdates(borderPointId: String): Flow<List<BorderUpdateEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBorderUpdate(update: BorderUpdateEntity)

    @Update
    suspend fun updateBorderPoint(borderPoint: BorderPointEntity)

    @Query("UPDATE border_points SET deleted = :deleted, deletedAt = :deletedAtTimestamp, deletedBy = :deletedBy WHERE id = :id")
    suspend fun updateBorderPointDeletedStatus(
        id: String,
        deleted: Boolean,
        deletedAtTimestamp: Long,
        deletedBy: String
    )
}