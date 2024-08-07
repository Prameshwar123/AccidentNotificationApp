package com.example.accidentnotificationapp.navigation

import androidx.compose.ui.input.key.Key.Companion.Home
import java.lang.IllegalArgumentException

enum class UserScreens {
    SplashScreen,
    LoginScreen,
    HomeScreen;
    companion object {
        fun fromRoute(route: String): UserScreens
            = when(route.substringBefore("/")) {
                SplashScreen.name -> SplashScreen
                LoginScreen.name -> LoginScreen
                HomeScreen.name -> HomeScreen
                else -> throw IllegalArgumentException("Route $route is not recognized")
            }
    }
}