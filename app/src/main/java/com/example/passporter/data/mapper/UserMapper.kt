package com.example.passporter.data.mapper

import com.example.passporter.data.local.entity.UserEntity
import com.example.passporter.data.remote.model.UserDto
import com.example.passporter.domain.entity.User
import javax.inject.Inject

class UserMapper @Inject constructor() {
    fun mapToDomain(dto: UserDto): User {
        return User(
            id = dto.id,
            email = dto.email,
            displayName = dto.displayName,
            photoUrl = dto.photoUrl,
            createdAt = dto.createdAt,
            preferredLanguage = dto.preferredLanguage,
            notificationsEnabled = dto.notificationsEnabled
        )
    }

    fun mapToDomain(entity: UserEntity): User {
        return User(
            id = entity.id,
            email = entity.email,
            displayName = entity.displayName,
            photoUrl = entity.photoUrl,
            createdAt = entity.createdAt,
            preferredLanguage = entity.preferredLanguage,
            notificationsEnabled = entity.notificationsEnabled
        )
    }

    fun mapToEntity(domain: User): UserEntity {
        return UserEntity(
            id = domain.id,
            email = domain.email,
            displayName = domain.displayName,
            photoUrl = domain.photoUrl,
            createdAt = domain.createdAt,
            preferredLanguage = domain.preferredLanguage,
            notificationsEnabled = domain.notificationsEnabled,
            lastLoginAt = System.currentTimeMillis()
        )
    }
}