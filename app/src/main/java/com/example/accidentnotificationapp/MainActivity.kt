package com.example.accidentnotificationapp

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.core.app.ActivityCompat
import com.example.accidentnotificationapp.data.UserPreferences
import com.example.accidentnotificationapp.detection.MainViewModel
import com.example.accidentnotificationapp.navigation.UserNavigation
import com.example.accidentnotificationapp.ui.theme.AccidentNotificationAppTheme
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import dagger.hilt.android.AndroidEntryPoint
import java.util.Locale

private const val LOCATION_REQUEST_CODE = 200
private var address: String = ""
private var accident = false


@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationManager: LocationManager
	@RequiresApi(Build.VERSION_CODES.TIRAMISU)
	override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val userPreferences = UserPreferences(this)
        setContent {
            AccidentNotificationAppTheme {
                fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
                locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
                if (!isLocationEnabled()) showLocationSettingsDialog()
                val context = this
                LaunchedEffect(Unit) {
                    if (ActivityCompat.checkSelfPermission(
                            context,
                            Manifest.permission.ACCESS_FINE_LOCATION
                        ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                            context,
                            Manifest.permission.ACCESS_COARSE_LOCATION
                        ) != PackageManager.PERMISSION_GRANTED
                    ) {
                        ActivityCompat.requestPermissions(
                            (context as Activity),
                            arrayOf(
                                Manifest.permission.ACCESS_FINE_LOCATION,
                                Manifest.permission.ACCESS_COARSE_LOCATION
                            ),
                            LOCATION_REQUEST_CODE
                        )
                    }
                    if (ActivityCompat.checkSelfPermission(
                            context,
                            Manifest.permission.ACCESS_FINE_LOCATION
                        ) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                            context,
                            Manifest.permission.ACCESS_COARSE_LOCATION
                        ) == PackageManager.PERMISSION_GRANTED
                    ) {
                        fusedLocationClient.getCurrentLocation(
                            Priority.PRIORITY_HIGH_ACCURACY,
                            null
                        )
                            .addOnSuccessListener { location: Location? ->
                                location?.let {
                                    val geocoder = Geocoder(context, Locale.getDefault())
                                    geocoder.getFromLocation(
                                        it.latitude, it.longitude, 1
                                    ) { addresses ->
                                        if (addresses.isNotEmpty()) {
                                            address = addresses[0].getAddressLine(0)
                                        } else {
                                            Toast.makeText(
                                                this@MainActivity,
                                                "Address not found",
                                                Toast.LENGTH_LONG
                                            ).show()
                                        }
                                    }
                                }
                            }
                        
                    }
                    else Toast.makeText(context, "Please provide Location permissions", Toast.LENGTH_SHORT).show()
                }
                val viewModel: MainViewModel by viewModels()
                val accidentDetected by viewModel::accidentDetected
                val t = remember { mutableStateOf(false) }
                LaunchedEffect(accidentDetected) { t.value = accidentDetected }
                accident = t.value
                UserApp(userPreferences)
            }
        }
        
    }
    
    private fun showLocationSettingsDialog() {
        val dialogBuilder = AlertDialog.Builder(this)
        dialogBuilder.setTitle("Location Services Required")
        dialogBuilder.setMessage("Please enable location services to use this app.")
        dialogBuilder.setPositiveButton("Enable") { _, _ ->
            startActivityForResult(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS), 0,null)
        }
        dialogBuilder.setNegativeButton("Cancel") { dialog, _ ->
            dialog.dismiss()
        }
        val dialog = dialogBuilder.create()
        dialog.show()
    }
    private fun isLocationEnabled(): Boolean {
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
    }
}

@Composable
fun UserApp(userPreferences: UserPreferences) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            UserNavigation(userPreferences)
        }
    }
}

fun getAddress(): String {
    return address
}

fun isAccidentHappened(): Boolean {
    return accident
}