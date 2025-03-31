package com.ipn.practica2.ui

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.ipn.practica2.ui.screens.AdminDashboardScreen
import com.ipn.practica2.ui.screens.LoginScreen
import com.ipn.practica2.ui.screens.ProfileScreen
import com.ipn.practica2.ui.screens.RegisterScreen
import com.ipn.practica2.ui.screens.SplashScreen

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "splash") {
        composable("splash") { SplashScreen(navController) }
        composable("login") { LoginScreen(navController) }
        composable("register") { RegisterScreen(navController) }
        composable("profile") { ProfileScreen(navController) }
        composable("admin_dashboard") { AdminDashboardScreen(navController) }
    }
}