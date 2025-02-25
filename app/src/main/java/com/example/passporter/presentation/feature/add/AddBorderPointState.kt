package com.example.passporter.presentation.feature.add

import com.example.passporter.domain.entity.Accessibility
import com.example.passporter.domain.entity.Facilities
import com.example.passporter.domain.entity.OperatingHours
import com.example.passporter.domain.entity.RoadCondition
import com.example.passporter.domain.entity.RoadConditions
import com.example.passporter.domain.entity.TrafficTypes

sealed class AddBorderPointState {
    open val additionComplete: Boolean = false

    data object Loading : AddBorderPointState()
    data class Error(val message: String) : AddBorderPointState()
    data class Input(
        val id: String? = null,
        val basicInfo: BasicBorderInfo = BasicBorderInfo(),
        val operatingHours: OperatingHours = OperatingHours(null),
        val accessibility: Accessibility = Accessibility(
            TrafficTypes(),
            RoadConditions(RoadCondition(null, null), RoadCondition(null, null))
        ),
        val facilities: Facilities = Facilities(),
        val latitude: Double = 0.0,
        val longitude: Double = 0.0,
        val createdBy: String? = null,
        override val additionComplete: Boolean = false
    ) : AddBorderPointState() {
        fun isValid(): Boolean = with(basicInfo) {
            name.isNotBlank() && countryA.isNotBlank() && countryB.isNotBlank() &&
                    latitude != 0.0 && longitude != 0.0 && status.isNotBlank()
        }
    }
}

data class BasicBorderInfo(
    val name: String = "",
    val nameEnglish: String? = null,
    val countryA: String = "",
    val countryB: String = "",
    val status: String = "OPEN",
    val statusComment: String = "",
    val restrictionDetails: String = "",
    val description: String = "",
    val borderType: String? = null,
    val crossingType: String? = null,
    val operatingAuthority: String? = null
)