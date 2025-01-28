package com.example.passporter.presentation.feature.map.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import com.example.passporter.domain.entity.BorderPoint
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState

@Composable
fun MapContent(
    borderPoints: List<BorderPoint>,
    userLocation: LatLng?,
    selectedBorderPoint: BorderPoint?,
    onBorderPointClick: (BorderPoint) -> Unit,
    onBoundsChange: (LatLngBounds?, Float) -> Unit,
    onDismissSelection: () -> Unit
) {
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(
            userLocation ?: LatLng(0.0, 0.0),
            6f
        )
    }

    LaunchedEffect(userLocation) {
        if (userLocation != null) {
            cameraPositionState.position = CameraPosition.fromLatLngZoom(
                userLocation,
                cameraPositionState.position.zoom
            )
        }
    }

    LaunchedEffect(cameraPositionState.isMoving) {
        if (!cameraPositionState.isMoving) {
            onBoundsChange(
                cameraPositionState.projection?.visibleRegion?.latLngBounds,
                cameraPositionState.position.zoom
            )
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState,
            uiSettings = MapUiSettings(
                myLocationButtonEnabled = userLocation != null
            ),
            onMapLoaded = {
                onBoundsChange(
                    cameraPositionState.projection?.visibleRegion?.latLngBounds,
                    cameraPositionState.position.zoom
                )
            }
        ) {
            // Border point markers
            borderPoints.forEach { borderPoint ->
                Marker(
                    state = MarkerState(
                        position = LatLng(borderPoint.latitude, borderPoint.longitude)
                    ),
                    title = borderPoint.name,
                    onClick = {
                        onBorderPointClick(borderPoint)
                        true
                    }
                )
            }
        }

        // Selected border point details
        selectedBorderPoint?.let { borderPoint ->
        }
    }
}