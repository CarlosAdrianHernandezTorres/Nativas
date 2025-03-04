package com.ipn.caht.calcucron

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.delay
import net.objecthunter.exp4j.ExpressionBuilder
import com.ipn.caht.calcucron.ui.theme.CalcuCronTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        Log.d("Lifecycle", "onCreate called")
        setContent {
            CalcuCronTheme {
                val navController = rememberNavController()
                NavHost(navController, startDestination = "main_menu") {
                    composable("main_menu") { MainMenu(navController) }
                    composable("calculator") { CalculatorScreen(navController) }
                    composable("cronometro") { CronometroScreen(navController) }
                }
            }
        }
    }

    override fun onPause() {
        super.onPause()
        Log.d("Lifecycle", "onPause called")
    }

    override fun onResume() {
        super.onResume()
        Log.d("Lifecycle", "onResume called")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("Lifecycle", "onDestroy called")
    }
}

@Composable
fun MainMenu(navController: NavHostController) {
    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Button(onClick = { navController.navigate("calculator") }) {
            Text("Abrir Calculadora")
        }
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = { navController.navigate("cronometro") }) {
            Text("Abrir Cronómetro")
        }
    }
}

@Composable
fun CalculatorScreen(navController: NavHostController) {
    var inputText by remember { mutableStateOf("") }
    var resultText by remember { mutableStateOf("") }

    val buttons = listOf(
        listOf("7", "8", "9", "/"),
        listOf("4", "5", "6", "*"),
        listOf("1", "2", "3", "-"),
        listOf("C", "0", "=", "+")
    )

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Button(onClick = { navController.navigate("main_menu") }) {
            Text("Regresar al Menú")
        }
        Spacer(modifier = Modifier.height(16.dp))
        Text(text = resultText.ifEmpty { inputText }, fontSize = 32.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.End, modifier = Modifier.fillMaxWidth().padding(16.dp))
        Spacer(modifier = Modifier.height(8.dp))
        buttons.forEach { row ->
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                row.forEach { text ->
                    Button(
                        onClick = { handleButtonClick(text, inputText, resultText, { inputText = it }, { resultText = it }) },
                        modifier = Modifier.weight(1f).padding(4.dp),
                        shape = ButtonDefaults.shape,
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Gray)
                    ) {
                        Text(text, fontSize = 24.sp, color = Color.White)
                    }
                }
            }
        }
    }
}

fun handleButtonClick(buttonText: String, inputText: String, resultText: String, onUpdateInput: (String) -> Unit, onUpdateResult: (String) -> Unit) {
    when (buttonText) {
        "C" -> {
            onUpdateInput("")
            onUpdateResult("")
        }
        "=" -> {
            try {
                val result = eval(inputText)
                onUpdateResult(result.toString())
            } catch (e: Exception) {
                onUpdateResult("Error")
            }
        }
        else -> {
            onUpdateInput(inputText + buttonText)
            onUpdateResult("")
        }
    }
}

fun eval(expression: String): Double {
    return try {
        ExpressionBuilder(expression).build().evaluate()
    } catch (e: Exception) {
        Double.NaN
    }
}

@Composable
fun CronometroScreen(navController: NavHostController) {
    var tiempo by remember { mutableStateOf(0L) }
    var isRunning by remember { mutableStateOf(false) }

    LaunchedEffect(isRunning) {
        while (isRunning) {
            delay(1000L)
            tiempo++
        }
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
        Button(onClick = { navController.navigate("main_menu") }) {
            Text("Regresar al Menú")
        }
        Spacer(modifier = Modifier.height(16.dp))
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
fun MainMenuPreview() {
    CalcuCronTheme {
        MainMenu(rememberNavController())
    }
}