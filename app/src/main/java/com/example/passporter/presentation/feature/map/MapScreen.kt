package com.example.passporter.presentation.feature.map

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.passporter.presentation.feature.map.components.LocationPermissionRequest
import com.example.passporter.presentation.feature.map.components.MapContent
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberPermissionState

@OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
@Composable
fun MapScreen(
    viewModel: MapViewModel = hiltViewModel(),
    onNavigateToBorderDetail: (String) -> Unit
) {
    val state by viewModel.state.collectAsState()

    val locationPermissionState = rememberPermissionState(
        android.Manifest.permission.ACCESS_FINE_LOCATION
    ) { isGranted ->
        if (isGranted) {
            viewModel.onPermissionGranted()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Border Crossings") },
                actions = {
                    IconButton(onClick = { /* Open notifications */ }) {
                        Icon(Icons.Default.Notifications, contentDescription = "Notifications")
                    }
                }
            )
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding)) {
            when (val currentState = state) {
                MapScreenState.Loading -> {
                    LoadingIndicator()
                }
                MapScreenState.LocationPermissionRequired -> {
                    LocationPermissionRequest(
                        onRequestPermission = {
                            locationPermissionState.launchPermissionRequest()
                        }
                    )
                }
                is MapScreenState.Error -> {
//                    ErrorMessage(message = currentState.message)
                }
                is MapScreenState.Success -> {
                    MapContent(
                        borderPoints = currentState.borderPoints,
                        userLocation = currentState.userLocation,
                        selectedBorderPoint = currentState.selectedBorderPoint,
                        lastCameraPosition = currentState.lastCameraPosition,
                        onBorderPointClick = viewModel::selectBorderPoint,
                        onBoundsChange = viewModel::onBoundsChange,
                        onDismissSelection = viewModel::clearSelectedBorderPoint,
                        onCameraPositionChange = viewModel::updateCameraPosition,
                        onNavigateToBorderDetail = onNavigateToBorderDetail
                    )
                }
            }

        }
    }
}

@Composable
private fun LoadingIndicator() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}