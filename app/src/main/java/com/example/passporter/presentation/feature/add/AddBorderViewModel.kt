package com.example.passporter.presentation.feature.add

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.passporter.domain.entity.Accessibility
import com.example.passporter.domain.entity.BorderPoint
import com.example.passporter.domain.entity.BorderStatus
import com.example.passporter.domain.entity.Facilities
import com.example.passporter.domain.entity.OperatingHours
import com.example.passporter.domain.usecase.border.AddBorderPointUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class AddBorderPointViewModel @Inject constructor(
    private val addBorderPointUseCase: AddBorderPointUseCase,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _state = MutableStateFlow<AddBorderPointState>(AddBorderPointState.Input())
    val state: StateFlow<AddBorderPointState> = _state.asStateFlow()

    private val latitude: Double = savedStateHandle.get<Float>("lat")?.toDouble()
        ?: throw IllegalArgumentException("Latitude is required")
    private val longitude: Double = savedStateHandle.get<Float>("lng")?.toDouble()
        ?: throw IllegalArgumentException("Longitude is required")

    fun updateBasicInfo(basicInfo: BasicBorderInfo) {
        _state.update { current ->
            if (current is AddBorderPointState.Input) {
                current.copy(basicInfo = basicInfo)
            } else current
        }
    }

    fun updateOperatingHours(operatingHours: OperatingHours) {
        _state.update { current ->
            if (current is AddBorderPointState.Input) {
                current.copy(operatingHours = operatingHours)
            } else current
        }
    }

    fun updateAccessibility(accessibility: Accessibility) {
        _state.update { current ->
            if (current is AddBorderPointState.Input) {
                current.copy(accessibility = accessibility)
            } else current
        }
    }

    fun updateFacilities(facilities: Facilities) {
        _state.update { current ->
            if (current is AddBorderPointState.Input) {
                current.copy(facilities = facilities)
            } else current
        }
    }

    fun submitBorderPoint() {
        val currentState = _state.value as? AddBorderPointState.Input ?: return

        if (!currentState.isValid()) {
            _state.value = AddBorderPointState.Error("Please fill in all required fields")
            return
        }

        viewModelScope.launch {
            _state.value = AddBorderPointState.Loading

            val borderPoint = BorderPoint(
                id = UUID.randomUUID().toString(),
                name = currentState.basicInfo.name,
                nameEnglish = currentState.basicInfo.nameEnglish,
                latitude = latitude,
                longitude = longitude,
                countryA = currentState.basicInfo.countryA,
                countryB = currentState.basicInfo.countryB,
                status = BorderStatus.OPEN,
                lastUpdate = System.currentTimeMillis(),
                createdBy = "user", // Should come from auth system
                description = currentState.basicInfo.description,
                borderType = currentState.basicInfo.borderType,
                crossingType = currentState.basicInfo.crossingType,
                sourceId = "manual",
                dataSource = "user",
                operatingHours = currentState.operatingHours,
                operatingAuthority = currentState.basicInfo.operatingAuthority,
                accessibility = currentState.accessibility,
                facilities = currentState.facilities
            )

            addBorderPointUseCase(borderPoint)
                .onSuccess {
                    _state.value = AddBorderPointState.Input(additionComplete = true)
                }
                .onFailure { error ->
                    _state.value = AddBorderPointState.Error(error.message ?: "Unknown error occurred")
                }
        }
    }
}