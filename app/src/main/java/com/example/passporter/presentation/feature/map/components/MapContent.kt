package com.example.passporter.presentation.feature.map.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.passporter.domain.entity.BorderPoint
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState

@Composable
fun MapContent(
    borderPoints: List<BorderPoint>,
    userLocation: LatLng?,
    selectedBorderPoint: BorderPoint?,
    onBorderPointClick: (BorderPoint) -> Unit,
    onDismissSelection: () -> Unit
) {
    Box(modifier = Modifier.fillMaxSize()) {
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = rememberCameraPositionState {
                position = CameraPosition.fromLatLngZoom(
                    userLocation ?: LatLng(0.0, 0.0),
                    if (userLocation != null) 10f else 2f
                )
            }
        ) {
            // User location marker
            userLocation?.let {
                Marker(
                    state = MarkerState(position = it),
                    title = "Your Location"
                )
            }

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
//            BorderPointDetails(
//                borderPoint = borderPoint,
//                onDismiss = onDismissSelection,
//                modifier = Modifier
//                    .align(Alignment.BottomCenter)
//                    .padding(16.dp)
//            )
        }
    }
}