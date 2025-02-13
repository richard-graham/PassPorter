package com.example.passporter.presentation.feature.detail.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.passporter.domain.entity.BorderPoint
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun DetailContent(
    borderPoint: BorderPoint,
    onNavigateUp: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(borderPoint.name) },
                navigationIcon = {
                    IconButton(onClick = onNavigateUp) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Navigate up")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
        ) {
            // Map Preview
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
            ) {
                GoogleMap(
                    modifier = Modifier.fillMaxSize(),
                    cameraPositionState = rememberCameraPositionState {
                        position = CameraPosition.fromLatLngZoom(
                            LatLng(borderPoint.latitude, borderPoint.longitude),
                            15f
                        )
                    },
                    properties = MapProperties(
                        // Keep the map centered on the marker
                        maxZoomPreference = 20f,
                        minZoomPreference = 5f,
                    ),
                    uiSettings = MapUiSettings(
                        zoomControlsEnabled = true,
                        scrollGesturesEnabled = false,
                        rotationGesturesEnabled = false,
                        tiltGesturesEnabled = false,
                        compassEnabled = false,
                        mapToolbarEnabled = false,
                        zoomGesturesEnabled = true // Only allow zoom gestures
                    )
                ) {
                    Marker(
                        state = MarkerState(
                            position = LatLng(borderPoint.latitude, borderPoint.longitude)
                        ),
                        title = borderPoint.name
                    )
                }
            }

            // Main Content
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth()
            ) {
                // Basic Information Card
                ElevatedCard(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = "Basic Information",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        InfoRow(
                            icon = Icons.Default.LocationOn,
                            label = "Countries",
                            value = "${borderPoint.countryA} - ${borderPoint.countryB}"
                        )
                        InfoRow(
                            icon = Icons.Default.Info,
                            label = "Status",
                            value = borderPoint.status.toString()
                        )
                        InfoRow(
                            icon = Icons.Default.AccessTime,
                            label = "Last Updated",
                            value = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
                                .format(Date(borderPoint.lastUpdate))
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Operating Hours Card
                borderPoint.operatingHours?.let { hours ->
                    ElevatedCard(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Text(
                                text = "Operating Hours",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(8.dp))

                            hours.regular?.let {
                                InfoRow(
                                    icon = Icons.Default.Schedule,
                                    label = "Regular Hours",
                                    value = it
                                )
                            }
                            hours.covid?.let {
                                InfoRow(
                                    icon = Icons.Default.Warning,
                                    label = "COVID Hours",
                                    value = it
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Traffic Types Card
                ElevatedCard(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = "Allowed Traffic",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        FlowRow(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            with(borderPoint.accessibility.trafficTypes) {
                                if (pedestrian == true) TrafficChip("Pedestrian")
                                if (bicycle == true) TrafficChip("Bicycle")
                                if (motorcycle == true) TrafficChip("Motorcycle")
                                if (car == true) TrafficChip("Car")
                                if (rv == true) TrafficChip("RV")
                                if (truck == true) TrafficChip("Truck")
                                if (bus == true) TrafficChip("Bus")
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Facilities Card
                ElevatedCard(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = "Facilities",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        // Amenities Section
                        Text(
                            text = "Amenities",
                            style = MaterialTheme.typography.titleMedium
                        )
                        FlowRow(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            with(borderPoint.facilities.amenities) {
                                if (restrooms == true) FacilityChip("Restrooms")
                                if (food == true) FacilityChip("Food")
                                if (water == true) FacilityChip("Water")
                                if (shelter == true) FacilityChip("Shelter")
                                if (wifi == true) FacilityChip("WiFi")
                                if (parking == true) FacilityChip("Parking")
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Services Section
                        Text(
                            text = "Services",
                            style = MaterialTheme.typography.titleMedium
                        )
                        FlowRow(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            with(borderPoint.facilities.services) {
                                if (currencyExchange.available == true) {
                                    FacilityChip("Currency Exchange")
                                }
                                if (dutyFree == true) FacilityChip("Duty Free")
                                if (insurance.available == true) {
                                    FacilityChip("Insurance")
                                }
                                if (storage == true) FacilityChip("Storage")
                                if (telecommunications.available == true) {
                                    FacilityChip("Telecommunications")
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun InfoRow(
    icon: ImageVector,
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Column {
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = value,
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}

@Composable
private fun TrafficChip(
    text: String,
    modifier: Modifier = Modifier
) {
    AssistChip(
        onClick = { },
        label = { Text(text) },
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.DirectionsCar,
                contentDescription = null,
                modifier = Modifier.size(AssistChipDefaults.IconSize)
            )
        },
        modifier = modifier
    )
}

@Composable
private fun FacilityChip(
    text: String,
    modifier: Modifier = Modifier
) {
    SuggestionChip(
        onClick = { },
        label = { Text(text) },
        modifier = modifier
    )
}