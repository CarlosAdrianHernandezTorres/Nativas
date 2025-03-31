package com.ipn.practica2.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ipn.practica2.data.model.RegisterRequest
import com.ipn.practica2.viewmodel.AuthViewModel
import com.ipn.practica2.data.model.User

@Composable
fun RegisterScreen(navController: NavHostController) {
    val authViewModel: AuthViewModel = viewModel()
    val registerState by authViewModel.registerState.collectAsState()

    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var localError by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Text("Registro de Usuario", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(20.dp))

        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Nombre") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Correo Electrónico") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Contraseña") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(20.dp))

        Button(
            onClick = {
                if (name.isBlank() || email.isBlank() || password.isBlank()) {
                    localError = "Todos los campos son obligatorios"
                    return@Button
                }

                localError = null
                val user = RegisterRequest(name = name, email = email, password = password)
                authViewModel.registerUser(user)
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Registrarse")
        }

        TextButton(onClick = { navController.popBackStack() }) {
            Text("¿Ya tienes cuenta? Inicia sesión")
        }

        Spacer(modifier = Modifier.height(16.dp))

        localError?.let {
            Text("❌ $it", color = MaterialTheme.colorScheme.error)
        }

        when (registerState) {
            is AuthViewModel.AuthState.Loading -> {
                CircularProgressIndicator()
            }
            is AuthViewModel.AuthState.Success -> {
                Text("✅ ¡Registro exitoso!", color = MaterialTheme.colorScheme.primary)
            }
            is AuthViewModel.AuthState.Error -> {
                val errorMsg = (registerState as AuthViewModel.AuthState.Error).message
                Text("❌ Error: $errorMsg", color = MaterialTheme.colorScheme.error)
            }
            else -> {}
        }
    }
}