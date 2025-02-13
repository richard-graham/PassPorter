package com.example.passporter.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import com.example.passporter.data.local.dao.BorderDao
import com.example.passporter.data.local.entity.BorderPointEntity
import com.example.passporter.data.local.entity.BorderUpdateEntity
import java.util.Date

@Database(
    entities = [BorderPointEntity::class, BorderUpdateEntity::class],
    version = 1
)
@TypeConverters(Converters::class)
abstract class BorderDatabase : RoomDatabase() {
    abstract fun borderDao(): BorderDao
}

class Converters {
    @TypeConverter
    fun fromStringList(value: String?): List<String>? {
        return value?.split(",")?.map { it.trim() }
    }

    @TypeConverter
    fun stringListToString(list: List<String>?): String? {
        return list?.joinToString(",")
    }
}