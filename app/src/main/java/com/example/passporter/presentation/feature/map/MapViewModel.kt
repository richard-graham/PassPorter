package com.example.passporter.presentation.feature.map

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.passporter.domain.entity.BorderPoint
import com.example.passporter.domain.location.LocationManagerImpl
import com.example.passporter.domain.usecase.border.GetBorderPointsByCoordinatesUseCase
import com.example.passporter.domain.usecase.border.SyncBorderPointsUseCase
import com.example.passporter.presentation.util.ResultUtil
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MapViewModel @Inject constructor(
    private val syncBorderPointsUseCase: SyncBorderPointsUseCase,
    private val getBorderPointsByCoordinatesUseCase: GetBorderPointsByCoordinatesUseCase,
    private val locationManager: LocationManagerImpl
) : ViewModel() {

    private val _state = MutableStateFlow<MapScreenState>(MapScreenState.Loading)
    val state: StateFlow<MapScreenState> = _state.asStateFlow()

    private val _selectedLocation = MutableStateFlow<LatLng?>(null)
    val selectedLocation: StateFlow<LatLng?> = _selectedLocation.asStateFlow()

    private val borderPointsFlow = MutableStateFlow<List<BorderPoint>>(emptyList())
    private val locationFlow = MutableStateFlow<LatLng?>(null)
    private val selectedBorderPointFlow = MutableStateFlow<BorderPoint?>(null)
    private val lastCameraPositionFlow = MutableStateFlow<CameraPosition?>(null)

    init {
        initialiseScreen()
    }

    private fun initialiseScreen() {
        viewModelScope.launch {
            try {
                if (!locationManager.hasLocationPermissions()) {
                    _state.value = MapScreenState.LocationPermissionRequired
                    return@launch
                }

                // First, wait for initial border points sync
                syncBorderPointsUseCase().collect { result ->
                    when (result) {
                        is ResultUtil.Success -> {
                            borderPointsFlow.value = result.data
                            // Now that we have border points, start collecting location updates
                            launch {
                                locationManager.getLocationUpdates().collect { locationResult ->
                                    locationFlow.value = locationResult.lastLocation?.let {
                                        LatLng(it.latitude, it.longitude)
                                    }
                                }
                            }

                            // Combine border points with location updates
                            combine(
                                borderPointsFlow,
                                locationFlow,
                                selectedBorderPointFlow,
                                lastCameraPositionFlow
                            ) { borderPoints, location, selectedBorderPoint, lastPosition ->
                                if (location != null) {
                                    MapScreenState.Success(
                                        borderPoints = borderPoints,
                                        userLocation = location,
                                        selectedBorderPoint = selectedBorderPoint,
                                        lastCameraPosition = lastPosition
                                    )
                                } else {
                                    MapScreenState.Loading
                                }
                            }.collect { newState ->
                                _state.value = newState
                            }
                        }

                        is ResultUtil.Error -> {
                            _state.value = MapScreenState.Error(
                                result.exception.message ?: "Failed to load border points"
                            )
                        }
                    }
                }
            } catch (e: SecurityException) {
                _state.value = MapScreenState.LocationPermissionRequired
            }
        }
    }

    fun onMapClick(latLng: LatLng) {
        _selectedLocation.value = latLng
    }

    fun onBoundsChange(bounds: LatLngBounds?, zoom: Float) {
        viewModelScope.launch {
            if (zoom < 5f) {
                borderPointsFlow.value = emptyList()
                return@launch
            } else if (bounds != null) {
                getBorderPointsByCoordinatesUseCase(bounds = bounds).collect { result ->
                    when (result) {
                        is ResultUtil.Success -> {
                            borderPointsFlow.value = result.data
                        }
                        is ResultUtil.Error -> _state.value = MapScreenState.Error(
                            result.exception.message ?: "Failed to load border points"
                        )
                    }
                }
            }
        }
    }

    fun onPermissionGranted() {
        _state.value = MapScreenState.Loading
        initialiseScreen()
    }

    fun selectBorderPoint(borderPoint: BorderPoint) {
        selectedBorderPointFlow.value = borderPoint
        // Make sure the selected point is included in the border points list
        if (!borderPointsFlow.value.contains(borderPoint)) {
            borderPointsFlow.value += borderPoint
        }
    }

    fun clearSelectedBorderPoint() {
        selectedBorderPointFlow.value = null
    }

    fun updateCameraPosition(position: CameraPosition) {
        lastCameraPositionFlow.value = position
    }
}