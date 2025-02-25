package com.example.passporter

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.compose.rememberNavController
import com.example.passporter.presentation.navigation.NavGraph
import com.example.passporter.presentation.navigation.Screen
import com.example.passporter.presentation.theme.PassPorterTheme
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.gms.maps.MapsInitializer
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()
    private lateinit var googleSignInClient: GoogleSignInClient


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setupGoogleSignIn()
        initializeGoogleServices()

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
                val isAuthenticated by viewModel.isAuthenticated.collectAsState()
                val navController = rememberNavController()
                NavGraph(
                    navController = navController,
                    startDestination = if (isAuthenticated) Screen.Map.route else Screen.Auth.route
                )
            }
        }
    }

    private fun setupGoogleSignIn() {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)
    }

    private fun initializeGoogleServices() {
        val availability = GoogleApiAvailability.getInstance()
        val resultCode = availability.isGooglePlayServicesAvailable(this)
        if (resultCode != com.google.android.gms.common.ConnectionResult.SUCCESS) {
            availability.getErrorDialog(this, resultCode, 9000)?.show()
            return
        }

        MapsInitializer.initialize(this, MapsInitializer.Renderer.LATEST) { }
    }
}