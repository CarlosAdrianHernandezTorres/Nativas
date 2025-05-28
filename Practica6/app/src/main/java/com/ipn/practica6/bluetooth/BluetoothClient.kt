package com.ipn.practica6.bluetooth

import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.util.Log
import java.io.IOException
import java.util.*
import androidx.annotation.RequiresPermission
import android.Manifest

class BluetoothClient(
    private val device: BluetoothDevice,
    private val uuid: UUID,
    private val onConnectionSuccess: () -> Unit,
    private val onConnectionClosed: () -> Unit,
    private val onConnectionFailed: (Exception) -> Unit
) : Thread() {

    private var socket: BluetoothSocket? = null
    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    override fun run() {
        try {
            socket = device.createRfcommSocketToServiceRecord(uuid)
            socket?.connect()
            onConnectionSuccess()

            val inputStream = socket?.inputStream
            val buffer = ByteArray(1024)
            while (true) {
                val read = inputStream?.read(buffer) ?: -1
                if (read == -1) {
                    onConnectionClosed()  // <-- Notificamos desconexión
                    break
                }
                // Procesa datos si quieres
            }
        } catch (e: IOException) {
            Log.e("BluetoothClient", "Error conectando", e)
            onConnectionFailed(e)
        } finally {
            try {
                socket?.close()
            } catch (closeException: IOException) {
                Log.e("BluetoothClient", "Error cerrando socket", closeException)
            }
        }
    }

    fun cancel() {
        try {
            socket?.close()
            onConnectionClosed()
        } catch (e: IOException) {
            Log.e("BluetoothClient", "Error cerrando socket", e)
        }
    }

    fun sendFile(bytes: ByteArray) {
        Thread {
            try {
                Log.d("BluetoothClient", "Enviando archivo, tamaño bytes: ${bytes.size}")
                socket?.outputStream?.write(bytes)
                socket?.outputStream?.flush()
                //socket?.outputStream?.close()  // Cerrar el stream para que el servidor detecte fin de datos
                Log.d("BluetoothClient", "Archivo enviado y stream cerrado")
            } catch (e: IOException) {
                Log.e("BluetoothClient", "Error enviando archivo", e)
            }
        }.start()
    }
}