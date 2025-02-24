package com.example.passporter.presentation.feature.add.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState

@Composable
fun LocationEditSection(
    latitude: Double,
    longitude: Double,
    onLocationChange: (Double, Double) -> Unit,
    modifier: Modifier = Modifier
) {
    // Initial position from props
    val initialPosition = LatLng(
        latitude.takeIf { it != 0.0 } ?: 0.0,
        longitude.takeIf { it != 0.0 } ?: 0.0
    )

    // Current marker position state
    var markerPosition by remember { mutableStateOf(initialPosition) }

    // Update marker position when props change
    LaunchedEffect(latitude, longitude) {
        if (latitude != 0.0 && longitude != 0.0) {
            markerPosition = LatLng(latitude, longitude)
        }
    }

    // Camera position state - only set initially
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(markerPosition, 15f)
    }

    // Only update camera position on initial load or when props change
    LaunchedEffect(latitude, longitude) {
        if (latitude != 0.0 && longitude != 0.0) {
            cameraPositionState.position = CameraPosition.fromLatLngZoom(
                LatLng(latitude, longitude),
                cameraPositionState.position.zoom
            )
        }
    }

    Card(
        modifier = modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = "Border Point Location",
                style = MaterialTheme.typography.titleMedium
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = if (latitude == 0.0 && longitude == 0.0)
                    "Tap on the map to set the border point location."
                else
                    "Tap anywhere on the map to set a new location.",
                style = MaterialTheme.typography.bodyMedium
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Coordinates display
            Text(
                text = "Latitude: ${markerPosition.latitude.format(6)}",
                style = MaterialTheme.typography.bodyMedium
            )

            Text(
                text = "Longitude: ${markerPosition.longitude.format(6)}",
                style = MaterialTheme.typography.bodyMedium
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Map for location selection
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp)
            ) {
                GoogleMap(
                    modifier = Modifier.fillMaxSize(),
                    cameraPositionState = cameraPositionState,
                    properties = MapProperties(
                        maxZoomPreference = 21f,
                        minZoomPreference = 5f,
                    ),
                    uiSettings = MapUiSettings(
                        zoomControlsEnabled = true,
                        mapToolbarEnabled = false
                    ),
                    onMapClick = { latLng ->
                        markerPosition = latLng
                        onLocationChange(latLng.latitude, latLng.longitude)
                    }
                ) {
                    Marker(
                        state = MarkerState(position = markerPosition),
                        title = "Border Point Location"
                    )
                }

                // Option to use preset locations if none is set
                if (latitude == 0.0 && longitude == 0.0) {
                    Button(
                        onClick = {
                            // You could set a relevant default location based on the app's context
                            // For example, a common border crossing point
                            val defaultLocation = LatLng(49.002, 2.550) // Example: Paris CDG Airport
                            markerPosition = defaultLocation
                            onLocationChange(defaultLocation.latitude, defaultLocation.longitude)
                        },
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .padding(bottom = 16.dp)
                    ) {
                        Text("Use Sample Location")
                    }
                }
            }
        }
    }
}

// Extension function to format double to specific number of decimal places
private fun Double.format(digits: Int): String {
    return "%.${digits}f".format(this)
}