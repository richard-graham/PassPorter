package com.example.passporter.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDateTime

@Entity(tableName = "border_points")
data class BorderPointEntity(
    @PrimaryKey val id: String,
    val name: String,
    val latitude: Double,
    val longitude: Double,
    val countryA: String,
    val countryB: String,
    val status: String,
    val lastUpdate: Long,
    val createdBy: String,
    val description: String
)