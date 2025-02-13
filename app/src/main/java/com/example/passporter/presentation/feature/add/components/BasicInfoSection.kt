package com.example.passporter.presentation.feature.add.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.passporter.presentation.feature.add.BasicBorderInfo

@Composable
fun BasicInfoSection(
    basicInfo: BasicBorderInfo,
    onBasicInfoChange: (BasicBorderInfo) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        OutlinedTextField(
            value = basicInfo.name,
            onValueChange = { onBasicInfoChange(basicInfo.copy(name = it)) },
            label = { Text("Name *") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = basicInfo.nameEnglish ?: "",
            onValueChange = { onBasicInfoChange(basicInfo.copy(nameEnglish = it.ifBlank { null })) },
            label = { Text("English Name") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = basicInfo.countryA,
            onValueChange = { onBasicInfoChange(basicInfo.copy(countryA = it)) },
            label = { Text("Country A *") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = basicInfo.countryB,
            onValueChange = { onBasicInfoChange(basicInfo.copy(countryB = it)) },
            label = { Text("Country B *") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = basicInfo.description,
            onValueChange = { onBasicInfoChange(basicInfo.copy(description = it)) },
            label = { Text("Description") },
            modifier = Modifier.fillMaxWidth(),
            minLines = 3
        )

        // Add dropdowns for borderType and crossingType
        // Add field for operatingAuthority
    }
}

// Similar section components for OperatingHours, Accessibility, and Facilities
// Implementation details omitted for brevity but would include all fields
// from the data classes with appropriate input controls