package com.example.accidentnotificationapp.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.accidentnotificationapp.data.UserPreferences
import com.example.accidentnotificationapp.screens.UserLoginScreen
import com.example.accidentnotificationapp.screens.Home
import com.example.accidentnotificationapp.screens.UserSplashScreen

@Composable
fun UserNavigation(userPreferences: UserPreferences){
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = UserScreens.SplashScreen.name) {
        composable(UserScreens.SplashScreen.name){
            UserSplashScreen(navController = navController)
        }
        composable(UserScreens.LoginScreen.name) {
            UserLoginScreen(navController = navController, userPreferences = userPreferences)
        }
        composable(UserScreens.HomeScreen.name) {
            Home(navController = navController, userPreferences = userPreferences)
        }
    }
}