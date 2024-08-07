package com.example.accidentnotificationapp.detection

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlin.math.sqrt

@HiltViewModel
class MainViewModel @Inject constructor(
    @Accelerometer private val accelerometerSensor: MeasurableSensor,
    @Gyroscope private val gyroscopeSensor: MeasurableSensor,
) : ViewModel() {

    var accelerometerData by mutableStateOf(listOf<Float>())
        private set

    var gyroscopeData by mutableStateOf(listOf<Float>())
        private set

    var accidentDetected by mutableStateOf(false)
        private set

    init {
        accelerometerSensor.startListening()
        gyroscopeSensor.startListening()
        accelerometerSensor.setOnSensorValuesChangedListener { values ->
            if (values.size == 3) {
                Log.d("MainViewModel", "Accelerometer data: $accelerometerData")
                accelerometerData = values.take(3)
                checkForAccident()
            }
        }
        gyroscopeSensor.setOnSensorValuesChangedListener { values ->
            if (values.size == 3) {
                Log.d("MainViewModel", "Gyroscope data: $gyroscopeData")
                gyroscopeData = values.take(3)
                checkForAccident()
            }
        }
    }
    private fun checkForAccident() {
        if (accelerometerData.size == 3 && gyroscopeData.size == 3) {
            val ax = accelerometerData[0]
            val ay = accelerometerData[1]
            val az = accelerometerData[2]

            val gx = gyroscopeData[0]
            val gy = gyroscopeData[1]
            val gz = gyroscopeData[2]

            val accelerationMagnitude = sqrt(ax * ax + ay * ay + az * az)
            val gyroscopeMagnitude = sqrt(gx * gx + gy * gy + gz * gz)
            val accelerationThreshold = 8.6f
//            val gyroscopeThreshold = 3.0f
            Log.d("valuesinside", "Acceleration magnitude: $accelerationMagnitude")
            Log.d("valuesinside", "Gyroscope magnitude: $gyroscopeMagnitude")
//            if (accelerationMagnitude > accelerationThreshold && gyroscopeMagnitude > gyroscopeThreshold) {
            if (accelerationMagnitude > accelerationThreshold) {
                Log.d(
                    "Accident",
                    "Accident detected with acceleration: $accelerationMagnitude, gyroscope: $gyroscopeMagnitude"
                )
                accidentDetected = true
            } else {
                accidentDetected = false
            }
        }
    }
}