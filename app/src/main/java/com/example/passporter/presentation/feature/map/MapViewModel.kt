package com.example.passporter.presentation.feature.map

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.passporter.domain.entity.BorderPoint
import com.example.passporter.domain.location.LocationException
import com.example.passporter.domain.location.LocationManager
import com.example.passporter.domain.location.LocationManagerImpl
import com.example.passporter.domain.usecase.border.AddBorderPointUseCase
import com.example.passporter.domain.usecase.border.GetBorderPointsUseCase
import com.example.passporter.presentation.util.ResultUtil
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MapViewModel @Inject constructor(
    private val getBorderPointsUseCase: GetBorderPointsUseCase,
    private val addBorderPointUseCase: AddBorderPointUseCase,
//    private val subscribeToBorderPointUseCase: SubscribeToBorderPointUseCase,
    private val locationManager: LocationManagerImpl
) : ViewModel() {

    private val _state = MutableStateFlow<MapScreenState>(MapScreenState.Loading)
    val state: StateFlow<MapScreenState> = _state.asStateFlow()

    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            try {
                if (!locationManager.hasLocationPermissions()) {
                    _state.value = MapScreenState.LocationPermissionRequired
                    return@launch
                }

                // Combine border points and location updates
                combine(
                    getBorderPointsUseCase(),
                    locationManager.getLocationUpdates().map { result ->
                        result.lastLocation?.let { LatLng(it.latitude, it.longitude) }
                    }
                ) { borderPointsResult, location ->
                    when (borderPointsResult) {
                        is ResultUtil.Success -> MapScreenState.Success(
                            borderPoints = borderPointsResult.data,
                            userLocation = location
                        )
                        is ResultUtil.Error -> MapScreenState.Error(
                            borderPointsResult.exception.message ?: "Failed to load border points"
                        )
                    }
                }.collect { state ->
                    _state.value = state
                }
            } catch (e: SecurityException) {
                _state.value = MapScreenState.LocationPermissionRequired
            } catch (e: Exception) {
                _state.value = MapScreenState.Error(e.message ?: "Unknown error occurred")
            }
        }
    }

    fun onPermissionGranted() {
        _state.value = MapScreenState.Loading
        loadData()
    }

    fun selectBorderPoint(borderPoint: BorderPoint) {
        val currentState = _state.value
        if (currentState is MapScreenState.Success) {
            _state.value = currentState.copy(selectedBorderPoint = borderPoint)
        }
    }

    fun clearSelectedBorderPoint() {
        val currentState = _state.value
        if (currentState is MapScreenState.Success) {
            _state.value = currentState.copy(selectedBorderPoint = null)
        }
    }

    private fun getBorderPoints() {
        viewModelScope.launch {
            getBorderPointsUseCase().collect { result ->
                _state.value = when (result) {
                    is ResultUtil.Success -> MapScreenState.Success(result.data)
                    is ResultUtil.Error -> MapScreenState.Error(result.exception.message ?: "Unknown error")
                }
            }
        }
    }
//
//    fun startLocationUpdates() {
//        viewModelScope.launch {
//            try {
//                if (!locationManager.hasLocationPermissions()) {
//                    _locationState.value = LocationState.RequiresPermission
//                    return@launch
//                }
//
//                locationManager.getLocationUpdates()
//                    .collect { result ->
//                        result.lastLocation?.let { location ->
//                            _locationState.value = LocationState.Success(
//                                LatLng(location.latitude, location.longitude)
//                            )
//                        }
//                    }
//            } catch (e: SecurityException) {
//                _locationState.value = LocationState.RequiresPermission
//            } catch (e: LocationException) {
//                _locationState.value = LocationState.Error(e.message ?: "Location error")
//            }
//        }
//    }

    fun addBorderPoint(
        name: String,
        location: LatLng,
        countryA: String,
        countryB: String,
        description: String,
        borderType: String?,
        crossingType: String?,
        sourceId: String,
        dataSource: String
    ) {
        viewModelScope.launch {
            val result = addBorderPointUseCase(
                name = name,
                latitude = location.latitude,
                longitude = location.longitude,
                countryA = countryA,
                countryB = countryB,
                description = description,
                borderType = borderType,
                crossingType = crossingType,
                sourceId = sourceId,
                dataSource = dataSource
            )
            when (result) {
                is ResultUtil.Success -> getBorderPoints()
                is ResultUtil.Error -> _state.value = MapScreenState.Error(
                    result.exception.message ?: "Failed to add border point"
                )
            }
        }
    }

    fun subscribeToBorderPoint(borderPointId: String) {
        viewModelScope.launch {
            val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return@launch
//            subscribeToBorderPointUseCase(userId, borderPointId)
        }
    }
}