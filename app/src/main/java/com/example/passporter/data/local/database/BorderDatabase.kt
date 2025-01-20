package com.example.passporter.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.passporter.data.local.dao.BorderDao
import com.example.passporter.data.local.entity.BorderPointEntity
import com.example.passporter.data.local.entity.BorderUpdateEntity

//@Database(entities = [BorderCrossingEntity::class, OperatingHoursEntity::class], version = 1)
//@TypeConverters(BorderCrossingTypeConverters::class)
//abstract class BorderDatabase : RoomDatabase() {
//    abstract val borderCrossingDao: BorderCrossingDao
//}

@Database(
    entities = [BorderPointEntity::class, BorderUpdateEntity::class],
    version = 1
)
abstract class BorderDatabase : RoomDatabase() {
    abstract fun borderDao(): BorderDao
}