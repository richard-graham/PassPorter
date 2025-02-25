package com.example.passporter.data.local.database

import android.content.Context
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
    version = 5
)
@TypeConverters(Converters::class)
abstract class BorderDatabase : RoomDatabase() {
    abstract fun borderDao(): BorderDao

    companion object {
        // Add migration here
        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                // Create temporary table with new schema
                db.execSQL(
                    """
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
                """
                )

                // Copy data from old table
                db.execSQL(
                    """
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
                """
                )

                // Drop old table and rename new one
                db.execSQL("DROP TABLE border_points")
                db.execSQL("ALTER TABLE border_points_new RENAME TO border_points")
            }
        }

        private val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(db: SupportSQLiteDatabase) {
                // Add the new restrictionDetails column
                db.execSQL("ALTER TABLE border_points ADD COLUMN restrictionDetails TEXT")

                // Update existing RESTRICTED status borders with null for restrictionDetails
                db.execSQL(
                    """
                    UPDATE border_points
                    SET restrictionDetails = NULL
                    WHERE status = 'RESTRICTED'
                """
                )
            }
        }

        // New migration from version 3 to 4 to replace restrictionDetails with statusComment
        private val MIGRATION_3_4 = object : Migration(3, 4) {
            override fun migrate(db: SupportSQLiteDatabase) {
                // Step 1: Add the new statusComment column
                db.execSQL("ALTER TABLE border_points ADD COLUMN statusComment TEXT")

                // Step 2: Copy data from restrictionDetails to statusComment for RESTRICTED status
                db.execSQL(
                    """
            UPDATE border_points
            SET statusComment = restrictionDetails
            WHERE status = 'RESTRICTED' AND restrictionDetails IS NOT NULL
        """
                )

                // Note: We cannot remove columns in SQLite directly,
                // so restrictionDetails will remain but won't be used by the app
            }
        }

        private val MIGRATION_4_5 = object : Migration(4, 5) {
            override fun migrate(db: SupportSQLiteDatabase) {
                // Add the deleted fields
                db.execSQL("ALTER TABLE border_points ADD COLUMN deleted INTEGER NOT NULL DEFAULT 0")
                db.execSQL("ALTER TABLE border_points ADD COLUMN deletedAt INTEGER DEFAULT NULL")
                db.execSQL("ALTER TABLE border_points ADD COLUMN deletedBy TEXT DEFAULT NULL")
            }
        }

        fun getDatabase(context: Context): BorderDatabase {
            return Room.databaseBuilder(
                context.applicationContext,
                BorderDatabase::class.java,
                "border_database"
            )
                .addMigrations(MIGRATION_1_2, MIGRATION_2_3, MIGRATION_3_4, MIGRATION_4_5)
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