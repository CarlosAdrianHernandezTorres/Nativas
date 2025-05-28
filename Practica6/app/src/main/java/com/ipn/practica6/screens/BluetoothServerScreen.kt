package com.ipn.practica6.screens

import android.bluetooth.BluetoothAdapter
import android.content.Intent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ipn.practica6.viewmodel.BluetoothViewModel

@Composable
fun BluetoothServerScreen(bluetoothViewModel: BluetoothViewModel = viewModel()) {
    val isServerRunning by bluetoothViewModel.isServerRunning.collectAsState()
    val context = LocalContext.current
    val isServerConnected by bluetoothViewModel.isServerConnected.collectAsState()


    LaunchedEffect(Unit) {
        val discoverableIntent = Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE).apply {
            putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300) // 5 minutos visible
        }
        context.startActivity(discoverableIntent)
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Servidor Bluetooth",
            style = MaterialTheme.typography.titleLarge
        )

        Button(
            onClick = {
                if (isServerRunning) {
                    bluetoothViewModel.stopServer()
                } else {
                    bluetoothViewModel.startServer(context) // Le pasas el contexto aquí
                }
            }
        ) {
            Text(if (isServerRunning) "Detener Servidor" else "Iniciar Servidor")
        }

        Text(
            text = when {
                isServerConnected -> "Servidor conectado con cliente"
                isServerRunning -> "Servidor corriendo, esperando conexiones..."
                else -> "Servidor detenido"
            }
        )
    }
}