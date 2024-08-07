package com.example.accidentnotificationapp.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.accidentnotificationapp.screens.homescreen.Home
import com.example.accidentnotificationapp.screens.UserSplashScreen

@Composable
fun UserNavigation(value: Boolean){
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = UserScreens.SplashScreen.name) {
        composable(UserScreens.SplashScreen.name){
            UserSplashScreen(navController = navController)
        }
//        composable(UserScreens.LoginScreen.name) {
//            UserLoginScreen(navController = navController)
//        }
        composable(UserScreens.HomeScreen.name) {
//            val homeViewModel = hiltViewModel<HomeScreenViewModel>()
            Home(navController = navController, value = value)
        }
    }
}