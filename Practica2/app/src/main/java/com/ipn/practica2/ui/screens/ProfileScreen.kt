package com.ipn.practica2.ui.screens

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.ipn.practica2.datastore.UserPreferences
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import coil.compose.AsyncImage
import com.ipn.practica2.data.model.User
import com.ipn.practica2.data.network.RetrofitInstance

@Composable
fun ProfileScreen(navController: NavHostController) {
    val context = LocalContext.current
    val userPrefs = remember { UserPreferences(context) }
    val coroutineScope = rememberCoroutineScope()

    val userData by userPrefs.getUserData().collectAsState(
        initial = UserPreferences.UserData("", "", "", "", "", "")
    )

    var imageUri by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(userData.imageUri) {
        imageUri = userData.imageUri.takeIf { it.isNotBlank() }
    }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        if (uri != null) {
            val selectedUri = uri.toString()
            imageUri = selectedUri

            coroutineScope.launch {
                userPrefs.saveUser(
                    id = userData.id,
                    name = userData.name,
                    email = userData.email,
                    role = userData.role,
                    token = userData.token,
                    imageUri = selectedUri
                )
                try {
                    val updatedUser = User(
                        id = userData.id,
                        name = userData.name,
                        email = userData.email,
                        password = "",
                        role = userData.role,
                        profileImageUrl = selectedUri
                    )
                    RetrofitInstance.api.updateUser(userData.id, updatedUser)
                } catch (e: Exception) {
                }
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("👤 Perfil del Usuario", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(16.dp))

        imageUri?.let {
            AsyncImage(
                model = it,
                contentDescription = "Foto de perfil",
                modifier = Modifier
                    .size(128.dp)
                    .clip(CircleShape)
            )
        }

        Button(onClick = { launcher.launch("image/*") }) {
            Text("Seleccionar Imagen")
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text("Nombre: ${userData.name}", style = MaterialTheme.typography.bodyLarge)
        Text("Correo: ${userData.email}", style = MaterialTheme.typography.bodyLarge)
        Text("Rol: ${userData.role}", style = MaterialTheme.typography.bodyLarge)

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = {
                coroutineScope.launch {
                    userPrefs.clearData()
                    navController.navigate("login") {
                        popUpTo("profile") { inclusive = true }
                    }
                }
            }
        ) {
            Text("Cerrar sesión")
        }
    }
}