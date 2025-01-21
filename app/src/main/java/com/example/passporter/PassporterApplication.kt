package com.example.passporter

import android.app.Application
import android.content.ComponentCallbacks2
import android.os.StrictMode
import android.util.Log
import androidx.viewbinding.BuildConfig
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.gms.maps.MapsInitializer
import com.google.firebase.FirebaseApp
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class PassporterApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        // Enable strict mode in debug builds
        if (BuildConfig.DEBUG) {
            StrictMode.setThreadPolicy(
                StrictMode.ThreadPolicy.Builder()
                    .detectDiskReads()
                    .detectDiskWrites()
                    .detectNetwork()
                    .penaltyLog()
                    .build()
            )

            StrictMode.setVmPolicy(
                StrictMode.VmPolicy.Builder()
                    .detectLeakedSqlLiteObjects()
                    .detectLeakedClosableObjects()
                    .detectActivityLeaks()
                    .penaltyLog()
                    .build()
            )
        }

        FirebaseApp.initializeApp(this)

        // Check Google Play Services
        val availability = GoogleApiAvailability.getInstance()
        when (val result = availability.isGooglePlayServicesAvailable(this)) {
            ConnectionResult.SUCCESS -> {
                Log.d("Maps", "Google Play Services is available")
                // Initialize Maps only if Play Services is available
                initializeMaps()
            }
            else -> {
                Log.e("Maps", "Google Play Services not available: ${availability.getErrorString(result)}")
            }
        }
    }

    private fun initializeMaps() {
        try {
            MapsInitializer.initialize(this, MapsInitializer.Renderer.LATEST) { renderer ->
                Log.d("Maps", "Maps initialized with renderer: $renderer")
            }
        } catch (e: Exception) {
            Log.e("Maps", "Error initializing Maps SDK", e)
        }
    }

    override fun onTrimMemory(level: Int) {
        super.onTrimMemory(level)
        when (level) {
            ComponentCallbacks2.TRIM_MEMORY_RUNNING_CRITICAL,
            ComponentCallbacks2.TRIM_MEMORY_COMPLETE -> {
                // Clear non-critical caches and resources
                System.gc()
            }
            ComponentCallbacks2.TRIM_MEMORY_RUNNING_LOW,
            ComponentCallbacks2.TRIM_MEMORY_MODERATE -> {
                // Clear non-essential caches
            }
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onLowMemory() {
        super.onLowMemory()
        // Clear all caches
        System.gc()
    }
}