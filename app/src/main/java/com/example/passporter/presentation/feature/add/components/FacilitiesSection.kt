package com.example.passporter.presentation.feature.add.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.passporter.domain.entity.Amenities
import com.example.passporter.domain.entity.Facilities
import com.example.passporter.domain.entity.Services

@Composable
fun FacilitiesSection(
    facilities: Facilities,
    onFacilitiesChange: (Facilities) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Amenities Section
        Card(
            modifier = Modifier.fillMaxWidth(),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text(
                    text = "Amenities",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                AmenitiesGrid(
                    amenities = facilities.amenities,
                    onAmenitiesChange = { updatedAmenities ->
                        onFacilitiesChange(facilities.copy(amenities = updatedAmenities))
                    }
                )
            }
        }

        // Services Section
        Card(
            modifier = Modifier.fillMaxWidth(),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text(
                    text = "Services",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                ServicesInputs(
                    services = facilities.services,
                    onServicesChange = { updatedServices ->
                        onFacilitiesChange(facilities.copy(services = updatedServices))
                    }
                )
            }
        }
    }
}

@Composable
private fun AmenitiesGrid(
    amenities: Amenities,
    onAmenitiesChange: (Amenities) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            AmenityCheckbox(
                label = "Restrooms",
                checked = amenities.restrooms ?: false,
                onCheckedChange = { onAmenitiesChange(amenities.copy(restrooms = it)) }
            )
            AmenityCheckbox(
                label = "Food",
                checked = amenities.food ?: false,
                onCheckedChange = { onAmenitiesChange(amenities.copy(food = it)) }
            )
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            AmenityCheckbox(
                label = "Water",
                checked = amenities.water ?: false,
                onCheckedChange = { onAmenitiesChange(amenities.copy(water = it)) }
            )
            AmenityCheckbox(
                label = "Shelter",
                checked = amenities.shelter ?: false,
                onCheckedChange = { onAmenitiesChange(amenities.copy(shelter = it)) }
            )
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            AmenityCheckbox(
                label = "WiFi",
                checked = amenities.wifi ?: false,
                onCheckedChange = { onAmenitiesChange(amenities.copy(wifi = it)) }
            )
            AmenityCheckbox(
                label = "Parking",
                checked = amenities.parking ?: false,
                onCheckedChange = { onAmenitiesChange(amenities.copy(parking = it)) }
            )
        }
    }
}

@Composable
private fun AmenityCheckbox(
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

@Composable
private fun ServicesInputs(
    services: Services,
    onServicesChange: (Services) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Currency Exchange
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = services.currencyExchange.available ?: false,
                onCheckedChange = {
                    onServicesChange(services.copy(
                        currencyExchange = services.currencyExchange.copy(available = it)
                    ))
                }
            )
            Text(
                text = "Currency Exchange",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(start = 8.dp)
            )
        }

        // Duty Free
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = services.dutyFree ?: false,
                onCheckedChange = { onServicesChange(services.copy(dutyFree = it)) }
            )
            Text(
                text = "Duty Free",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(start = 8.dp)
            )
        }

        // Storage
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = services.storage ?: false,
                onCheckedChange = { onServicesChange(services.copy(storage = it)) }
            )
            Text(
                text = "Storage Facilities",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(start = 8.dp)
            )
        }

        // Insurance Services
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(
                    checked = services.insurance.available ?: false,
                    onCheckedChange = {
                        onServicesChange(services.copy(
                            insurance = services.insurance.copy(available = it)
                        ))
                    }
                )
                Text(
                    text = "Insurance Services",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(start = 8.dp)
                )
            }

            AnimatedVisibility(visible = services.insurance.available == true) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 32.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    var insuranceTypes by remember { mutableStateOf(services.insurance.types.joinToString(", ")) }

                    OutlinedTextField(
                        value = insuranceTypes,
                        onValueChange = { newValue ->
                            insuranceTypes = newValue
                            onServicesChange(services.copy(
                                insurance = services.insurance.copy(
                                    types = newValue.split(",").map { it.trim() }.filter { it.isNotEmpty() }
                                )
                            ))
                        },
                        label = { Text("Insurance Types") },
                        placeholder = { Text("e.g., Vehicle, Travel, Health") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }

        // Telecommunications Services
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(
                    checked = services.telecommunications.available ?: false,
                    onCheckedChange = {
                        onServicesChange(services.copy(
                            telecommunications = services.telecommunications.copy(available = it)
                        ))
                    }
                )
                Text(
                    text = "Telecommunications",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(start = 8.dp)
                )
            }

            AnimatedVisibility(visible = services.telecommunications.available == true) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 32.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    var operators by remember { mutableStateOf(services.telecommunications.operators.joinToString(", ")) }

                    OutlinedTextField(
                        value = operators,
                        onValueChange = { newValue ->
                            operators = newValue
                            onServicesChange(services.copy(
                                telecommunications = services.telecommunications.copy(
                                    operators = newValue.split(",").map { it.trim() }.filter { it.isNotEmpty() }
                                )
                            ))
                        },
                        label = { Text("Mobile Operators") },
                        placeholder = { Text("e.g., Operator1, Operator2") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = services.telecommunications.hasSimCards ?: false,
                            onCheckedChange = {
                                onServicesChange(services.copy(
                                    telecommunications = services.telecommunications.copy(hasSimCards = it)
                                ))
                            }
                        )
                        Text(
                            text = "SIM Cards Available",
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(start = 8.dp)
                        )
                    }
                }
            }
        }
    }
}