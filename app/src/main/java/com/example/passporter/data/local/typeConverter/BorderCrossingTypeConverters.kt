package com.example.passporter.data.local.typeConverter

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.ui.input.key.type
import androidx.room.TypeConverter
import com.example.passporter.data.local.entity.OperatingHoursEntity
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

class BorderCrossingTypeConverters {
    private val gson = Gson()
    @RequiresApi(Build.VERSION_CODES.O)
    private val formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME

    @RequiresApi(Build.VERSION_CODES.O)
    @TypeConverter
    fun fromMap(value: Map<String, LocalDateTime>): String {
        val gson = Gson()
        val mapOfString = value.mapValues { it.value.format(formatter) }
        return gson.toJson(mapOfString)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @TypeConverter
    fun toMap(value: String): Map<String, LocalDateTime> {
        val gson = Gson()
        val type = object : TypeToken<Map<String, String>>() {}.type
        val mapOfString = gson.fromJson<Map<String, String>>(value, type)
        return mapOfString.mapValues { LocalDateTime.parse(it.value, formatter) }
    }

    // List<String> converters for days of operation
    @TypeConverter
    fun fromStringList(value: List<String>): String {
        return gson.toJson(value)
    }

    @TypeConverter
    fun toStringList(value: String): List<String> {
        val listType = object : TypeToken<List<String>>() {}.type
        return gson.fromJson(value, listType)
    }

    // LocalDateTime converters for lastUpdated
    @RequiresApi(Build.VERSION_CODES.O)
    @TypeConverter
    fun fromLocalDateTime(dateTime: LocalDateTime): Long {
        return dateTime.toEpochSecond(ZoneOffset.UTC)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @TypeConverter
    fun toLocalDateTime(timestamp: Long): LocalDateTime {
        return LocalDateTime.ofEpochSecond(timestamp, 0, ZoneOffset.UTC)
    }

    // LocalTime converters for openTime and closeTime
    @TypeConverter
    fun fromLocalTime(time: LocalTime): String {
        return time.toString()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @TypeConverter
    fun toLocalTime(timeString: String): LocalTime {
        return LocalTime.parse(timeString)
    }

    // Operating Hours List converter
    @TypeConverter
    fun fromOperatingHoursList(value: List<OperatingHoursEntity>): String {
        return gson.toJson(value)
    }

    @TypeConverter
    fun toOperatingHoursList(value: String): List<OperatingHoursEntity> {
        val listType = object : TypeToken<List<OperatingHoursEntity>>() {}.type
        return gson.fromJson(value, listType)
    }
}