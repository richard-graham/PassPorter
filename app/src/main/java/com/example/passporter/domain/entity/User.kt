package com.example.passporter.domain.entity

data class User(
    val id: String,
    val email: String,
    val displayName: String,
    val photoUrl: String?,
    val phoneNumber: String?,
    val createdAt: Long,
    val preferredLanguage: String,
    val notificationsEnabled: Boolean
)