package com.ipn.practica2.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.ipn.practica2.data.network.RetrofitInstance
import com.ipn.practica2.datastore.UserPreferences
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

@Composable
fun SplashScreen(navController: NavHostController) {
    val context = LocalContext.current
    val userPrefs = remember { UserPreferences(context) }
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(true) {
        val user = userPrefs.getUserData().first()

        if (user.token.isNotEmpty()) {
            try {
                val secureApi = RetrofitInstance.provideRetrofit(user.token)

                val response = secureApi.getUserById(user.id)

                if (response.isSuccessful) {
                    val destination = if (user.role == "admin") "admin_dashboard" else "profile"
                    navController.navigate(destination) {
                        popUpTo("splash") { inclusive = true }
                    }
                } else {
                    userPrefs.clearData()
                    navController.navigate("login") {
                        popUpTo("splash") { inclusive = true }
                    }
                }
            } catch (e: Exception) {
                userPrefs.clearData()
                navController.navigate("login") {
                    popUpTo("splash") { inclusive = true }
                }
            }
        } else {
            navController.navigate("login") {
                popUpTo("splash") { inclusive = true }
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("✨ Bienvenido a la App CRUD", style = MaterialTheme.typography.headlineMedium)
            Spacer(modifier = Modifier.height(24.dp))
            CircularProgressIndicator()
        }
    }
}