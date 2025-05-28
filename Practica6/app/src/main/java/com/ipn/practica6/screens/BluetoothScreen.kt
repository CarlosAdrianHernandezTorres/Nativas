package com.ipn.practica6.screens

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.content.Intent
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.ipn.practica6.viewmodel.BluetoothViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BluetoothScreen(bluetoothViewModel: BluetoothViewModel, navController: NavController) {
    val context = LocalContext.current

    val permissionsGranted by bluetoothViewModel.permissionsGranted.collectAsState()
    val bluetoothEnabled by bluetoothViewModel.bluetoothEnabled.collectAsState()

    val permissionsLauncher = rememberLauncherForActivityResult(RequestMultiplePermissions()) { perms ->
        val granted = perms.values.all { it }
        bluetoothViewModel.setPermissionsGranted(granted)
        if (!granted) {
            Toast.makeText(context, "Permisos Bluetooth necesarios", Toast.LENGTH_LONG).show()
        }
    }

    val enableBtLauncher = rememberLauncherForActivityResult(StartActivityForResult()) { result ->
        val isEnabled = bluetoothViewModel.isBluetoothEnabled()
        bluetoothViewModel.setBluetoothEnabled(isEnabled)

        if (!isEnabled) {
            Toast.makeText(context, "Bluetooth debe estar activado", Toast.LENGTH_LONG).show()
        }
    }

    LaunchedEffect(Unit) {
        val missing = bluetoothViewModel.checkPermissions(context)
        if (missing.isNotEmpty()) {
            permissionsLauncher.launch(missing.toTypedArray())
        } else {
            bluetoothViewModel.setPermissionsGranted(true)
        }
    }

    LaunchedEffect(permissionsGranted) {
        if (permissionsGranted) {
            if (!bluetoothViewModel.isBluetoothSupported) {
                Toast.makeText(context, "Bluetooth no compatible", Toast.LENGTH_LONG).show()
            } else if (!bluetoothEnabled) {
                val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
                enableBtLauncher.launch(enableBtIntent)
            } else {
                bluetoothViewModel.setBluetoothEnabled(true)
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Bluetooth Configuración") })
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            when {
                !permissionsGranted -> Text("Solicitando permisos Bluetooth...")
                !bluetoothEnabled -> Text("Por favor, activa Bluetooth para continuar")
                else -> {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Bluetooth activado y permisos concedidos.\nListo para compartir archivos.", modifier = Modifier.padding(bottom = 24.dp))

                        Button(
                            onClick = { navController.navigate("bluetooth_discovery") },
                            enabled = bluetoothEnabled
                        ) {
                            Text("Ir a Búsqueda Bluetooth")
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = { navController.navigate("bluetooth_server") },
                            enabled = bluetoothEnabled
                        ) {
                            Text("Controlar Servidor Bluetooth")
                        }
                    }
                }
            }
        }
    }
}