package com.ipn.caht.sistemasolar

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavHostController
import androidx.navigation.compose.*
import com.ipn.caht.sistemasolar.ui.theme.SistemaSolarTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SistemaSolarTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    NavigationGraph(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}

// Definimos las rutas para la navegación
sealed class Screen(val route: String) {
    object Galaxia : Screen("galaxia")
    object SistemaSolar : Screen("sistema_solar")
    object Planeta : Screen("planeta/{nombre}") {
        fun createRoute(nombre: String) = "planeta/$nombre"
    }
}

// Componente principal de navegación
@Composable
fun NavigationGraph(modifier: Modifier = Modifier) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Screen.Galaxia.route,
        modifier = modifier
    ) {
        composable(Screen.Galaxia.route) { GalaxiaScreen(navController) }
        composable(Screen.SistemaSolar.route) { SistemaSolarScreen(navController) }
        composable(Screen.Planeta.route) { backStackEntry ->
            val nombre = backStackEntry.arguments?.getString("nombre") ?: "Desconocido"
            PlanetaScreen(navController, nombre)
        }
    }
}

// Pantallas de la aplicación
@Composable
fun GalaxiaScreen(navController: NavHostController) {
    Scaffold {
        Button(onClick = { navController.navigate(Screen.SistemaSolar.route) }) {
            Text("Ir al Sistema Solar")
        }
    }
}

@Composable
fun SistemaSolarScreen(navController: NavHostController) {
    Scaffold {
        Button(onClick = { navController.navigate(Screen.Planeta.createRoute("Tierra")) }) {
            Text("Explorar el planeta Tierra")
        }
    }
}

@Composable
fun PlanetaScreen(navController: NavHostController, nombre: String) {
    Scaffold {
        Text("Explorando el planeta: $nombre")
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewNavigation() {
    SistemaSolarTheme {
        NavigationGraph()
    }
}
