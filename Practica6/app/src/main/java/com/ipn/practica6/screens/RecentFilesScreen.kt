package com.ipn.practica6.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.ipn.practica6.data.RecentFilesStore
import com.ipn.practica6.navigation.Screen
import kotlinx.coroutines.launch
import java.io.File
import java.net.URLEncoder

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecentFilesScreen(navController: NavController) {
    val context = LocalContext.current
    val recentFiles by RecentFilesStore.getRecentFiles(context).collectAsState(initial = emptySet())
    val coroutineScope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("📂 Archivos recientes") })
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            val sortedPaths = recentFiles.toList().sortedByDescending { File(it).lastModified() }
            item {
                Button(
                    onClick = {
                        coroutineScope.launch {
                            RecentFilesStore.clearHistory(context)
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp)
                ) {
                    Text("🗑 Borrar historial")
                }
            }
            items(sortedPaths, key = { it }) { path ->
                val file = File(path)
                if (file.exists()) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                val extension = file.extension.lowercase()
                                if (extension in listOf("txt", "md")) {
                                    val encodedPath = URLEncoder.encode(file.absolutePath, "utf-8")
                                    navController.navigate("${Screen.TextViewer.route}/$encodedPath")
                                } else if (extension in listOf("jpg", "jpeg", "png")) {
                                    val encodedPath = URLEncoder.encode(file.absolutePath, "utf-8")
                                    navController.navigate("${Screen.ImageViewer.route}/$encodedPath")
                                } else {
                                    // Abrir con otra app
                                }
                            }
                            .padding(8.dp)
                    ) {
                        Text(file.name)
                    }
                }
            }
        }
    }
}