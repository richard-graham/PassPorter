package com.example.passporter.domain.location

import com.google.android.gms.location.LocationResult
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.flow.Flow

interface LocationManager {
    fun getLocationUpdates(): Flow<LocationResult>
    suspend fun getCurrentLocation(): LatLng?
    fun hasLocationPermission(): Boolean
    suspend fun requestLocationPermission(): Boolean
    suspend fun checkLocationSettings(): Boolean
}