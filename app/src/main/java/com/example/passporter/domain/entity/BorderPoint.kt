package com.example.passporter.domain.entity

data class BorderPoint(
    val id: String,
    val name: String,
    val latitude: Double,
    val longitude: Double,
    val countryA: String,
    val countryB: String,
    val status: BorderStatus,
    val lastUpdate: Long,
    val createdBy: String,
    val description: String,
    val borderType: String?,
    val crossingType: String?,
    val sourceId: String,
    val dataSource: String
)