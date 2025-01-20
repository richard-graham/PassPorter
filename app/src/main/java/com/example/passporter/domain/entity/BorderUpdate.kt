package com.example.passporter.domain.entity

data class BorderUpdate(
    val id: String,
    val borderPointId: String,
    val status: BorderStatus,
    val message: String,
    val timestamp: Long,
    val reportedBy: String
)