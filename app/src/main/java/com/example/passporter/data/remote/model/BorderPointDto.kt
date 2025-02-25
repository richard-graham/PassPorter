package com.example.passporter.data.remote.model

import androidx.annotation.Keep

data class BorderPointDto(
    val id: String = "",
    val name: String = "",
    val nameEnglish: String? = null,
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val countryA: String = "",
    val countryB: String = "",
    val status: String = "",
    val statusComment: String? = null,
    val lastUpdate: Long = 0L,
    val createdBy: String = "",
    val description: String = "",
    val borderType: String? = null,
    val crossingType: String? = null,
    val sourceId: String = "",
    val dataSource: String = "",
    val operatingHours: OperatingHours? = null,
    val operatingAuthority: String? = null,
    val accessibility: Accessibility? = null,
    val facilities: Facilities? = null,
)

@Keep
data class OperatingHours(
    val regular: String? = null,
    val covid19: String? = null
)

@Keep
data class Facilities(
    val amenities: Amenities = Amenities(),
    val services: Services = Services()
)

@Keep
data class Amenities(
    val restrooms: Boolean = false,
    val food: Boolean = false,
    val water: Boolean = false,
    val shelter: Boolean = false,
    val wifi: Boolean = false,
    val parking: Boolean = false
)

@Keep
data class Services(
    val currencyExchange: CurrencyExchange = CurrencyExchange(),
    val dutyFree: Boolean = false,
    val insurance: InsuranceServices? = null,
    val storage: Boolean = false,
    val telecommunications: TelecomServices? = null
)

@Keep
data class InsuranceServices(
    val available: Boolean = false,
    val types: List<String> = emptyList()
)

@Keep
data class TelecomServices(
    val available: Boolean = false,
    val operators: List<String> = emptyList(),
    val hasSimCards: Boolean = false
)

@Keep
data class CurrencyExchange(
    val available: Boolean = false
)

@Keep
data class Accessibility(
    val trafficTypes: TrafficTypes = TrafficTypes(),
    val roadConditions: RoadConditions = RoadConditions()
)

@Keep
data class TrafficTypes(
    val pedestrian: Boolean = false,
    val bicycle: Boolean = false,
    val motorcycle: Boolean = false,
    val car: Boolean = false,
    val rv: Boolean = false,
    val truck: Boolean = false,
    val bus: Boolean = false
)

@Keep
data class RoadConditions(
    val approaching: RoadCondition = RoadCondition(),
    val departing: RoadCondition = RoadCondition(),
)

@Keep
data class RoadCondition(
    val type: String = "unknown",
    val condition: String = "unknown"
)