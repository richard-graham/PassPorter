package com.example.passporter.data.mapper

import android.os.Build
import androidx.annotation.RequiresApi
import com.example.passporter.data.local.entity.AccessibilityEntity
import com.example.passporter.data.local.entity.AmenitiesEntity
import com.example.passporter.data.local.entity.BorderPointEntity
import com.example.passporter.data.local.entity.ClosurePeriodEntity
import com.example.passporter.data.local.entity.CurrencyExchangeEntity
import com.example.passporter.data.local.entity.FacilitiesEntity
import com.example.passporter.data.local.entity.InsuranceServicesEntity
import com.example.passporter.data.local.entity.OperatingHoursEntity
import com.example.passporter.data.local.entity.RoadConditionEntity
import com.example.passporter.data.local.entity.RoadConditionsEntity
import com.example.passporter.data.local.entity.SeasonalHoursEntity
import com.example.passporter.data.local.entity.ServicesEntity
import com.example.passporter.data.local.entity.TelecomServicesEntity
import com.example.passporter.data.local.entity.TrafficTypesEntity
import com.example.passporter.data.remote.model.BorderPointDto
import com.example.passporter.domain.entity.Accessibility
import com.example.passporter.domain.entity.Amenities
import com.example.passporter.domain.entity.BorderPoint
import com.example.passporter.domain.entity.BorderStatus
import com.example.passporter.domain.entity.CurrencyExchange
import com.example.passporter.domain.entity.Facilities
import com.example.passporter.domain.entity.InsuranceServices
import com.example.passporter.domain.entity.OperatingHours
import com.example.passporter.domain.entity.RoadCondition
import com.example.passporter.domain.entity.RoadConditions
import com.example.passporter.domain.entity.SeasonalHours
import com.example.passporter.domain.entity.Services
import com.example.passporter.domain.entity.TelecomServices
import com.example.passporter.domain.entity.TrafficTypes
import java.time.LocalDate
import javax.inject.Inject

class BorderPointMapper @Inject constructor() {
    fun toDto(domain: BorderPoint): BorderPointDto =
        BorderPointDto(
            id = domain.id,
            name = domain.name,
            latitude = domain.latitude,
            longitude = domain.longitude,
            countryA = domain.countryA,
            countryB = domain.countryB,
            status = domain.status.name,
            lastUpdate = domain.lastUpdate,
            createdBy = domain.createdBy,
            description = domain.description
        )

    fun toDomain(dto: BorderPointDto): BorderPoint =
        BorderPoint(
            id = dto.id,
            name = dto.name,
            nameEnglish = dto.nameEnglish,
            latitude = dto.latitude,
            longitude = dto.longitude,
            countryA = dto.countryA,
            countryB = dto.countryB,
            status = BorderStatus.valueOf(dto.status),
            lastUpdate = dto.lastUpdate,
            createdBy = dto.createdBy,
            description = dto.description,
            borderType = dto.borderType,
            crossingType = dto.crossingType,
            sourceId = dto.sourceId,
            dataSource = dto.dataSource,
            operatingHours = OperatingHours(
                regular = dto.operatingHours?.regular
            ),
            operatingAuthority = dto.operatingAuthority,
            accessibility = Accessibility(
                trafficTypes = TrafficTypes(
                    pedestrian = dto.accessibility?.trafficTypes?.pedestrian,
                    bicycle = dto.accessibility?.trafficTypes?.bicycle,
                    motorcycle = dto.accessibility?.trafficTypes?.motorcycle,
                    car = dto.accessibility?.trafficTypes?.car,
                    rv = dto.accessibility?.trafficTypes?.rv,
                    truck = dto.accessibility?.trafficTypes?.truck,
                    bus = dto.accessibility?.trafficTypes?.bus,
                ),
                roadConditions = RoadConditions(
                    approaching = RoadCondition(
                        type = dto.accessibility?.roadConditions?.approaching?.type,
                        condition = dto.accessibility?.roadConditions?.approaching?.condition
                    ),
                    departing = RoadCondition(
                        type = dto.accessibility?.roadConditions?.departing?.type,
                        condition = dto.accessibility?.roadConditions?.departing?.condition
                    )
                )
            ),
            facilities = Facilities(
                amenities = Amenities(
                    restrooms = dto.facilities?.amenities?.restrooms,
                    food = dto.facilities?.amenities?.food,
                    water = dto.facilities?.amenities?.water,
                    shelter = dto.facilities?.amenities?.shelter,
                    wifi = dto.facilities?.amenities?.wifi,
                    parking = dto.facilities?.amenities?.parking,
                ),
                services = Services(
                    currencyExchange = CurrencyExchange(
                        available = dto.facilities?.services?.currencyExchange?.available
                    ),
                    dutyFree = dto.facilities?.services?.dutyFree,
                    insurance = InsuranceServices(
                        available = dto.facilities?.services?.insurance?.available,
                        types = dto.facilities?.services?.insurance?.types ?: emptyList()
                    ),
                    storage = dto.facilities?.services?.storage,
                    telecommunications = TelecomServices(
                        available = dto.facilities?.services?.telecommunications?.available,
                        operators = dto.facilities?.services?.telecommunications?.operators
                            ?: emptyList(),
                        hasSimCards = dto.facilities?.services?.telecommunications?.hasSimCards
                    )
                )
            )
        )

    @RequiresApi(Build.VERSION_CODES.O)
    fun toEntity(domain: BorderPoint): BorderPointEntity =
        BorderPointEntity(
            id = domain.id,
            name = domain.name,
            nameEnglish = domain.nameEnglish,
            latitude = domain.latitude,
            longitude = domain.longitude,
            countryA = domain.countryA,
            countryB = domain.countryB,
            status = domain.status.name,
            lastUpdate = domain.lastUpdate,
            createdBy = domain.createdBy,
            description = domain.description,
            borderType = domain.borderType,
            crossingType = domain.crossingType,
            sourceId = domain.sourceId,
            dataSource = domain.dataSource,
            operatingHours = domain.operatingHours?.let { hours ->
                OperatingHoursEntity(
                    regular = hours.regular,
                    summerHours = hours.summerHours?.let { summer ->
                        SeasonalHoursEntity(
                            schedule = summer.schedule,
                            startDate = summer.startDate.toEpochDay(),
                            endDate = summer.endDate.toEpochDay()
                        )
                    },
                    winterHours = hours.winterHours?.let { winter ->
                        SeasonalHoursEntity(
                            schedule = winter.schedule,
                            startDate = winter.startDate.toEpochDay(),
                            endDate = winter.endDate.toEpochDay()
                        )
                    },
                    closurePeriods = hours.closurePeriods.map { period ->
                        ClosurePeriodEntity(
                            startDate = period.startDate.toEpochDay(),
                            endDate = period.endDate.toEpochDay(),
                            reason = period.reason,
                            isRecurring = period.isRecurring
                        )
                    }
                )
            },
            operatingAuthority = domain.operatingAuthority,
            accessibility = domain.accessibility.let { access ->
                AccessibilityEntity(
                    trafficTypes = access.trafficTypes.let { traffic ->
                        TrafficTypesEntity(
                            pedestrian = traffic.pedestrian,
                            bicycle = traffic.bicycle,
                            motorcycle = traffic.motorcycle,
                            car = traffic.car,
                            rv = traffic.rv,
                            truck = traffic.truck,
                            bus = traffic.bus
                        )
                    },
                    roadConditions = access.roadConditions.let { roads ->
                        RoadConditionsEntity(
                            approaching = roads.approaching.let { approach ->
                                RoadConditionEntity(
                                    type = approach.type,
                                    condition = approach.condition
                                )
                            },
                            departing = roads.departing.let { depart ->
                                RoadConditionEntity(
                                    type = depart.type,
                                    condition = depart.condition
                                )
                            }
                        )
                    }
                )
            },
            facilities = domain.facilities.let { fac ->
                FacilitiesEntity(
                    amenities = fac.amenities.let { amen ->
                        AmenitiesEntity(
                            restrooms = amen.restrooms,
                            food = amen.food,
                            water = amen.water,
                            shelter = amen.shelter,
                            wifi = amen.wifi,
                            parking = amen.parking
                        )
                    },
                    services = fac.services.let { serv ->
                        ServicesEntity(
                            currencyExchange = serv.currencyExchange.let { curr ->
                                CurrencyExchangeEntity(
                                    available = curr.available
                                )
                            },
                            dutyFree = serv.dutyFree,
                            insurance = serv.insurance.let { ins ->
                                InsuranceServicesEntity(
                                    available = ins.available,
                                    types = ins.types
                                )
                            },
                            storage = serv.storage,
                            telecommunications = serv.telecommunications.let { tel ->
                                TelecomServicesEntity(
                                    available = tel.available,
                                    operators = tel.operators,
                                    hasSimCards = tel.hasSimCards
                                )
                            }
                        )
                    }
                )
            }
        )

    @RequiresApi(Build.VERSION_CODES.O)
    fun toDomain(entity: BorderPointEntity): BorderPoint =
        BorderPoint(
            id = entity.id,
            name = entity.name,
            nameEnglish = entity.nameEnglish,
            latitude = entity.latitude,
            longitude = entity.longitude,
            countryA = entity.countryA,
            countryB = entity.countryB,
            status = BorderStatus.valueOf(entity.status),
            lastUpdate = entity.lastUpdate,
            createdBy = entity.createdBy,
            description = entity.description,
            borderType = entity.borderType,
            crossingType = entity.crossingType,
            sourceId = entity.sourceId,
            dataSource = entity.dataSource,
            operatingHours = entity.operatingHours?.let { hours ->
                OperatingHours(
                    regular = hours.regular,
                    timezone = hours.timezone,
                    summerHours = hours.summerHours?.let {
                        SeasonalHours(
                            schedule = it.schedule,
                            startDate = it.startDate.toLocalDate(),
                            endDate = it.endDate.toLocalDate()
                        )
                    },
                    winterHours = hours.winterHours?.let {
                        SeasonalHours(
                            schedule = it.schedule,
                            startDate = it.startDate.toLocalDate(),
                            endDate = it.endDate.toLocalDate()
                        )
                    },
                )
            },
            operatingAuthority = entity.operatingAuthority,
            accessibility = entity.accessibility.let { access ->
                Accessibility(
                    trafficTypes = access.trafficTypes.let { traffic ->
                        TrafficTypes(
                            pedestrian = traffic.pedestrian,
                            bicycle = traffic.bicycle,
                            motorcycle = traffic.motorcycle,
                            car = traffic.car,
                            rv = traffic.rv,
                            truck = traffic.truck,
                            bus = traffic.bus
                        )
                    },
                    roadConditions = access.roadConditions.let { roads ->
                        RoadConditions(
                            approaching = roads.approaching.let { approach ->
                                RoadCondition(
                                    type = approach.type,
                                    condition = approach.condition
                                )
                            },
                            departing = roads.departing.let { depart ->
                                RoadCondition(
                                    type = depart.type,
                                    condition = depart.condition
                                )
                            }
                        )
                    }
                )
            },
            facilities = entity.facilities.let { fac ->
                Facilities(
                    amenities = fac.amenities.let { amen ->
                        Amenities(
                            restrooms = amen.restrooms,
                            food = amen.food,
                            water = amen.water,
                            shelter = amen.shelter,
                            wifi = amen.wifi,
                            parking = amen.parking
                        )
                    },
                    services = fac.services.let { serv ->
                        Services(
                            currencyExchange = CurrencyExchange(
                                available = serv.currencyExchange.available
                            ),
                            dutyFree = serv.dutyFree,
                            insurance = serv.insurance.let { ins ->
                                InsuranceServices(
                                    available = ins.available,
                                    types = ins.types ?: emptyList()
                                )
                            },
                            storage = serv.storage,
                            telecommunications = serv.telecommunications.let { tel ->
                                TelecomServices(
                                    available = tel.available,
                                    operators = tel.operators ?: emptyList(),
                                    hasSimCards = tel.hasSimCards
                                )
                            }
                        )
                    }
                )
            }
        )
}

@RequiresApi(Build.VERSION_CODES.O)
private fun Long.toLocalDate(): LocalDate {
    // The value is already in epoch days, not milliseconds
    return LocalDate.ofEpochDay(this)
}