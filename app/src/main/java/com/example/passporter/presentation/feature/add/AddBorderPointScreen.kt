package com.example.passporter.presentation.feature.add

import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import com.example.passporter.presentation.feature.add.components.EnhancedOperatingHoursSelector
import com.example.passporter.presentation.feature.add.components.FacilitiesSection
import com.example.passporter.presentation.feature.add.components.LocationEditSection

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddBorderPointScreen(
    viewModel: AddBorderPointViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit,
    onUpdateSuccess: () -> Unit = {},
    onDeleteSuccess: () -> Unit = onNavigateBack,
    initialSection: Int = 0
) {
    val state by viewModel.state.collectAsState()
    val context = LocalContext.current
    var currentSection by remember { mutableIntStateOf(initialSection) }
    var showDeleteConfirmation by remember { mutableStateOf(false) }
    var deletionComplete by remember { mutableStateOf(false) }
    val sections =
        listOf("Basic Info", "Location", "Operating Hours", "Accessibility", "Facilities")

    LaunchedEffect(state.additionComplete, deletionComplete) {
        if (state.additionComplete) {
            if (deletionComplete) {
                // If deletion was successful, show toast and navigate to map
                Toast.makeText(context, "Border point deleted successfully", Toast.LENGTH_SHORT).show()
                onDeleteSuccess()
            } else {
                // For regular updates/additions
                Toast.makeText(context, "Border point saved successfully", Toast.LENGTH_SHORT).show()
                onUpdateSuccess()
                onNavigateBack()
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (state is AddBorderPointState.Input &&
                            (state as AddBorderPointState.Input).basicInfo.name.isNotEmpty()
                        ) {
                            "Editing ${(state as AddBorderPointState.Input).basicInfo.name}"
                        } else {
                            "Add New Border Point"
                        }
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Navigate back"
                        )
                    }
                },
                actions = {
                    if (state is AddBorderPointState.Input) {
                        val inputState = state as AddBorderPointState.Input

                        if (inputState.id != null) {
                            IconButton(
                                onClick = { showDeleteConfirmation = true }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = "Delete border point",
                                    tint = MaterialTheme.colorScheme.error
                                )
                            }
                        }

                        TextButton(
                            onClick = viewModel::submitBorderPoint,
                            enabled = inputState.isValid()
                        ) {
                            Text("Submit")
                        }
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
            ScrollableTabRow(
                selectedTabIndex = currentSection,
                modifier = Modifier.fillMaxWidth(),
                edgePadding = 0.dp
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
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }

                is AddBorderPointState.Input -> {
                    val inputState = state as AddBorderPointState.Input
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp)
                    ) {
                        when (currentSection) {
                            0 -> item {
                                FormSection(title = "Basic Information") {
                                    BasicInfoSection(
                                        basicInfo = inputState.basicInfo,
                                        onBasicInfoChange = viewModel::updateBasicInfo
                                    )
                                }
                            }

                            1 -> item {
                                FormSection(title = "Location") {
                                    LocationEditSection(
                                        latitude = inputState.latitude,
                                        longitude = inputState.longitude,
                                        onLocationChange = viewModel::updateLocation
                                    )
                                }
                            }

                            2 -> {
                                item {
                                    FormSection(title = "Operating Hours") {
                                        // Just the section header
                                    }
                                }

                                // The selector content
                                item {
                                    EnhancedOperatingHoursSelector(
                                        operatingHours = inputState.operatingHours,
                                        onOperatingHoursChange = viewModel::updateOperatingHours,
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                }
                            }

                            3 -> item {
                                FormSection(title = "Accessibility") {
                                    AccessibilitySection(
                                        accessibility = inputState.accessibility,
                                        onAccessibilityChange = viewModel::updateAccessibility
                                    )
                                }
                            }

                            4 -> item {
                                FormSection(title = "Facilities") {
                                    FacilitiesSection(
                                        facilities = inputState.facilities,
                                        onFacilitiesChange = viewModel::updateFacilities
                                    )
                                }
                            }
                        }

                        item {
                            Spacer(modifier = Modifier.height(16.dp))
                        }

                        item {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                if (currentSection > 0) {
                                    Button(
                                        onClick = { currentSection-- },
                                        modifier = Modifier
                                            .weight(1f)
                                            .padding(end = 8.dp)
                                    ) {
                                        Text("Previous")
                                    }
                                }

                                if (currentSection < sections.size - 1) {
                                    Button(
                                        onClick = { currentSection++ },
                                        modifier = Modifier
                                            .weight(1f)
                                            .padding(start = if (currentSection > 0) 8.dp else 0.dp)
                                    ) {
                                        Text("Next")
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        if (showDeleteConfirmation && state is AddBorderPointState.Input) {
            AlertDialog(
                onDismissRequest = { showDeleteConfirmation = false },
                title = { Text("Delete Border Point") },
                text = {
                    Text("Are you sure you want to delete ${(state as AddBorderPointState.Input).basicInfo.name}? This action is destructive and cannot be reversed.")
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            deletionComplete = true
                            viewModel.deleteBorderPoint()
                            showDeleteConfirmation = false
                        },
                        colors = ButtonDefaults.textButtonColors(
                            contentColor = MaterialTheme.colorScheme.error
                        )
                    ) {
                        Text("Delete")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDeleteConfirmation = false }) {
                        Text("Cancel")
                    }
                }
            )
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