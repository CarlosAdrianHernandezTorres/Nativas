package com.ipn.practica6.bluetooth

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothServerSocket
import android.bluetooth.BluetoothSocket
import android.util.Log
import java.io.IOException
import java.util.UUID
import androidx.annotation.RequiresPermission
import android.Manifest
import android.content.Context
import java.io.File
import java.io.FileOutputStream

class BluetoothServer(
    private val adapter: BluetoothAdapter,
    private val uuid: UUID,
    private val context: Context,
    private val onConnectionAccepted: (BluetoothSocket) -> Unit,
    private val onConnectionClosed: () -> Unit,
    private val onError: (Exception) -> Unit
) : Thread() {

    private var serverSocket: BluetoothServerSocket? = null
    private var isRunning = true
    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    fun startServer() {
        try {
            serverSocket = adapter.listenUsingRfcommWithServiceRecord("MyApp", uuid)
        } catch (e: IOException) {
            Log.e("BluetoothServer", "Error creating server socket", e)
            onError(e)
            return
        }
        start()  // Inicia el hilo
    }

    override fun run() {
        var socket: BluetoothSocket?
        while (isRunning) {
            try {
                socket = serverSocket?.accept()
                if (socket != null) {
                    onConnectionAccepted(socket)
                    manageConnection(socket)
                    // NO cerrar serverSocket ni romper el loop
                    // Así el servidor queda escuchando nuevas conexiones
                }
            } catch (e: IOException) {
                if (isRunning) {
                    Log.e("BluetoothServer", "Error accepting connection", e)
                    onError(e)
                }
                break
            }
        }
    }

    fun cancel() {
        isRunning = false
        try {
            serverSocket?.close()
            onConnectionClosed()
        } catch (e: IOException) {
            Log.e("BluetoothServer", "Error closing server socket", e)
        }
    }

    private fun manageConnection(socket: BluetoothSocket) {
        val inputStream = socket.inputStream
        val outputStream = socket.outputStream
        Thread {
            try {
                val buffer = ByteArray(1024)
                var bytesRead: Int

                val file = File(context.filesDir, "received_file")
                Log.d("BluetoothServer", "Guardando archivo en: ${file.absolutePath}")
                val fos = FileOutputStream(file)

                while (true) {
                    Log.d("BluetoothServer", "Esperando datos...")
                    bytesRead = inputStream.read(buffer)
                    Log.d("BluetoothServer", "Bytes leidos: $bytesRead")
                    if (bytesRead == -1) {
                        onConnectionClosed()
                        break
                    }
                    fos.write(buffer, 0, bytesRead)
                }
                fos.close()
                Log.d("BluetoothServer", "Archivo recibido: ${file.absolutePath}")

            } catch (e: IOException) {
                Log.e("BluetoothServer", "Error en conexión", e)
                onConnectionClosed()
            } finally {
                try {
                    socket.close()
                } catch (e: IOException) {
                    Log.e("BluetoothServer", "Error cerrando socket", e)
                }
            }
        }.start()
    }
}
