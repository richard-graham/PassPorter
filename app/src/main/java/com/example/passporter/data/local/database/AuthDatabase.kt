package com.example.passporter.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.passporter.data.local.dao.AuthDao
import com.example.passporter.data.local.entity.UserEntity

@Database(
    entities = [
        UserEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AuthDatabase : RoomDatabase() {
    abstract fun userDao(): AuthDao
}