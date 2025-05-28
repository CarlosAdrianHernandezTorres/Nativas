package com.ipn.practica6.screens

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.ipn.practica6.viewmodel.BluetoothViewModel
import kotlinx.coroutines.delay

@Composable
fun BluetoothDiscoveryScreen(bluetoothViewModel: BluetoothViewModel) {
    val context = LocalContext.current
    val devices by bluetoothViewModel.discoveredDevices.collectAsState()
    val isConnecting by bluetoothViewModel.isConnecting.collectAsState()
    val connectionError by bluetoothViewModel.connectionError.collectAsState()
    val isConnected by bluetoothViewModel.isClientConnected.collectAsState()

    LaunchedEffect(Unit) {
        while(true) {
            bluetoothViewModel.startDiscovery(context)
            delay(15000)  // espera 15 segundos antes de buscar de nuevo
        }
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Button(onClick = { bluetoothViewModel.startDiscovery(context) }) {
            Text("Buscar dispositivos")
        }
        Spacer(modifier = Modifier.height(16.dp))

        Text("Dispositivos encontrados:", style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(8.dp))

        LazyColumn(modifier = Modifier.weight(1f)) {
            items(devices, key = { it.address }) { device ->
                val deviceName = if (ContextCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED) {
                    device.name ?: "Desconocido"
                } else {
                    "Nombre no disponible"
                }
                Text(
                    "$deviceName - ${device.address}",
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { bluetoothViewModel.connectToDevice(device, context) }
                        .padding(8.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (isConnecting) {
            Text("Conectando...", modifier = Modifier.padding(8.dp))
        } else if (connectionError != null) {
            Text(
                text = "Error: $connectionError",
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(8.dp)
            )
        } else if (isConnected) {
            Text(
                text = "Conexión establecida correctamente",
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(8.dp)
            )
        } else {
            Text(
                text = "Sin Conexión",
                modifier = Modifier.padding(8.dp)
            )
        }
    }
}