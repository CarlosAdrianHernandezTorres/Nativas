package com.ipn.practica3.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TextViewerScreen(navController: NavController, filePath: String) {
    val file = remember { File(filePath) }
    var content by remember { mutableStateOf("") }

    // Leer archivo de texto
    LaunchedEffect(filePath) {
        content = try {
            file.readText()
        } catch (e: Exception) {
            "Error al leer el archivo: ${e.message}"
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(file.name) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Text("⬅️")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(modifier = Modifier
            .padding(innerPadding)
            .padding(16.dp)
            .fillMaxSize()
        ) {
            Text(text = content)
        }
    }
}