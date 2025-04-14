package com.ipn.practica3.screens

import android.Manifest
import android.os.Environment
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import java.io.File
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.ActivityCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ipn.practica3.navigation.Screen
import java.net.URLEncoder
import com.ipn.practica3.data.RecentFilesStore
import kotlinx.coroutines.launch
import com.ipn.practica3.data.FavoritesStore
import androidx.compose.material.icons.outlined.StarBorder
import coil.compose.AsyncImage
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import java.nio.file.Paths

@Composable
fun FileManagerScreen(navController: NavController) {
    val context = LocalContext.current
    var currentPath by remember { mutableStateOf(Environment.getExternalStorageDirectory()) }
    var fileList by remember { mutableStateOf(currentPath.listFiles()?.toList() ?: emptyList()) }
    val coroutineScope = rememberCoroutineScope()
    val pathSegments = splitPathManually(currentPath)
    // Solicita permisos si no están concedidos
    LaunchedEffect(true) {
        val hasPermission = ContextCompat.checkSelfPermission(
            context, Manifest.permission.READ_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED

        if (!hasPermission) {
            ActivityCompat.requestPermissions(
                (context as android.app.Activity),
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                100
            )
        }
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        // Botón para regresar
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(text = "Ruta actual:", style = MaterialTheme.typography.titleMedium)
            Button(onClick = {
                val parent = currentPath.parentFile
                if (parent != null) {
                    try {
                        if (parent.canRead()) {
                            val files = parent.listFiles()?.toList()
                            if (files != null) {
                                currentPath = parent
                                fileList = files
                            } else {
                                Toast.makeText(context, "No se pudo acceder a la carpeta padre", Toast.LENGTH_SHORT).show()
                            }
                        } else {
                            Toast.makeText(context, "No tienes permiso para esta carpeta", Toast.LENGTH_SHORT).show()
                        }
                    } catch (e: Exception) {
                        Toast.makeText(context, "Error al intentar subir de carpeta", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    navController.popBackStack()
                }
            }) {
                Text("⬅️ Regresar")
            }
        }

        Spacer(modifier = Modifier.height(8.dp))
        Text(text = currentPath.path, style = MaterialTheme.typography.bodySmall)

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(vertical = 4.dp)
        ) {
            var accumulatedPath = File("/")

            for ((index, segment) in pathSegments.withIndex()) {
                accumulatedPath = File(accumulatedPath, segment.toString())

                TextButton(onClick = {
                    if (accumulatedPath.exists() && accumulatedPath.isDirectory) {
                        currentPath = accumulatedPath
                        fileList = accumulatedPath.listFiles()?.toList() ?: emptyList()
                    }
                }) {
                    Text(segment.toString())
                }

                if (index < pathSegments.size - 1) {
                    Text(" > ", style = MaterialTheme.typography.bodySmall)
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))
        LazyColumn(modifier = Modifier.fillMaxSize()) {
            // Añade opción ".." para subir
            if (currentPath.parentFile != null) {
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                currentPath = currentPath.parentFile!!
                                fileList = currentPath.listFiles()?.toList() ?: emptyList()
                            }
                            .padding(8.dp)
                    ) {
                        Text("📁 .. (Subir)", style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }

            items(fileList.size) { index ->
                val file = fileList[index]
                val favorites by FavoritesStore.getFavorites(context).collectAsState(initial = emptySet())
                val isFavorite = favorites.contains(file.absolutePath)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    // 📄 Nombre del archivo (y click para abrirlo)
                    if (file.extension.lowercase() in listOf("jpg", "jpeg", "png")) {
                        AsyncImage(
                            model = file,
                            contentDescription = "Imagen",
                            modifier = Modifier
                                .size(48.dp)
                                .padding(end = 8.dp)
                        )
                        Text(text = file.name, modifier = Modifier.weight(1f))
                    }
                    else{
                    Text(
                        text = if (file.isDirectory) "📁 ${file.name}" else "📄 ${file.name}",
                        modifier = Modifier
                            .weight(1f)
                            .clickable {
                                if (file.isDirectory) {
                                    try {
                                        if (file.canRead()) {
                                            val files = file.listFiles()?.toList()
                                            if (files != null) {
                                                currentPath = file
                                                fileList = files
                                            } else {
                                                Toast.makeText(context, "No se pudo leer esta carpeta", Toast.LENGTH_SHORT).show()
                                            }
                                        } else {
                                            Toast.makeText(context, "Acceso denegado a la carpeta", Toast.LENGTH_SHORT).show()
                                        }
                                    } catch (e: Exception) {
                                        Toast.makeText(context, "Error al acceder a la carpeta", Toast.LENGTH_SHORT).show()
                                    }
                                } else {
                                    val extension = file.extension.lowercase()
                                    val encodedPath = URLEncoder.encode(file.absolutePath, "utf-8")
                                    coroutineScope.launch {
                                        RecentFilesStore.addFileToHistory(context, file.absolutePath)
                                    }
                                    when {
                                        extension in listOf("txt", "md") -> navController.navigate("${Screen.TextViewer.route}/$encodedPath")
                                        extension in listOf("jpg", "jpeg", "png") -> navController.navigate("${Screen.ImageViewer.route}/$encodedPath")
                                        else -> {
                                            try {
                                                val uri = androidx.core.content.FileProvider.getUriForFile(
                                                    context,
                                                    "${context.packageName}.provider",
                                                    file
                                                )
                                                val intent = android.content.Intent(android.content.Intent.ACTION_VIEW).apply {
                                                    setDataAndType(uri, getMimeType(file.extension))
                                                    flags = android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION
                                                }
                                                context.startActivity(android.content.Intent.createChooser(intent, "Abrir con"))
                                            } catch (e: Exception) {
                                                Toast.makeText(context, "No se pudo abrir el archivo", Toast.LENGTH_SHORT).show()
                                            }
                                        }
                                    }
                                }
                            }
                    )}

                    // ⭐ Botón para marcar/desmarcar como favorito
                    IconButton(onClick = {
                        coroutineScope.launch {
                            FavoritesStore.toggleFavorite(context, file.absolutePath)
                        }
                    }) {
                        Icon(
                            imageVector = if (isFavorite) Icons.Filled.Star else Icons.Outlined.StarBorder,
                            contentDescription = if (isFavorite) "Quitar de favoritos" else "Agregar a favoritos"
                        )
                    }
                }
            }
        }
    }
}

fun getMimeType(extension: String): String {
    return when (extension.lowercase()) {
        "pdf" -> "application/pdf"
        "doc", "docx" -> "application/msword"
        "xls", "xlsx" -> "application/vnd.ms-excel"
        "ppt", "pptx" -> "application/vnd.ms-powerpoint"
        "mp4" -> "video/mp4"
        "mp3" -> "audio/mpeg"
        else -> "*/*"
    }
}

fun splitPathManually(path: File): List<String> {
    return path.absolutePath
        .removePrefix("/")
        .split("/")
        .filter { it.isNotBlank() }
}