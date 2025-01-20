package com.example.passporter.data.remote.model

data class BorderUpdateDto(
    val id: String = "",
    val borderPointId: String = "",
    val status: String = "",
    val message: String = "",
    val timestamp: Long = 0L,
    val reportedBy: String = ""
)