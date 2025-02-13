package com.example.passporter.presentation.feature.add

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.passporter.presentation.feature.add.components.AccessibilitySection
import com.example.passporter.presentation.feature.add.components.BasicInfoSection
import com.example.passporter.presentation.feature.add.components.FacilitiesSection
import com.example.passporter.presentation.feature.add.components.OperatingHoursSection

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddBorderPointScreen(
    viewModel: AddBorderPointViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit
) {
    val state by viewModel.state.collectAsState()
    val context = LocalContext.current
    var currentSection by remember { mutableStateOf(0) }
    val sections = listOf("Basic Info", "Operating Hours", "Accessibility", "Facilities")

    LaunchedEffect(state.additionComplete) {
        if (state.additionComplete) {
            Toast.makeText(context, "Border point added successfully", Toast.LENGTH_SHORT).show()
            onNavigateBack()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Add New Border Point") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Navigate back"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            TabRow(
                selectedTabIndex = currentSection,
                modifier = Modifier.fillMaxWidth()
            ) {
                sections.forEachIndexed { index, title ->
                    Tab(
                        selected = currentSection == index,
                        onClick = { currentSection = index },
                        text = { Text(title) }
                    )
                }
            }

            when (state) {
                is AddBorderPointState.Loading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }

                is AddBorderPointState.Error -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Error: ${(state as AddBorderPointState.Error).message}",
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }

                is AddBorderPointState.Input -> {
                    val inputState = state as AddBorderPointState.Input
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                            .padding(16.dp)
                    ) {
                        when (currentSection) {
                            0 -> FormSection(title = "Basic Information") {
                                BasicInfoSection(
                                    basicInfo = inputState.basicInfo,
                                    onBasicInfoChange = viewModel::updateBasicInfo
                                )
                            }
                            1 -> FormSection(title = "Operating Hours") {
                                OperatingHoursSection(
                                    operatingHours = inputState.operatingHours,
                                    onOperatingHoursChange = viewModel::updateOperatingHours
                                )
                            }
                            2 -> FormSection(title = "Accessibility") {
                                AccessibilitySection(
                                    accessibility = inputState.accessibility,
                                    onAccessibilityChange = viewModel::updateAccessibility
                                )
                            }
                            3 -> FormSection(title = "Facilities") {
                                FacilitiesSection(
                                    facilities = inputState.facilities,
                                    onFacilitiesChange = viewModel::updateFacilities
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            if (currentSection > 0) {
                                Button(
                                    onClick = { currentSection-- },
                                    modifier = Modifier.weight(1f).padding(end = 8.dp)
                                ) {
                                    Text("Previous")
                                }
                            }

                            if (currentSection < sections.size - 1) {
                                Button(
                                    onClick = { currentSection++ },
                                    modifier = Modifier.weight(1f).padding(start = if (currentSection > 0) 8.dp else 0.dp)
                                ) {
                                    Text("Next")
                                }
                            } else {
                                Button(
                                    onClick = viewModel::submitBorderPoint,
                                    enabled = inputState.isValid(),
                                    modifier = Modifier.weight(1f).padding(start = if (currentSection > 0) 8.dp else 0.dp)
                                ) {
                                    Text("Submit")
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
private fun FormSection(
    title: String,
    content: @Composable () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        content()
    }
}