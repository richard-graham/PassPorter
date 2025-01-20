package com.example.passporter

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.passporter.presentation.feature.map.MapScreen
import com.example.passporter.presentation.theme.PassPorterTheme
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.gms.maps.MapsInitializer
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val availability = GoogleApiAvailability.getInstance()
        val resultCode = availability.isGooglePlayServicesAvailable(this)
        if (resultCode != com.google.android.gms.common.ConnectionResult.SUCCESS) {
            availability.getErrorDialog(this, resultCode, 9000)?.show()
            return
        }

        MapsInitializer.initialize(this, MapsInitializer.Renderer.LATEST) { }

        enableEdgeToEdge()
        setContent {
            PassPorterTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                   MapScreen(
                       modifier = Modifier.padding(innerPadding),
                       onNavigateToDetail = {}
                   )
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    PassPorterTheme {
        Greeting("Android")
    }
}