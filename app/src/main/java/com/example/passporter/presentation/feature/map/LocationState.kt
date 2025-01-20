package com.example.passporter.presentation.feature.map

import com.google.android.gms.maps.model.LatLng

sealed class LocationState {
    object Initial : LocationState()
    object RequiresPermission : LocationState()
    data class Success(val location: LatLng) : LocationState()
    data class Error(val message: String) : LocationState()
}