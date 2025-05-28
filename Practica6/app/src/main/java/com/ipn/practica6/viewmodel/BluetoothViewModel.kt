package com.ipn.practica6.viewmodel

import android.Manifest
import android.app.Application
import android.bluetooth.BluetoothAdapter
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import androidx.core.content.ContextCompat
import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.Intent
import android.content.IntentFilter
import android.util.Log
import androidx.lifecycle.viewModelScope
import com.ipn.practica6.bluetooth.BluetoothClient
import com.ipn.practica6.bluetooth.BluetoothServer
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File
import java.util.UUID

class BluetoothViewModel(application: Application) : AndroidViewModel(application) {

    private val _permissionsGranted = MutableStateFlow(false)
    val permissionsGranted: StateFlow<Boolean> = _permissionsGranted

    private val _bluetoothEnabled = MutableStateFlow(false)
    val bluetoothEnabled: StateFlow<Boolean> = _bluetoothEnabled

    private val bluetoothAdapter: BluetoothAdapter? = BluetoothAdapter.getDefaultAdapter()

    val isBluetoothSupported: Boolean
        get() = bluetoothAdapter != null

    private val _discoveredDevices = MutableStateFlow<List<BluetoothDevice>>(emptyList())
    val discoveredDevices: StateFlow<List<BluetoothDevice>> = _discoveredDevices

    private var bluetoothClientThread: BluetoothClient? = null
    private var bluetoothServerThread: BluetoothServer? = null

    private val _isConnecting = MutableStateFlow(false)
    val isConnecting: StateFlow<Boolean> = _isConnecting

    private val _connectionError = MutableStateFlow<String?>(null)
    val connectionError: StateFlow<String?> = _connectionError

    private val discoveredDevicesSet = mutableSetOf<BluetoothDevice>()

    private val _isServerRunning = MutableStateFlow(false)
    val isServerRunning: StateFlow<Boolean> = _isServerRunning

    private val _isClientConnected = MutableStateFlow(false)
    val isClientConnected: StateFlow<Boolean> = _isClientConnected

    private val _isServerConnected = MutableStateFlow(false)
    val isServerConnected: StateFlow<Boolean> = _isServerConnected

    private var loggingJob: Job? = null

    init {
        startLoggingConnectionStatus()
    }

    fun startServer(context: Context) {
        if (_isServerRunning.value) return

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                _connectionError.value = "Permiso BLUETOOTH_CONNECT no concedido"
                return
            }
        }

        val uuid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")

        bluetoothServerThread = BluetoothServer(
            bluetoothAdapter ?: return,
            uuid,
            getApplication(),
            onConnectionAccepted = { socket ->
                Log.d("BluetoothViewModel", "Servidor: conexión aceptada")
                viewModelScope.launch { _isServerConnected.emit(true)
                    Log.d("BSServer1", "isServerConnected: ${_isServerConnected.value}, isClientConnected: ${_isClientConnected.value}")}
            },
            onConnectionClosed = {
                Log.d("BluetoothViewModel", "Servidor: conexión cerrada")
                viewModelScope.launch { _isServerConnected.emit(false)
                    Log.d("BSServer2", "isServerConnected: ${_isServerConnected.value}, isClientConnected: ${_isClientConnected.value}")
                }
            },
            onError = { e ->
                Log.d("BluetoothViewModel", "Servidor: conexión erronea")
                viewModelScope.launch { _connectionError.emit(e.message)
                    Log.d("BSServer3", "isServerConnected: ${_isServerConnected.value}, isClientConnected: ${_isClientConnected.value}")}
            }
        )
        bluetoothServerThread?.startServer()
        _isServerRunning.value = true
    }

    fun stopServer() {
        bluetoothServerThread?.cancel()
        _isServerRunning.value = false
        viewModelScope.launch {
            _isServerConnected.emit(false)
        }
    }

    private val discoveryReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            when (intent?.action) {
                BluetoothDevice.ACTION_FOUND -> {
                    val device: BluetoothDevice? = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE)
                    device?.let {
                        viewModelScope.launch {
                            if (discoveredDevicesSet.add(device)) {
                                _discoveredDevices.emit(discoveredDevicesSet.toList())
                            }
                        }
                    }
                }
                BluetoothAdapter.ACTION_DISCOVERY_FINISHED -> {
                    // Opcional: manejar cuando termine el descubrimiento
                }
            }
        }
    }

    fun startDiscovery(context: Context) {
        stopDiscovery(context) // Primero para evitar múltiples receivers
        discoveredDevicesSet.clear()
        _discoveredDevices.value = emptyList()


        bluetoothAdapter?.let { adapter ->
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_SCAN) == PackageManager.PERMISSION_GRANTED) {
                _discoveredDevices.value = emptyList()
                context.registerReceiver(discoveryReceiver, IntentFilter().apply {
                    addAction(BluetoothDevice.ACTION_FOUND)
                    addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED)
                })
                adapter.startDiscovery()
            } else {
                // Opcional: notificar que falta permiso BLUETOOTH_SCAN
            }
        }
    }

    fun stopDiscovery(context: Context) {
        // Evitar que el discovery cierre la conexión al salir de la pantalla
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_SCAN) == PackageManager.PERMISSION_GRANTED) {
            bluetoothAdapter?.cancelDiscovery()
        }
        try {
            context.unregisterReceiver(discoveryReceiver)
        } catch (e: IllegalArgumentException) {
            // El receptor no estaba registrado, ignorar
        }
        // Asegurar que la conexión se mantenga activa o reconectar si es necesario
        if (_isClientConnected.value || _isServerConnected.value) {
            Log.d("BluetoothStatus", "Manteniendo la conexión activa")
        } else {
            Log.d("BluetoothStatus", "Conexión perdida, intentando reconectar...")
            // Lógica de reconexión aquí si es necesario
        }
    }

    fun connectToDevice(device: BluetoothDevice, context: Context) {
        if (_isConnecting.value) return

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                _connectionError.value = "Permiso BLUETOOTH_CONNECT no concedido"
                return
            }
        }

        _isConnecting.value = true
        _isClientConnected.value = false  // Reseteamos conexión

        val uuid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")

        bluetoothClientThread = BluetoothClient(
            device,
            uuid,
            onConnectionSuccess = {
                Log.d("BluetoothViewModel", "Cliente: conexión exitosa")
                viewModelScope.launch {
                    _isConnecting.emit(false)
                    _connectionError.emit(null)
                    _isClientConnected.emit(true)
                    Log.d("BSClient1", "isServerConnected: ${_isServerConnected.value}, isClientConnected: ${_isClientConnected.value}")
                }
            },
            onConnectionClosed = {
                Log.d("BluetoothViewModel", "Cliente: conexión cerrada")
                viewModelScope.launch { _isClientConnected.emit(false)
                    Log.d("BSClient2", "isServerConnected: ${_isServerConnected.value}, isClientConnected: ${_isClientConnected.value}")}
            },
            onConnectionFailed = { e ->
                Log.d("BluetoothViewModel", "Cliente: conexión fallida: ${e.message}")
                viewModelScope.launch {
                    _isConnecting.emit(false)
                    _connectionError.emit(e.message)
                    _isClientConnected.emit(false)
                    Log.d("BSClient3", "isServerConnected: ${_isServerConnected.value}, isClientConnected: ${_isClientConnected.value}")
                }
            }
        )
        bluetoothClientThread?.start()
    }

    fun disconnect() {
        bluetoothClientThread?.cancel()
        _isConnecting.value = false
        _isClientConnected.value = false  // Marcamos desconexión
    }

    fun checkPermissions(context: Context): List<String> {
        val needed = mutableListOf(
            Manifest.permission.BLUETOOTH,
            Manifest.permission.BLUETOOTH_ADMIN,
            Manifest.permission.ACCESS_FINE_LOCATION
        )
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            needed.add(Manifest.permission.BLUETOOTH_SCAN)
            needed.add(Manifest.permission.BLUETOOTH_CONNECT)
        }
        val missing = needed.filter {
            ContextCompat.checkSelfPermission(context, it) != PackageManager.PERMISSION_GRANTED
        }
        return missing
    }

    fun setPermissionsGranted(granted: Boolean) {
        viewModelScope.launch {
            _permissionsGranted.emit(granted)
        }
    }

    fun checkBluetoothStatus() {
        viewModelScope.launch {
            _bluetoothEnabled.emit(bluetoothAdapter?.isEnabled == true)
        }
    }

    fun setBluetoothEnabled(enabled: Boolean) {
        viewModelScope.launch {
            _bluetoothEnabled.emit(enabled)
        }
    }

    fun isBluetoothEnabled(): Boolean {
        return bluetoothAdapter?.isEnabled == true
    }

    fun sendFile(file: File) {
        Log.d("BluetoothViewModel", "Estado conexión cliente: ${_isClientConnected.value}")
        if (!_isClientConnected.value) {
            Log.d("BluetoothViewModel", "No hay conexión activa, no se puede enviar archivo")
            return
        }
        val bytes = try {
            val data = file.readBytes()
            Log.d("BluetoothViewModel", "Intentando enviar archivo de tamaño: ${data.size}")
            data
        } catch (e: Exception) {
            Log.e("BluetoothViewModel", "Error leyendo archivo", e)
            null
        }
        if (bytes != null) {
            bluetoothClientThread?.sendFile(bytes)
        }
    }
    fun startLoggingConnectionStatus() {
        loggingJob?.cancel() // cancela si ya estaba corriendo

        loggingJob = viewModelScope.launch {
            while (true) {
                Log.d("BS", "isServerConnected: ${_isServerConnected.value}, isClientConnected: ${_isClientConnected.value}")
                delay(5000) // cada 5 segundos
            }
        }
    }

    fun stopLoggingConnectionStatus() {
        loggingJob?.cancel()
        loggingJob = null
    }
}