package com.example.passporter.presentation.feature.map.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.passporter.domain.entity.BorderPoint
import com.google.android.gms.maps.model.BitmapDescriptorFactory
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
    lastCameraPosition: CameraPosition?,
    selectedLocation: LatLng?,
    onBorderPointClick: (BorderPoint) -> Unit,
    onBoundsChange: (LatLngBounds?, Float) -> Unit,
    onDismissSelection: () -> Unit,
    onCameraPositionChange: (CameraPosition) -> Unit,
    onNavigateToBorderDetail: (String) -> Unit,
    onNavigateToAdd: (LatLng) -> Unit,
    onMapClick: (LatLng) -> Unit
) {

    val cameraPositionState = rememberCameraPositionState {
        position = selectedBorderPoint?.let {
            CameraPosition.fromLatLngZoom(
                LatLng(it.latitude, it.longitude),
                lastCameraPosition?.zoom ?: 6f  // Use last zoom or default
            )
        } ?: lastCameraPosition ?: CameraPosition.fromLatLngZoom(
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

    LaunchedEffect(selectedBorderPoint) {
        selectedBorderPoint?.let {
            cameraPositionState.position = CameraPosition.Builder()
                .target(LatLng(it.latitude, it.longitude))
                .zoom(cameraPositionState.position.zoom)  // Maintain current zoom
                .bearing(cameraPositionState.position.bearing)  // Maintain current bearing
                .tilt(cameraPositionState.position.tilt)  // Maintain current tilt
                .build()
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

    LaunchedEffect(cameraPositionState.position) {
        if (selectedBorderPoint == null) {
            onCameraPositionChange(cameraPositionState.position)
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState,
            uiSettings = MapUiSettings(
                myLocationButtonEnabled = userLocation != null
            ),
            onMapClick = onMapClick,
            onMapLoaded = {
                onBoundsChange(
                    cameraPositionState.projection?.visibleRegion?.latLngBounds,
                    cameraPositionState.position.zoom
                )
            }
        ) {
            // Border point markers
            borderPoints.forEach { borderPoint ->
                val isSelected = selectedBorderPoint?.id == borderPoint.id

                Marker(
                    state = MarkerState(
                        position = LatLng(borderPoint.latitude, borderPoint.longitude)
                    ),
                    title = borderPoint.name,
                    icon = if (isSelected) {
                        BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)
                    } else {
                        BitmapDescriptorFactory.defaultMarker()
                    },
                    onClick = {
                        onBorderPointClick(borderPoint)
                        true
                    }
                )
            }
        }

        // Selected border point details
        selectedBorderPoint?.let { borderPoint ->
            BorderPointPopup(
                borderPoint = borderPoint,
                onDismiss = onDismissSelection,
                onNavigateToBorderDetail = onNavigateToBorderDetail,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 16.dp)
            )
        }

        selectedLocation?.let { location ->
            FloatingActionButton(
                onClick = { onNavigateToAdd(location) },
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(16.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Border Point")
            }
        }
    }
}