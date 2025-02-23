package com.example.passporter.presentation.feature.detail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.example.passporter.presentation.feature.detail.components.DetailContent
import com.example.passporter.presentation.feature.map.BorderDetailsState
import com.example.passporter.presentation.feature.map.BorderDetailsViewModel
import com.example.passporter.presentation.navigation.Screen

@Composable
fun BorderDetailsScreen(
    onNavigateBack: () -> Unit,
    navController: NavHostController,
    viewModel: BorderDetailsViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.refresh()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        when (val currentState = state) {
            is BorderDetailsState.Loading -> {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center)
                )
            }

            is BorderDetailsState.Error -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = currentState.message,
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.error
                    )
                    Button(
                        onClick = { viewModel.retry() },
                        modifier = Modifier.padding(top = 8.dp)
                    ) {
                        Text("Retry")
                    }
                }
            }

            is BorderDetailsState.Success -> {
                val borderPoint = currentState.borderPoint

                DetailContent(
                    borderPoint = borderPoint,
                    onNavigateUp = onNavigateBack,
                    onEditClick = {
                        navController.navigate(Screen.EditBorderPoint.createRoute(borderPoint.id))
                    }
                )
            }

            else -> {}
        }
    }
}