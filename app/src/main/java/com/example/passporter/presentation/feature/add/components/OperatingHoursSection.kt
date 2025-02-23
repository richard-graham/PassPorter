package com.example.passporter.presentation.feature.add.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.passporter.domain.entity.OperatingHours

@Composable
fun OperatingHoursSection(
    operatingHours: OperatingHours,
    onOperatingHoursChange: (OperatingHours) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        OutlinedTextField(
            value = operatingHours.regular ?: "",
            onValueChange = { onOperatingHoursChange(operatingHours.copy(regular = it.ifBlank { null })) },
            label = { Text("Regular Hours") },
            placeholder = { Text("e.g., Mon-Fri: 9:00-17:00, Sat-Sun: 10:00-16:00") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = operatingHours.covid ?: "",
            onValueChange = { onOperatingHoursChange(operatingHours.copy(covid = it.ifBlank { null })) },
            label = { Text("COVID-19 Hours") },
            placeholder = { Text("Special hours during COVID-19 if applicable") },
            modifier = Modifier.fillMaxWidth()
        )
    }
}