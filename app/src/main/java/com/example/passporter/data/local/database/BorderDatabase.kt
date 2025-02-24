package com.example.passporter.data.local.database

import android.content.Context
import android.util.Log
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.passporter.data.local.dao.BorderDao
import com.example.passporter.data.local.entity.BorderPointEntity
import com.example.passporter.data.local.entity.BorderUpdateEntity
import com.example.passporter.data.local.entity.ClosurePeriodEntity
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

@Database(
    entities = [BorderPointEntity::class, BorderUpdateEntity::class],
    version = 2
)
@TypeConverters(Converters::class)
abstract class BorderDatabase : RoomDatabase() {
    abstract fun borderDao(): BorderDao

    companion object {
        // Add migration here
        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                Log.d("Migration", "Starting migration from 1 to 2")
                // Create temporary table with new schema
                db.execSQL("""
                    CREATE TABLE border_points_new (
                        id TEXT NOT NULL PRIMARY KEY,
                        name TEXT NOT NULL,
                        nameEnglish TEXT,
                        latitude REAL NOT NULL,
                        longitude REAL NOT NULL,
                        countryA TEXT NOT NULL,
                        countryB TEXT NOT NULL,
                        status TEXT NOT NULL,
                        lastUpdate INTEGER NOT NULL,
                        createdBy TEXT NOT NULL,
                        description TEXT NOT NULL,
                        borderType TEXT,
                        crossingType TEXT,
                        sourceId TEXT NOT NULL,
                        dataSource TEXT NOT NULL,
                        operating_regular TEXT,
                        operating_timezone TEXT DEFAULT 'UTC',
                        operating_summer_schedule TEXT,
                        operating_summer_startDate INTEGER,
                        operating_summer_endDate INTEGER,
                        operating_winter_schedule TEXT,
                        operating_winter_startDate INTEGER,
                        operating_winter_endDate INTEGER,
                        operating_closurePeriods TEXT DEFAULT '[]',
                        operatingAuthority TEXT,
                        accessibility_traffic_pedestrian INTEGER,
                        accessibility_traffic_bicycle INTEGER,
                        accessibility_traffic_motorcycle INTEGER,
                        accessibility_traffic_car INTEGER,
                        accessibility_traffic_rv INTEGER,
                        accessibility_traffic_truck INTEGER,
                        accessibility_traffic_bus INTEGER,
                        accessibility_road_approaching_type TEXT,
                        accessibility_road_approaching_condition TEXT,
                        accessibility_road_departing_type TEXT,
                        accessibility_road_departing_condition TEXT,
                        facilities_amenities_restrooms INTEGER,
                        facilities_amenities_food INTEGER,
                        facilities_amenities_water INTEGER,
                        facilities_amenities_shelter INTEGER,
                        facilities_amenities_wifi INTEGER,
                        facilities_amenities_parking INTEGER,
                        facilities_services_dutyFree INTEGER,
                        facilities_services_storage INTEGER,
                        facilities_services_currency_available INTEGER,
                        facilities_services_insurance_available INTEGER,
                        facilities_services_insurance_types TEXT,
                        facilities_services_telecom_available INTEGER,
                        facilities_services_telecom_operators TEXT,
                        facilities_services_telecom_hasSimCards INTEGER
                    )
                """)

                // Copy data from old table
                db.execSQL("""
                    INSERT INTO border_points_new (
                        id, name, nameEnglish, latitude, longitude, countryA, countryB,
                        status, lastUpdate, createdBy, description, borderType, crossingType,
                        sourceId, dataSource, operating_regular, operatingAuthority,
                        accessibility_traffic_pedestrian, accessibility_traffic_bicycle,
                        accessibility_traffic_motorcycle, accessibility_traffic_car,
                        accessibility_traffic_rv, accessibility_traffic_truck,
                        accessibility_traffic_bus, accessibility_road_approaching_type,
                        accessibility_road_approaching_condition, accessibility_road_departing_type,
                        accessibility_road_departing_condition, facilities_amenities_restrooms,
                        facilities_amenities_food, facilities_amenities_water,
                        facilities_amenities_shelter, facilities_amenities_wifi,
                        facilities_amenities_parking, facilities_services_dutyFree,
                        facilities_services_storage, facilities_services_currency_available,
                        facilities_services_insurance_available, facilities_services_insurance_types,
                        facilities_services_telecom_available, facilities_services_telecom_operators,
                        facilities_services_telecom_hasSimCards
                    )
                    SELECT *
                    FROM border_points
                """)

                // Drop old table and rename new one
                db.execSQL("DROP TABLE border_points")
                db.execSQL("ALTER TABLE border_points_new RENAME TO border_points")

                Log.d("Migration", "Completed migration from 1 to 2")
            }
        }

        fun getDatabase(context: Context): BorderDatabase {
            return Room.databaseBuilder(
                context.applicationContext,
                BorderDatabase::class.java,
                "border_database"
            )
                .addMigrations(MIGRATION_1_2)  // Add migration to builder
                .build()
        }
    }
}

class Converters {
    private val gson = Gson()

    @TypeConverter
    fun fromStringList(value: String?): List<String>? {
        return value?.split(",")?.map { it.trim() }
    }

    @TypeConverter
    fun stringListToString(list: List<String>?): String? {
        return list?.joinToString(",")
    }

    @TypeConverter
    fun fromClosurePeriods(value: List<ClosurePeriodEntity>): String {
        return gson.toJson(value)
    }

    @TypeConverter
    fun toClosurePeriods(value: String): List<ClosurePeriodEntity> {
        val listType = object : TypeToken<List<ClosurePeriodEntity>>() {}.type
        return gson.fromJson(value, listType)
    }
}