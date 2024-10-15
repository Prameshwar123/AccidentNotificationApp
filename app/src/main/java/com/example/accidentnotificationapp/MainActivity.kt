package com.example.accidentnotificationapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
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
import com.example.accidentnotificationapp.data.UserPreferences
import com.example.accidentnotificationapp.detection.MainViewModel
import com.example.accidentnotificationapp.navigation.UserNavigation
import com.example.accidentnotificationapp.ui.theme.AccidentNotificationAppTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val userPreferences = UserPreferences(this)
        
        setContent {
            AccidentNotificationAppTheme {
                val viewModel: MainViewModel by viewModels()
                val accidentDetected by viewModel::accidentDetected
                val t = remember { mutableStateOf(false) }
                LaunchedEffect(accidentDetected) { t.value = accidentDetected }
                UserApp(t.value, userPreferences)
            }
        }
    }
}

@Composable
fun UserApp(value: Boolean, userPreferences: UserPreferences) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            UserNavigation(value, userPreferences)
        }
    }
}
