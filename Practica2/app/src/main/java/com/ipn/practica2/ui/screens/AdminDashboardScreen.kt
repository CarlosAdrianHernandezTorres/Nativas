package com.ipn.practica2.ui.screens

import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.ipn.practica2.data.model.RegisterRequest
import com.ipn.practica2.data.model.User
import com.ipn.practica2.data.network.RetrofitInstance
import com.ipn.practica2.datastore.UserPreferences
import com.ipn.practica2.ui.components.CreateUserDialog
import com.ipn.practica2.ui.components.EditUserDialog
import kotlinx.coroutines.launch

@Composable
fun AdminDashboardScreen(navController: NavHostController) {
    val context = LocalContext.current
    val userPrefs = remember { UserPreferences(context) }
    val coroutineScope = rememberCoroutineScope()

    val adminData by userPrefs.getUserData().collectAsState(
        initial = UserPreferences.UserData("", "", "", "", "", "")
    )
    LaunchedEffect(adminData) {
        Log.d("LOAD", "Imagen cargada desde DataStore: ${adminData.imageUri}")
    }

    var imageUri by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(adminData.imageUri) {
        imageUri = adminData.imageUri.takeIf { it.isNotBlank() }
    }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        if (uri != null) {
            val selectedUri = uri.toString()
            imageUri = selectedUri
            coroutineScope.launch {
                userPrefs.saveUser(
                    id = adminData.id,
                    name = adminData.name,
                    email = adminData.email,
                    role = adminData.role,
                    token = adminData.token,
                    imageUri = selectedUri
                )

                try {
                    RetrofitInstance.api.updateUser(
                        id = adminData.id,
                        user = User(
                            id = adminData.id,
                            name = adminData.name,
                            email = adminData.email,
                            password = "",
                            role = adminData.role,
                            profileImageUrl = selectedUri
                        )
                    )
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    var userList by remember { mutableStateOf<List<User>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        try {
            val response = RetrofitInstance.api.getAllUsers()
            if (response.isSuccessful) {
                userList = response.body() ?: emptyList()
            }
        } catch (e: Exception) {
        } finally {
            isLoading = false
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text("👨‍💼 Panel del Administrador", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(16.dp))

        imageUri?.let {
            AsyncImage(
                model = it,
                contentDescription = "Foto del administrador",
                modifier = Modifier
                    .size(128.dp)
                    .clip(CircleShape)
            )
            Spacer(modifier = Modifier.height(12.dp))
        }

        Button(onClick = { launcher.launch("image/*") }) {
            Text("Seleccionar Imagen")
        }

        Spacer(modifier = Modifier.height(16.dp))
        Text("Nombre: ${adminData.name}")
        Text("Correo: ${adminData.email}")
        Text("Rol: ${adminData.role}")
        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                coroutineScope.launch {
                    userPrefs.clearData()
                    navController.navigate("login") {
                        popUpTo("admin_dashboard") { inclusive = true }
                    }
                }
            }
        ) {
            Text("Cerrar sesión")
        }

        Spacer(modifier = Modifier.height(32.dp))
        var showCreateDialog by remember { mutableStateOf(false) }

        Button(
            onClick = { showCreateDialog = true },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("➕ Crear Usuario")
        }

        if (showCreateDialog) {
            CreateUserDialog(
                onDismiss = { showCreateDialog = false },
                onCreate = { newUser ->
                    coroutineScope.launch {
                        try {
                            val request = RegisterRequest(
                                name = newUser.name,
                                email = newUser.email,
                                password = newUser.password,
                                role = newUser.role
                            )
                            val response = RetrofitInstance.api.registerUser(request)
                            if (response.isSuccessful) {
                                userList = userList + response.body()!!
                                showCreateDialog = false
                            }
                        } catch (e: Exception) {
                        }
                    }
                }
            )
        }

        Spacer(modifier = Modifier.height(32.dp))
        Text("📋 Lista de Usuarios:", style = MaterialTheme.typography.titleLarge)

        if (isLoading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
        } else {
            LazyColumn {
                items(userList) { user ->
                    var showEditDialog by remember { mutableStateOf(false) }

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        elevation = CardDefaults.cardElevation(4.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            user.profileImageUrl?.takeIf { it.isNotBlank() }?.let { imageUrl ->
                                AsyncImage(
                                    model = imageUrl,
                                    contentDescription = "Foto de ${user.name}",
                                    modifier = Modifier
                                        .size(64.dp)
                                        .clip(CircleShape)
                                        .align(Alignment.CenterHorizontally)
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                            }
                            Text("👤 ${user.name}", style = MaterialTheme.typography.titleMedium)
                            Text("✉️ ${user.email}")
                            Text("🔐 Rol: ${user.role}")

                            Spacer(modifier = Modifier.height(8.dp))

                            Row {
                                Button(
                                    onClick = { showEditDialog = true },
                                    modifier = Modifier.padding(end = 8.dp)
                                ) {
                                    Text("📝 Editar")
                                }

                                Button(
                                    onClick = {
                                        coroutineScope.launch {
                                            user.id?.let { id ->
                                                try {
                                                    val response = RetrofitInstance.api.deleteUser(id)
                                                    if (response.isSuccessful) {
                                                        userList = userList.filterNot { it.id == id }
                                                    }
                                                } catch (e: Exception) {
                                                    // Manejo de error
                                                }
                                            }
                                        }
                                    },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = MaterialTheme.colorScheme.error
                                    )
                                ) {
                                    Text("🗑️ Eliminar")
                                }
                            }

                            if (showEditDialog) {
                                EditUserDialog(
                                    user = user,
                                    onDismiss = { showEditDialog = false },
                                    onUpdate = { updatedUser ->
                                        coroutineScope.launch {
                                            user.id?.let { id ->
                                                try {
                                                    val response = RetrofitInstance.api.updateUser(id, updatedUser)
                                                    if (response.isSuccessful) {
                                                        userList = userList.map {
                                                            if (it.id == id) updatedUser.copy(id = id) else it
                                                        }
                                                    }
                                                } catch (e: Exception) {
                                                } finally {
                                                    showEditDialog = false
                                                }
                                            }
                                        }
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}