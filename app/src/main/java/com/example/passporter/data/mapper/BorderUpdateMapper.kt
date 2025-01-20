package com.example.passporter.data.mapper

import com.example.passporter.data.local.entity.BorderUpdateEntity
import com.example.passporter.data.remote.model.BorderUpdateDto
import com.example.passporter.domain.entity.BorderStatus
import com.example.passporter.domain.entity.BorderUpdate
import javax.inject.Inject

class BorderUpdateMapper @Inject constructor() {
    fun toDto(domain: BorderUpdate): BorderUpdateDto =
        BorderUpdateDto(
            id = domain.id,
            borderPointId = domain.borderPointId,
            status = domain.status.name,
            message = domain.message,
            timestamp = domain.timestamp,
            reportedBy = domain.reportedBy
        )

    fun toDomain(dto: BorderUpdateDto): BorderUpdate =
        BorderUpdate(
            id = dto.id,
            borderPointId = dto.borderPointId,
            status = BorderStatus.valueOf(dto.status),
            message = dto.message,
            timestamp = dto.timestamp,
            reportedBy = dto.reportedBy
        )

    fun toEntity(domain: BorderUpdate): BorderUpdateEntity =
        BorderUpdateEntity(
            id = domain.id,
            borderPointId = domain.borderPointId,
            status = domain.status.name,
            message = domain.message,
            timestamp = domain.timestamp,
            reportedBy = domain.reportedBy
        )

    fun toDomain(entity: BorderUpdateEntity): BorderUpdate =
        BorderUpdate(
            id = entity.id,
            borderPointId = entity.borderPointId,
            status = BorderStatus.valueOf(entity.status),
            message = entity.message,
            timestamp = entity.timestamp,
            reportedBy = entity.reportedBy
        )
}