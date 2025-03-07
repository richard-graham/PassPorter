package com.example.passporter.presentation.feature.add

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.passporter.domain.entity.Accessibility
import com.example.passporter.domain.entity.BorderPoint
import com.example.passporter.domain.entity.BorderStatus
import com.example.passporter.domain.entity.Facilities
import com.example.passporter.domain.entity.OperatingHours
import com.example.passporter.domain.repository.AuthRepository
import com.example.passporter.domain.usecase.border.AddBorderPointUseCase
import com.example.passporter.domain.usecase.border.DeleteBorderPointUseCase
import com.example.passporter.domain.usecase.border.GetBorderPointDetailsUseCase
import com.example.passporter.domain.usecase.border.UpdateBorderPointUseCase
import com.example.passporter.presentation.util.ResultUtil
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class AddBorderPointViewModel @Inject constructor(
    private val updateBorderPointUseCase: UpdateBorderPointUseCase,
    private val getBorderPointDetailsUseCase: GetBorderPointDetailsUseCase,
    private val addBorderPointUseCase: AddBorderPointUseCase,
    private val deleteBorderPointUseCase: DeleteBorderPointUseCase,
    private val authRepository: AuthRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _state = MutableStateFlow<AddBorderPointState>(AddBorderPointState.Input())
    val state: StateFlow<AddBorderPointState> = _state.asStateFlow()

    private var latitude: Double = savedStateHandle.get<Float>("lat")?.toDouble() ?: 0.0
    private var longitude: Double = savedStateHandle.get<Float>("lng")?.toDouble() ?: 0.0
    private val borderId: String? = savedStateHandle["borderId"]
    private val isEditing = borderId != null

    private var currentUser: String = ""

    init {
        viewModelScope.launch {
            val user = authRepository.currentUser.first()
            currentUser = user?.id ?: "unknown_user"
        }

        // Initialize location from savedStateHandle if available
        val initialLat = savedStateHandle.get<Float>("lat")?.toDouble() ?: 0.0
        val initialLng = savedStateHandle.get<Float>("lng")?.toDouble() ?: 0.0

        if (isEditing) {
            // For editing: load entire border point from database
            loadExistingBorderPoint()
        } else if (initialLat != 0.0 && initialLng != 0.0) {
            // For new border point with initial location: update state with location
            _state.update { current ->
                if (current is AddBorderPointState.Input) {
                    current.copy(latitude = initialLat, longitude = initialLng)
                } else current
            }
        }
    }

    private fun loadExistingBorderPoint() {
        viewModelScope.launch {
            _state.value = AddBorderPointState.Loading
            borderId?.let { id ->
                when (val result = getBorderPointDetailsUseCase(id)) {
                    is ResultUtil.Success -> {
                        val borderPoint = result.data

                        latitude = borderPoint.latitude
                        longitude = borderPoint.longitude

                        _state.value = AddBorderPointState.Input(
                            id = id,
                            basicInfo = BasicBorderInfo(
                                name = borderPoint.name,
                                nameEnglish = borderPoint.nameEnglish,
                                countryA = borderPoint.countryA,
                                countryB = borderPoint.countryB,
                                description = borderPoint.description,
                                borderType = borderPoint.borderType,
                                crossingType = borderPoint.crossingType,
                                operatingAuthority = borderPoint.operatingAuthority,
                                status = borderPoint.status.name,
                                statusComment = borderPoint.statusComment ?: ""
                            ),
                            operatingHours = borderPoint.operatingHours ?: OperatingHours("", ""),
                            accessibility = borderPoint.accessibility,
                            facilities = borderPoint.facilities,
                            createdBy = borderPoint.createdBy,
                            latitude = borderPoint.latitude,
                            longitude = borderPoint.longitude
                        )
                    }

                    is ResultUtil.Error -> {
                        _state.value = AddBorderPointState.Error("Failed to load border point")
                    }
                }
            }
        }
    }

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

    fun updateLocation(latitude: Double, longitude: Double) {
        _state.update { current ->
            if (current is AddBorderPointState.Input) {
                current.copy(latitude = latitude, longitude = longitude)
            } else current
        }
    }

    fun submitBorderPoint() {
        val currentState = _state.value as? AddBorderPointState.Input ?: return

        if (!currentState.isValid()) {
            _state.value = AddBorderPointState.Error("Please fill in all required fields")
            return
        }

        if (latitude == 0.0 && longitude == 0.0) {
            _state.value = AddBorderPointState.Error("No location provided")
            return
        }


        viewModelScope.launch {
            _state.value = AddBorderPointState.Loading

            val borderPoint = BorderPoint(
                id = borderId ?: UUID.randomUUID().toString(),
                name = currentState.basicInfo.name,
                nameEnglish = currentState.basicInfo.nameEnglish,
                latitude = currentState.latitude,
                longitude = currentState.longitude,
                countryA = currentState.basicInfo.countryA,
                countryB = currentState.basicInfo.countryB,
                status = BorderStatus.valueOf(currentState.basicInfo.status),
                statusComment = if (currentState.basicInfo.status != "OPEN")
                    currentState.basicInfo.statusComment.ifBlank { null } else null,
                lastUpdate = System.currentTimeMillis(),
                createdBy = if (isEditing) {
                    when (val existingPointResult = getBorderPointDetailsUseCase(borderId!!)) {
                        is ResultUtil.Success -> existingPointResult.data.createdBy
                        is ResultUtil.Error -> currentUser
                    }
                } else {
                    currentUser
                },
                lastUpdatedBy = currentUser,
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

            val result = if (isEditing) {
                updateBorderPointUseCase(borderPoint)
            } else {
                addBorderPointUseCase(borderPoint)
            }

            when (result) {
                is ResultUtil.Success<*> -> {
                    _state.value = AddBorderPointState.Input(
                        additionComplete = true,
                        id = borderPoint.id,
                        basicInfo = currentState.basicInfo,
                        operatingHours = currentState.operatingHours,
                        accessibility = currentState.accessibility,
                        facilities = currentState.facilities,
                        latitude = currentState.latitude,
                        longitude = currentState.longitude,
                    )
                }

                is ResultUtil.Error -> {
                    _state.value = AddBorderPointState.Error(
                        result.exception.message ?: "Unknown error occurred"
                    )
                }
            }
        }
    }

    fun deleteBorderPoint() {
        viewModelScope.launch {
            val inputState = (state.value as? AddBorderPointState.Input) ?: return@launch
            val id = inputState.id ?: return@launch

            _state.value = AddBorderPointState.Loading

            val result = deleteBorderPointUseCase(id)

            when (result) {
                is ResultUtil.Success -> {
                    _state.value = AddBorderPointState.Input(additionComplete = true)
                }
                is ResultUtil.Error -> {
                    _state.value = AddBorderPointState.Error(
                        result.exception.message ?: "Failed to delete border point"
                    )
                }
            }
        }
    }
}