package com.ipn.calculadora

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.ipn.calculadora.ui.theme.CalculadoraTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import net.objecthunter.exp4j.ExpressionBuilder

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CalculadoraTheme {
                CalculatorScreen()
            }
        }
    }
}

@Composable
fun CalculatorScreen() {
    var inputText by remember { mutableStateOf("") }
    var resultText by remember { mutableStateOf("") }

    val buttons = listOf(
        listOf("7", "8", "9", "/"),
        listOf("4", "5", "6", "*"),
        listOf("1", "2", "3", "-"),
        listOf("C", "0", "=", "+")
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = resultText.ifEmpty { inputText },
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.End,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        )

        Spacer(modifier = Modifier.height(8.dp))

        buttons.forEach { row ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                row.forEach { text ->
                    Button(
                        onClick = { handleButtonClick(text, inputText, resultText, onUpdateInput = { inputText = it }, onUpdateResult = { resultText = it }) },
                        modifier = Modifier
                            .weight(1f)
                            .padding(4.dp),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Gray)
                    ) {
                        Text(text = text, fontSize = 24.sp, color = Color.White)
                    }
                }
            }
        }
    }
}

fun handleButtonClick(
    buttonText: String,
    inputText: String,
    resultText: String,
    onUpdateInput: (String) -> Unit,
    onUpdateResult: (String) -> Unit
) {
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

@Preview(showBackground = true)
@Composable
fun CalculatorPreview() {
    CalculadoraTheme {
        CalculatorScreen()
    }
}