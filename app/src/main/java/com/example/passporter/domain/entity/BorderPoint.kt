package com.example.passporter.domain.entity

data class BorderPoint(
    val id: String,
    val name: String,
    val nameEnglish: String? = null,
    val latitude: Double,
    val longitude: Double,
    val countryA: String,
    val countryB: String,
    val status: BorderStatus,
    val lastUpdate: Long,
    val createdBy: String,
    val description: String,
    val borderType: String?,
    val crossingType: String?,
    val sourceId: String,
    val dataSource: String,
    val operatingHours: OperatingHours?,
    val operatingAuthority: String?,
    val accessibility: Accessibility,
    val facilities: Facilities
)

data class OperatingHours(
    val regular: String?,
    val covid: String?,
)

data class Accessibility(
    val trafficTypes: TrafficTypes,
    val roadConditions: RoadConditions
)

data class TrafficTypes(
    val pedestrian: Boolean? = false,
    val bicycle: Boolean? = false,
    val motorcycle: Boolean? = false,
    val car: Boolean? = false,
    val rv: Boolean? = false,
    val truck: Boolean? = false,
    val bus: Boolean? = false
)

data class RoadConditions(
    val approaching: RoadCondition,
    val departing: RoadCondition,
)

data class RoadCondition(
    val type: String?,
    val condition: String?
)

data class Facilities(
    val amenities: Amenities = Amenities(),
    val services: Services = Services()
)

data class Amenities(
    val restrooms: Boolean? = false,
    val food: Boolean? = false,
    val water: Boolean? = false,
    val shelter: Boolean? = false,
    val wifi: Boolean? = false,
    val parking: Boolean? = false
)

data class Services(
    val currencyExchange: CurrencyExchange = CurrencyExchange(),
    val dutyFree: Boolean? = false,
    val insurance: InsuranceServices = InsuranceServices(),
    val storage: Boolean? = false,
    val telecommunications: TelecomServices = TelecomServices()
)

data class InsuranceServices(
    val available: Boolean? = false,
    val types: List<String> = emptyList()
)

data class TelecomServices(
    val available: Boolean? = false,
    val operators: List<String> = emptyList(),
    val hasSimCards: Boolean? = false
)

data class CurrencyExchange(
    val available: Boolean? = false
)