package com.ipn.practica6.screens

import android.media.MediaPlayer
import android.os.Environment
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import java.io.File
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AudioGalleryScreen() {
    val context = LocalContext.current

    val audioDir = context.getExternalFilesDir(Environment.DIRECTORY_MUSIC)
    val audioFiles = remember { mutableStateListOf<File>() }

    var currentlyPlaying by remember { mutableStateOf<File?>(null) }
    var mediaPlayer by remember { mutableStateOf<MediaPlayer?>(null) }
    LaunchedEffect(Unit) {
        val files = audioDir?.listFiles()?.filter { it.extension == "3gp" } ?: emptyList()
        audioFiles.clear()
        audioFiles.addAll(files)
    }
    Scaffold(
        topBar = {
            TopAppBar(title = { Text("🎧 Audios Grabados") })
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .padding(innerPadding)
                .padding(16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(audioFiles) { file ->
                Card(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    var expanded by remember { mutableStateOf(false) }
                    var showRenameDialog by remember { mutableStateOf(false) }
                    var newName by remember { mutableStateOf("") }

                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f).clickable {
                                mediaPlayer?.stop()
                                mediaPlayer?.release()
                                mediaPlayer = MediaPlayer().apply {
                                    setDataSource(file.absolutePath)
                                    prepare()
                                    start()
                                }
                                currentlyPlaying = file
                            }) {
                                Text(text = file.name, style = MaterialTheme.typography.bodyLarge)
                                if (file == currentlyPlaying) {
                                    Text("⏸️ Reproduciendo...", style = MaterialTheme.typography.bodySmall)
                                }
                            }

                            Box {
                                IconButton(onClick = { expanded = true }) {
                                    Icon(Icons.Default.MoreVert, contentDescription = "Opciones")
                                }

                                DropdownMenu(
                                    expanded = expanded,
                                    onDismissRequest = { expanded = false }
                                ) {
                                    DropdownMenuItem(
                                        text = { Text("📝 Renombrar") },
                                        onClick = {
                                            expanded = false
                                            newName = file.nameWithoutExtension
                                            showRenameDialog = true
                                        }
                                    )
                                    DropdownMenuItem(
                                        text = { Text("🗑️ Eliminar") },
                                        onClick = {
                                            expanded = false
                                            if (file.delete()) {
                                                audioFiles.remove(file)
                                                if (file == currentlyPlaying) {
                                                    currentlyPlaying = null
                                                    mediaPlayer?.release()
                                                }
                                            }
                                            // Recargar lista
                                            currentlyPlaying = null
                                            mediaPlayer?.release()
                                        }
                                    )
                                }
                            }
                        }
                    }

                    if (showRenameDialog) {
                        AlertDialog(
                            onDismissRequest = { showRenameDialog = false },
                            confirmButton = {
                                TextButton(onClick = {
                                    val renamed = File(file.parent, "$newName.3gp")
                                    if (file.renameTo(renamed)) {
                                        val index = audioFiles.indexOf(file)
                                        if (index != -1) {
                                            audioFiles[index] = renamed
                                        }
                                        Toast.makeText(context, "Renombrado", Toast.LENGTH_SHORT).show()
                                    }
                                    showRenameDialog = false
                                }) {
                                    Text("Aceptar")
                                }
                            },
                            dismissButton = {
                                TextButton(onClick = { showRenameDialog = false }) {
                                    Text("Cancelar")
                                }
                            },
                            title = { Text("Renombrar audio") },
                            text = {
                                OutlinedTextField(
                                    value = newName,
                                    onValueChange = { newName = it },
                                    singleLine = true
                                )
                            }
                        )
                    }
                }
            }
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            mediaPlayer?.release()
        }
    }
}