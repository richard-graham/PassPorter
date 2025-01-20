package com.example.passporter.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.passporter.data.local.entity.BorderPointEntity
import com.example.passporter.data.local.entity.BorderUpdateEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface BorderDao {
    @Query("SELECT * FROM border_points")
    fun getBorderPoints(): Flow<List<BorderPointEntity>>

    @Query("SELECT * FROM border_points WHERE id = :id")
    suspend fun getBorderPoint(id: String): BorderPointEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBorderPoint(borderPoint: BorderPointEntity)

    @Query("SELECT * FROM border_updates WHERE borderPointId = :borderPointId ORDER BY timestamp DESC")
    fun getBorderUpdates(borderPointId: String): Flow<List<BorderUpdateEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBorderUpdate(update: BorderUpdateEntity)
}