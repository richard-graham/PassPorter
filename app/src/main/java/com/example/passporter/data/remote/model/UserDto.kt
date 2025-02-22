package com.example.passporter.data.remote.model

data class UserDto(
    val id: String = "",
    val email: String = "",
    val displayName: String = "",
    val photoUrl: String? = "",
    val createdAt: Long = 0L,
    val preferredLanguage: String = "",
    val notificationsEnabled: Boolean = false
)