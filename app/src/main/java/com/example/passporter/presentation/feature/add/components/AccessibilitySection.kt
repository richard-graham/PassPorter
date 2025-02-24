package com.example.passporter.presentation.feature.add.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.passporter.domain.entity.Accessibility
import com.example.passporter.domain.entity.RoadCondition
import com.example.passporter.domain.entity.TrafficTypes

@Composable
fun AccessibilitySection(
    accessibility: Accessibility,
    onAccessibilityChange: (Accessibility) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Traffic Types Section
        Card(
            modifier = Modifier.fillMaxWidth(),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Traffic Types",
                    style = MaterialTheme.typography.titleMedium
                )

                TrafficTypesGrid(
                    trafficTypes = accessibility.trafficTypes,
                    onTrafficTypeChange = { updatedTrafficTypes ->
                        onAccessibilityChange(accessibility.copy(trafficTypes = updatedTrafficTypes))
                    }
                )
            }
        }

        // Road Conditions Section
        Card(
            modifier = Modifier.fillMaxWidth(),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Road Conditions",
                    style = MaterialTheme.typography.titleMedium
                )

                // Approaching Road
                RoadConditionSection(
                    title = "Approaching Road",
                    roadCondition = accessibility.roadConditions.approaching,
                    onConditionChange = { updatedCondition ->
                        onAccessibilityChange(
                            accessibility.copy(
                                roadConditions = accessibility.roadConditions.copy(
                                    approaching = updatedCondition
                                )
                            )
                        )
                    }
                )

                RoadConditionSection(
                    title = "Departing Road",
                    roadCondition = accessibility.roadConditions.departing,
                    onConditionChange = { updatedCondition ->
                        onAccessibilityChange(
                            accessibility.copy(
                                roadConditions = accessibility.roadConditions.copy(
                                    departing = updatedCondition
                                )
                            )
                        )
                    }
                )
            }
        }
    }
}

@Composable
private fun TrafficTypesGrid(
    trafficTypes: TrafficTypes,
    onTrafficTypeChange: (TrafficTypes) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            TrafficTypeCheckbox(
                label = "Pedestrian",
                checked = trafficTypes.pedestrian ?: false,
                onCheckedChange = { onTrafficTypeChange(trafficTypes.copy(pedestrian = it)) }
            )
            TrafficTypeCheckbox(
                label = "Bicycle",
                checked = trafficTypes.bicycle ?: false,
                onCheckedChange = { onTrafficTypeChange(trafficTypes.copy(bicycle = it)) }
            )
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            TrafficTypeCheckbox(
                label = "Motorcycle",
                checked = trafficTypes.motorcycle ?: false,
                onCheckedChange = { onTrafficTypeChange(trafficTypes.copy(motorcycle = it)) }
            )
            TrafficTypeCheckbox(
                label = "Car",
                checked = trafficTypes.car ?: false,
                onCheckedChange = { onTrafficTypeChange(trafficTypes.copy(car = it)) }
            )
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            TrafficTypeCheckbox(
                label = "RV",
                checked = trafficTypes.rv ?: false,
                onCheckedChange = { onTrafficTypeChange(trafficTypes.copy(rv = it)) }
            )
            TrafficTypeCheckbox(
                label = "Truck",
                checked = trafficTypes.truck ?: false,
                onCheckedChange = { onTrafficTypeChange(trafficTypes.copy(truck = it)) }
            )
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            TrafficTypeCheckbox(
                label = "Bus",
                checked = trafficTypes.bus ?: false,
                onCheckedChange = { onTrafficTypeChange(trafficTypes.copy(bus = it)) }
            )
            Spacer(modifier = Modifier.width(48.dp)) // For alignment
        }
    }
}

@Composable
private fun TrafficTypeCheckbox(
    label: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier.width(120.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(
            checked = checked,
            onCheckedChange = onCheckedChange
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(start = 8.dp)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun RoadConditionInputs(
    roadCondition: RoadCondition,
    onConditionChange: (RoadCondition) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Road Type Dropdown
        ExposedDropdownMenuBox(
            expanded = false,
            onExpandedChange = { },
            modifier = Modifier.fillMaxWidth()
        ) {
            OutlinedTextField(
                value = roadCondition.type ?: "",
                onValueChange = { onConditionChange(roadCondition.copy(type = it.ifBlank { null })) },
                label = { Text("Road Type") },
                modifier = Modifier.fillMaxWidth()
            )
        }

        // Road Condition Dropdown
        ExposedDropdownMenuBox(
            expanded = false,
            onExpandedChange = { },
            modifier = Modifier.fillMaxWidth()
        ) {
            OutlinedTextField(
                value = roadCondition.condition ?: "",
                onValueChange = { onConditionChange(roadCondition.copy(condition = it.ifBlank { null })) },
                label = { Text("Condition") },
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
private fun RoadConditionSection(
    title: String,
    roadCondition: RoadCondition,
    onConditionChange: (RoadCondition) -> Unit
) {
    Text(
        text = title,
        style = MaterialTheme.typography.bodyMedium,
        modifier = Modifier.padding(top = 8.dp)
    )
    RoadConditionInputs(
        roadCondition = roadCondition,
        onConditionChange = onConditionChange
    )
}