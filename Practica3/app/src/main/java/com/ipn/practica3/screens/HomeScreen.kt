package com.ipn.practica3.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.ipn.practica3.navigation.Screen
import com.ipn.practica3.ui.theme.AppTheme
import com.ipn.practica3.viewmodel.ThemeViewModel

@Composable
fun HomeScreen(navController: NavController, themeViewModel: ThemeViewModel) {
    val selectedTheme = themeViewModel.currentTheme.value

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("🎨 Tema actual: ${if (selectedTheme == AppTheme.GUINDA) "Guinda (IPN)" else "Azul (ESCOM)"}")

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(onClick = { themeViewModel.setTheme(AppTheme.GUINDA) }) {
                Text("Guinda")
            }
            Button(onClick = { themeViewModel.setTheme(AppTheme.AZUL) }) {
                Text("Azul")
            }
        }

        Divider()

        Button(onClick = { navController.navigate(Screen.FileManager.route) }) {
            Text("📁 Gestor de Archivos")
        }
        Button(onClick = { navController.navigate(Screen.CameraMic.route) }) {
            Text("📷 Cámara y Micrófono")
        }
        Button(onClick = { navController.navigate(Screen.AudioGallery.route) }) {
            Text("🎧 Ver Audios")
        }
        Button(onClick = { navController.navigate(Screen.RecentFiles.route) }) {
            Text("🕘 Archivos Recientes")
        }
        Button(onClick = { navController.navigate(Screen.FavoriteFiles.route) }) {
            Text("⭐ Ver Favoritos")
        }
    }
}