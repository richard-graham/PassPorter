package com.example.passporter.presentation.feature.map

import com.example.passporter.domain.entity.BorderPoint
import com.google.android.gms.maps.model.LatLng

sealed class MapScreenState {
    object Loading : MapScreenState()
    object LocationPermissionRequired : MapScreenState()
    data class Error(val message: String) : MapScreenState()
    data class Success(
        val borderPoints: List<BorderPoint>,
        val userLocation: LatLng? = null,
        val selectedBorderPoint: BorderPoint? = null
    ) : MapScreenState()
}