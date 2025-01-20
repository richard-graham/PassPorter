package com.example.passporter.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ForeignKey
import androidx.room.Index

@Entity(
    tableName = "border_updates",
    foreignKeys = [
        ForeignKey(
            entity = BorderPointEntity::class,
            parentColumns = ["id"],
            childColumns = ["borderPointId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("borderPointId")]
)
data class BorderUpdateEntity(
    @PrimaryKey val id: String,
    val borderPointId: String,
    val status: String,
    val message: String,
    val timestamp: Long,
    val reportedBy: String
)