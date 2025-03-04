package com.ipn.caht.cronometro

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import com.ipn.caht.cronometro.ui.theme.CronometroTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CronometroTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Box(modifier = Modifier.padding(innerPadding)) {
                        Cronometro()
                    }
                }
            }
        }
    }
}

@Composable
fun Cronometro() {
    var tiempo by remember { mutableStateOf(0L) }
    var isRunning by remember { mutableStateOf(false) }

    LaunchedEffect(isRunning) {
        while (isRunning) {
            delay(1000L)
            tiempo++
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = formatTiempo(tiempo), style = MaterialTheme.typography.displayLarge)
        Spacer(modifier = Modifier.height(16.dp))
        Row {
            Button(onClick = { isRunning = !isRunning }) {
                Text(if (isRunning) "Pausar" else "Iniciar")
            }
            Spacer(modifier = Modifier.width(8.dp))
            Button(onClick = {
                tiempo = 0L
                isRunning = false
            }) {
                Text("Reiniciar")
            }
        }
    }
}

fun formatTiempo(segundos: Long): String {
    val minutos = segundos / 60
    val segs = segundos % 60
    return "%02d:%02d".format(minutos, segs)
}

@Preview(showBackground = true)
@Composable
fun CronometroPreview() {
    CronometroTheme {
        Cronometro()
    }
}