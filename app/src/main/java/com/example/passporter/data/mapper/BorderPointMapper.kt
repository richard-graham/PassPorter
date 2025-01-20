package com.example.passporter.data.mapper

import com.example.passporter.data.local.entity.BorderPointEntity
import com.example.passporter.data.remote.model.BorderPointDto
import com.example.passporter.domain.entity.BorderPoint
import com.example.passporter.domain.entity.BorderStatus
import javax.inject.Inject

class BorderPointMapper @Inject constructor() {
    fun toDto(domain: BorderPoint): BorderPointDto =
        BorderPointDto(
            id = domain.id,
            name = domain.name,
            latitude = domain.latitude,
            longitude = domain.longitude,
            countryA = domain.countryA,
            countryB = domain.countryB,
            status = domain.status.name,
            lastUpdate = domain.lastUpdate,
            createdBy = domain.createdBy,
            description = domain.description
        )

    fun toDomain(dto: BorderPointDto): BorderPoint =
        BorderPoint(
            id = dto.id,
            name = dto.name,
            latitude = dto.latitude,
            longitude = dto.longitude,
            countryA = dto.countryA,
            countryB = dto.countryB,
            status = BorderStatus.valueOf(dto.status),
            lastUpdate = dto.lastUpdate,
            createdBy = dto.createdBy,
            description = dto.description
        )

    fun toEntity(domain: BorderPoint): BorderPointEntity =
        BorderPointEntity(
            id = domain.id,
            name = domain.name,
            latitude = domain.latitude,
            longitude = domain.longitude,
            countryA = domain.countryA,
            countryB = domain.countryB,
            status = domain.status.name,
            lastUpdate = domain.lastUpdate,
            createdBy = domain.createdBy,
            description = domain.description
        )

    fun toDomain(entity: BorderPointEntity): BorderPoint =
        BorderPoint(
            id = entity.id,
            name = entity.name,
            latitude = entity.latitude,
            longitude = entity.longitude,
            countryA = entity.countryA,
            countryB = entity.countryB,
            status = BorderStatus.valueOf(entity.status),
            lastUpdate = entity.lastUpdate,
            createdBy = entity.createdBy,
            description = entity.description
        )
}