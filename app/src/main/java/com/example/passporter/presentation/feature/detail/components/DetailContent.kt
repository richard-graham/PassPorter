package com.example.passporter.presentation.feature.detail.components

import android.os.Build
import androidx.annotation.RequiresApi
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
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocationOn
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
import com.google.android.gms.maps.model.BitmapDescriptorFactory
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

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun DetailContent(
    borderPoint: BorderPoint,
    onNavigateUp: () -> Unit,
    onEditClick: (String) -> Unit,
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
                },
                actions = {
                    IconButton(
                        onClick = { onEditClick(borderPoint.id) }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Edit border point"
                        )
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
                        icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE),
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
                            icon = Icons.Default.Info,
                            label = "Border Type",
                            value = borderPoint.borderType ?: "Not specified"
                        )
                        InfoRow(
                            icon = Icons.Default.Info,
                            label = "Crossing Type",
                            value = borderPoint.crossingType ?: "Not specified"
                        )
                        if (borderPoint.description.isNotBlank()) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Description",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = borderPoint.description,
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }
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

                            // Regular Hours
                            hours.regular?.let {
                                Text(
                                    text = "Regular Hours",
                                    style = MaterialTheme.typography.titleMedium
                                )
                                Text(
                                    text = formatScheduleText(it),
                                    style = MaterialTheme.typography.bodyLarge
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                            }

                            // Summer Hours
                            hours.summerHours?.let { summer ->
                                Text(
                                    text = "Summer Season",
                                    style = MaterialTheme.typography.titleMedium
                                )
                                val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
                                Text(
                                    text = "From ${dateFormat.format(Date(summer.startDate.toEpochDay() * 24 * 60 * 60 * 1000))} " +
                                            "to ${dateFormat.format(Date(summer.endDate.toEpochDay() * 24 * 60 * 60 * 1000))}",
                                    style = MaterialTheme.typography.bodyMedium
                                )
                                Text(
                                    text = formatScheduleText(summer.schedule),
                                    style = MaterialTheme.typography.bodyLarge
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                            }

                            // Winter Hours
                            hours.winterHours?.let { winter ->
                                Text(
                                    text = "Winter Season",
                                    style = MaterialTheme.typography.titleMedium
                                )
                                val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
                                Text(
                                    text = "From ${dateFormat.format(Date(winter.startDate.toEpochDay() * 24 * 60 * 60 * 1000))} " +
                                            "to ${dateFormat.format(Date(winter.endDate.toEpochDay() * 24 * 60 * 60 * 1000))}",
                                    style = MaterialTheme.typography.bodyMedium
                                )
                                Text(
                                    text = formatScheduleText(winter.schedule),
                                    style = MaterialTheme.typography.bodyLarge
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                }


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

private fun formatScheduleText(scheduleText: String): String {
    if (scheduleText.isBlank()) return "Not specified"

    // Define days of the week in order
    val daysOrder = listOf("Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday")

    // Parse the schedule text into a map of day ranges and their hours
    val scheduleMap = mutableMapOf<String, String>()
    scheduleText.split(";").forEach { part ->
        val trimmedPart = part.trim()
        val colonIndex = trimmedPart.indexOf(':')
        if (colonIndex > 0) {
            val days = trimmedPart.substring(0, colonIndex).trim()
            val hours = trimmedPart.substring(colonIndex + 1).trim()
            scheduleMap[days] = hours
        }
    }

    // Sort the entries based on the first day in each range
    val sortedEntries = scheduleMap.entries.sortedWith { a, b ->
        val firstDayA = extractFirstDay(a.key, daysOrder)
        val firstDayB = extractFirstDay(b.key, daysOrder)
        daysOrder.indexOf(firstDayA).compareTo(daysOrder.indexOf(firstDayB))
    }

    // Format each entry with proper line breaks
    return sortedEntries.joinToString("\n") { (days, hours) ->
        "$days:\n   $hours"
    }
}

// Helper function to extract the first day from a day range
private fun extractFirstDay(dayRange: String, daysOrder: List<String>): String {
    // If it's a range like "Monday-Friday"
    if (dayRange.contains("-")) {
        val firstDay = dayRange.split("-")[0].trim()
        return firstDay
    }
    // If it's a list like "Monday, Wednesday"
    else if (dayRange.contains(",")) {
        val days = dayRange.split(",").map { it.trim() }
        // Find the earliest day in the week
        return days.minByOrNull { daysOrder.indexOf(it) } ?: daysOrder.first()
    }
    // If it's a single day
    return dayRange.trim()
}