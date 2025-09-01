package com.example.accidentnotificationapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.example.accidentnotificationapp.data.UserPreferences
import com.example.accidentnotificationapp.navigation.UserNavigation
import com.example.accidentnotificationapp.ui.theme.AccidentNotificationAppTheme
import dagger.hilt.android.AndroidEntryPoint

private var address: String = ""
private var accident = false

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private lateinit var locationHelper: LocationHelper
	override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val userPreferences = UserPreferences(this)
        setContent {
            AccidentNotificationAppTheme {
                locationHelper = LocationHelper(this@MainActivity, applicationContext)
                UserApp()
                address = locationHelper.getAddress()
                accident = locationHelper.isAccidentHappened()
            }
        }
    }
}

@Composable
fun UserApp() {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            UserNavigation()
        }
    }
}

fun getAddress(): String = address

fun isAccidentHappened(): Boolean = accident

