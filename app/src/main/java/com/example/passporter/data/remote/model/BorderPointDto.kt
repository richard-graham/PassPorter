package com.example.passporter.data.remote.model

data class BorderPointDto(
    val id: String = "",
    val name: String = "",
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val countryA: String = "",
    val countryB: String = "",
    val status: String = "",
    val lastUpdate: Long = 0L,
    val createdBy: String = "",
    val description: String = ""
)