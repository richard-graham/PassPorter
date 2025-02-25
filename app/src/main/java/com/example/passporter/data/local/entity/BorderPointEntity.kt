package com.example.passporter.data.local.entity

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "border_points")
data class BorderPointEntity(
    @PrimaryKey val id: String,
    val name: String,
    val nameEnglish: String?,
    val latitude: Double,
    val longitude: Double,
    val countryA: String,
    val countryB: String,
    val status: String,
    val statusComment: String?,
    val lastUpdate: Long,
    val createdBy: String,
    val lastUpdatedBy: String?,
    val description: String,
    val borderType: String?,
    val crossingType: String?,
    val sourceId: String,
    val dataSource: String,
    @Embedded(prefix = "operating_")
    val operatingHours: OperatingHoursEntity?,
    val operatingAuthority: String?,
    @Embedded(prefix = "accessibility_")
    val accessibility: AccessibilityEntity,
    @Embedded(prefix = "facilities_")
    val facilities: FacilitiesEntity,
    val deleted: Boolean = false,
    val deletedAt: Long? = null,
    val deletedBy: String? = null
)

// Existing entity classes remain unchanged
data class AccessibilityEntity(
    @Embedded(prefix = "traffic_")
    val trafficTypes: TrafficTypesEntity,
    @Embedded(prefix = "road_")
    val roadConditions: RoadConditionsEntity
)

data class TrafficTypesEntity(
    val pedestrian: Boolean?,
    val bicycle: Boolean?,
    val motorcycle: Boolean?,
    val car: Boolean?,
    val rv: Boolean?,
    val truck: Boolean?,
    val bus: Boolean?
)

data class RoadConditionsEntity(
    @Embedded(prefix = "approaching_")
    val approaching: RoadConditionEntity,
    @Embedded(prefix = "departing_")
    val departing: RoadConditionEntity
)

data class RoadConditionEntity(
    val type: String?,
    val condition: String?
)

data class FacilitiesEntity(
    @Embedded(prefix = "amenities_")
    val amenities: AmenitiesEntity,
    @Embedded(prefix = "services_")
    val services: ServicesEntity
)

data class AmenitiesEntity(
    val restrooms: Boolean?,
    val food: Boolean?,
    val water: Boolean?,
    val shelter: Boolean?,
    val wifi: Boolean?,
    val parking: Boolean?
)

data class ServicesEntity(
    @Embedded(prefix = "currency_")
    val currencyExchange: CurrencyExchangeEntity,
    val dutyFree: Boolean?,
    @Embedded(prefix = "insurance_")
    val insurance: InsuranceServicesEntity,
    val storage: Boolean?,
    @Embedded(prefix = "telecom_")
    val telecommunications: TelecomServicesEntity
)

data class CurrencyExchangeEntity(
    val available: Boolean?
)

data class InsuranceServicesEntity(
    val available: Boolean?,
    val types: List<String>?
)

data class TelecomServicesEntity(
    val available: Boolean?,
    val operators: List<String>?,
    val hasSimCards: Boolean?
)

data class OperatingHoursEntity(
    val regular: String?,
    val timezone: String = "UTC",
    @Embedded(prefix = "summer_")
    val summerHours: SeasonalHoursEntity? = null,
    @Embedded(prefix = "winter_")
    val winterHours: SeasonalHoursEntity? = null,
    val closurePeriods: List<ClosurePeriodEntity> = emptyList()
)

data class SeasonalHoursEntity(
    val schedule: String,
    val startDate: Long,
    val endDate: Long
)

data class ClosurePeriodEntity(
    val startDate: Long,
    val endDate: Long,
    val reason: String?,
    val isRecurring: Boolean = false
)