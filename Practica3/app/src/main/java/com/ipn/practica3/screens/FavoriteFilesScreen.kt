package com.ipn.practica3.screens

import androidx.compose.runtime.Composable
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
import com.ipn.practica3.data.FavoritesStore
import com.ipn.practica3.data.RecentFilesStore
import com.ipn.practica3.navigation.Screen
import kotlinx.coroutines.launch
import java.io.File
import java.net.URLEncoder

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoriteFilesScreen(navController: NavController) {
    val context = LocalContext.current
    val favorites by FavoritesStore.getFavorites(context).collectAsState(initial = emptySet())

    Scaffold(
        topBar = { TopAppBar(title = { Text("⭐ Archivos favoritos") }) }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            val files = favorites.toList().map { File(it) }.filter { it.exists() }

            items(files, key = { it.absolutePath }) { file ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            val extension = file.extension.lowercase()
                            val encodedPath = URLEncoder.encode(file.absolutePath, "utf-8")
                            when {
                                extension in listOf("txt", "md") -> navController.navigate("${Screen.TextViewer.route}/$encodedPath")
                                extension in listOf("jpg", "jpeg", "png") -> navController.navigate("${Screen.ImageViewer.route}/$encodedPath")
                                else -> { }
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