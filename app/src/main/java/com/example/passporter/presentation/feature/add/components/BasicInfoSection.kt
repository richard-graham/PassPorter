package com.example.passporter.presentation.feature.add.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.passporter.presentation.feature.add.BasicBorderInfo

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BasicInfoSection(
    basicInfo: BasicBorderInfo,
    onBasicInfoChange: (BasicBorderInfo) -> Unit
) {
    val borderTypes = listOf("Land", "Maritime", "Air", "River", "Other")
    val crossingTypes = listOf("Vehicular", "Pedestrian", "Rail", "Ferry", "Mixed")
    var borderTypeExpanded by remember { mutableStateOf(false) }
    var crossingTypeExpanded by remember { mutableStateOf(false) }

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

        // Border Type Dropdown
        ExposedDropdownMenuBox(
            expanded = borderTypeExpanded,
            onExpandedChange = { borderTypeExpanded = it },
            modifier = Modifier.fillMaxWidth()
        ) {
            TextField(
                value = basicInfo.borderType ?: "",
                onValueChange = {},
                readOnly = true,
                label = { Text("Border Type") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = borderTypeExpanded) },
                modifier = Modifier.menuAnchor().fillMaxWidth()
            )

            ExposedDropdownMenu(
                expanded = borderTypeExpanded,
                onDismissRequest = { borderTypeExpanded = false }
            ) {
                borderTypes.forEach { type ->
                    DropdownMenuItem(
                        text = { Text(type) },
                        onClick = {
                            onBasicInfoChange(basicInfo.copy(borderType = type))
                            borderTypeExpanded = false
                        }
                    )
                }
            }
        }

        // Crossing Type Dropdown
        ExposedDropdownMenuBox(
            expanded = crossingTypeExpanded,
            onExpandedChange = { crossingTypeExpanded = it },
            modifier = Modifier.fillMaxWidth()
        ) {
            TextField(
                value = basicInfo.crossingType ?: "",
                onValueChange = {},
                readOnly = true,
                label = { Text("Crossing Type") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = crossingTypeExpanded) },
                modifier = Modifier.menuAnchor().fillMaxWidth()
            )

            ExposedDropdownMenu(
                expanded = crossingTypeExpanded,
                onDismissRequest = { crossingTypeExpanded = false }
            ) {
                crossingTypes.forEach { type ->
                    DropdownMenuItem(
                        text = { Text(type) },
                        onClick = {
                            onBasicInfoChange(basicInfo.copy(crossingType = type))
                            crossingTypeExpanded = false
                        }
                    )
                }
            }
        }

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