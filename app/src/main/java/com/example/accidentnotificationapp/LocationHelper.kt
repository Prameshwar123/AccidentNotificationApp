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
import android.provider.Settings
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import java.util.Locale

private const val LOCATION_REQUEST_CODE = 200
private var address: String = ""
private var accident = false

class LocationHelper(private val activity: Activity, private val context: Context) {
	
	private var fusedLocationClient: FusedLocationProviderClient =
		LocationServices.getFusedLocationProviderClient(activity)
	
	private var locationManager =
		context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
	
	private var isLocationEnabled: Boolean =
		locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
				locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
	
	init {
		if (!isLocationEnabled) {
			showLocationSettingsDialog()
		} else {
			fetchLocation()
		}
	}
	
	private fun showLocationSettingsDialog() {
		val dialogBuilder = AlertDialog.Builder(context)
		dialogBuilder.setTitle("Location Services Required")
		dialogBuilder.setMessage("Please enable location services to use this app.")
		dialogBuilder.setPositiveButton("Enable") { _, _ ->
			val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
			activity.startActivity(intent)
		}
		dialogBuilder.setNegativeButton("Cancel") { dialog, _ ->
			dialog.dismiss()
		}
		dialogBuilder.create().show()
	}
	private fun fetchLocation() {
		if (ActivityCompat.checkSelfPermission(
				context,
				Manifest.permission.ACCESS_FINE_LOCATION
			) != PackageManager.PERMISSION_GRANTED &&
			ActivityCompat.checkSelfPermission(
				context,
				Manifest.permission.ACCESS_COARSE_LOCATION
			) != PackageManager.PERMISSION_GRANTED
		) {
			ActivityCompat.requestPermissions(
				activity,
				arrayOf(
					Manifest.permission.ACCESS_FINE_LOCATION,
					Manifest.permission.ACCESS_COARSE_LOCATION
				),
				LOCATION_REQUEST_CODE
			)
			return
		}
		
		fusedLocationClient.getCurrentLocation(
			Priority.PRIORITY_HIGH_ACCURACY,
			null
		).addOnSuccessListener { location: Location? ->
			location?.let {
				val geocoder = Geocoder(context, Locale.getDefault())
				val addresses = geocoder.getFromLocation(it.latitude, it.longitude, 1)
				if (!addresses.isNullOrEmpty()) {
					address = addresses[0].getAddressLine(0)
				} else {
					Toast.makeText(
						context,
						"Address not found",
						Toast.LENGTH_LONG
					).show()
				}
			}
		}
	}
	
	fun getAddress(): String = address
	
	fun isAccidentHappened(): Boolean = accident
}
