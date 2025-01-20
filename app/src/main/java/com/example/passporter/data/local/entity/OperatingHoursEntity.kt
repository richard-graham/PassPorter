package com.example.passporter.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDateTime

@Entity(tableName = "operating_hours")
data class OperatingHoursEntity(
    @PrimaryKey val id: String,
    val borderCrossingId: String,
    val openTime: Map<String, LocalDateTime>,
    val closeTime: Map<String, LocalDateTime>
)