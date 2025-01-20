package com.example.passporter.domain.location

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Looper
import androidx.core.app.ActivityCompat
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.Priority
import com.google.android.gms.maps.model.LatLng
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

class LocationException(message: String, cause: Throwable? = null) : Exception(message, cause)
class LocationSettingsException(val resolvable: ResolvableApiException) : Exception()

@Singleton
class LocationManagerImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : LocationManager {

    private val fusedLocationClient: FusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context)
    private val locationRequest = LocationRequest.Builder(
        Priority.PRIORITY_HIGH_ACCURACY,
        UPDATE_INTERVAL
    ).setMinUpdateDistanceMeters(MIN_DISTANCE_METERS)
        .setMinUpdateIntervalMillis(FASTEST_UPDATE_INTERVAL)
        .build()
    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(result: LocationResult) {
            super.onLocationResult(result)
            // Handle location result
        }
    }

    override fun getLocationUpdates(): Flow<LocationResult> = callbackFlow {
        if (!hasLocationPermissions()) {
            throw SecurityException("Location permissions not granted")
        }

        try {
            // Create client settings
            val builder = LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest)
                .setAlwaysShow(true)

            // Check location settings
            val settingsClient = LocationServices.getSettingsClient(context)
            settingsClient.checkLocationSettings(builder.build()).await()

            // Create callback
            val callback = object : LocationCallback() {
                override fun onLocationResult(result: LocationResult) {
                    trySend(result)
                }
            }

            // Request location updates
            fusedLocationClient.requestLocationUpdates(
                locationRequest,
                callback,
                Looper.getMainLooper()
            ).await()

            // Cleanup when Flow collection ends
            awaitClose {
                fusedLocationClient.removeLocationUpdates(callback)
            }
        } catch (e: Exception) {
            throw LocationException("Failed to get location updates", e)
        }
    }

    override suspend fun getCurrentLocation(): LatLng? {
        if (!hasLocationPermissions()) {
            throw SecurityException("Location permissions not granted")
        }

        return try {
            // Check permissions again just before getting location
            if (ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                throw SecurityException("Location permissions not granted")
            }

            fusedLocationClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, null)
                .await()
                ?.let { LatLng(it.latitude, it.longitude) }
        } catch (e: SecurityException) {
            throw SecurityException("Location permissions not granted", e)
        } catch (e: Exception) {
            throw LocationException("Failed to get current location", e)
        }
    }

    override fun hasLocationPermission(): Boolean {
        return hasPermission(Manifest.permission.ACCESS_FINE_LOCATION) &&
                hasPermission(Manifest.permission.ACCESS_COARSE_LOCATION)
    }

    override suspend fun requestLocationPermission(): Boolean {
        // Note: This should be implemented in the UI layer using the activity result API
        // This method should be called from the ViewModel to check if permissions are needed
        return hasLocationPermission()
    }

    override suspend fun checkLocationSettings(): Boolean {
        val builder = LocationSettingsRequest.Builder()
            .addLocationRequest(locationRequest)
            .setAlwaysShow(true)

        try {
            LocationServices.getSettingsClient(context)
                .checkLocationSettings(builder.build())
                .await()
            return true
        } catch (e: Exception) {
            when (e) {
                is ResolvableApiException -> {
                    // This should be handled in the UI layer
                    throw LocationSettingsException(e)
                }
                else -> throw LocationException("Location settings check failed", e)
            }
        }
    }

    private fun hasPermission(permission: String): Boolean {
        return ActivityCompat.checkSelfPermission(
            context,
            permission
        ) == PackageManager.PERMISSION_GRANTED
    }

    fun hasLocationPermissions(): Boolean {
        return ActivityCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
    }

    companion object {
        private const val UPDATE_INTERVAL = 10000L // 10 seconds
        private const val FASTEST_UPDATE_INTERVAL = 5000L // 5 seconds
        private const val MIN_DISTANCE_METERS = 10f // 10 meters
    }
}