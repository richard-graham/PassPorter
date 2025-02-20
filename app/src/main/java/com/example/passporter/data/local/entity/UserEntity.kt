package com.example.passporter.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey val id: String,
    val email: String,
    val displayName: String,
    val photoUrl: String?,
    val phoneNumber: String?,
    val createdAt: Long,
    val preferredLanguage: String,
    val notificationsEnabled: Boolean,
    val lastLoginAt: Long
)