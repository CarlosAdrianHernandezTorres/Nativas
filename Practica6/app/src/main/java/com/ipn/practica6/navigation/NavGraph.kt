package com.ipn.practica6.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.ipn.practica6.screens.CameraMicScreen
import com.ipn.practica6.screens.FileManagerScreen
import com.ipn.practica6.screens.HomeScreen
import com.ipn.practica6.screens.ImageViewerScreen
import com.ipn.practica6.screens.TextViewerScreen
import com.ipn.practica6.screens.AudioGalleryScreen
import com.ipn.practica6.screens.BluetoothScreen
import com.ipn.practica6.screens.FavoriteFilesScreen
import com.ipn.practica6.screens.RecentFilesScreen
import com.ipn.practica6.viewmodel.BluetoothViewModel
import com.ipn.practica6.viewmodel.ThemeViewModel
import java.net.URLDecoder
import com.ipn.practica6.screens.BluetoothDiscoveryScreen
import com.ipn.practica6.screens.BluetoothServerScreen

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object FileManager : Screen("file_manager")
    object CameraMic : Screen("camera_mic")
    object TextViewer : Screen("text_viewer")
    object ImageViewer : Screen("image_viewer")
    object AudioGallery : Screen("audio_gallery")
    object RecentFiles : Screen("recent_files")
    object FavoriteFiles : Screen("favorite_files")
}

@Composable
fun AppNavGraph(navController: NavHostController, themeViewModel: ThemeViewModel){
    NavHost(navController, startDestination = Screen.Home.route) {
        composable(Screen.Home.route) {
            HomeScreen(navController, themeViewModel)
        }
        composable(Screen.FileManager.route) { FileManagerScreen(navController) }
        composable(Screen.CameraMic.route) { CameraMicScreen() }
        composable("${Screen.TextViewer.route}/{filePath}") { backStackEntry ->
            val encoded = backStackEntry.arguments?.getString("filePath") ?: ""
            val decodedPath = URLDecoder.decode(encoded, "utf-8")
            TextViewerScreen(navController, filePath = decodedPath)
        }
        composable("${Screen.ImageViewer.route}/{filePath}") { backStackEntry ->
            val encoded = backStackEntry.arguments?.getString("filePath") ?: ""
            val decodedPath = URLDecoder.decode(encoded, "utf-8")
            ImageViewerScreen(navController, decodedPath)
        }
        composable(Screen.AudioGallery.route) {
            AudioGalleryScreen()
        }
        composable(Screen.RecentFiles.route) {
            RecentFilesScreen(navController)
        }
        composable(Screen.FavoriteFiles.route) {
            FavoriteFilesScreen(navController)
        }
        composable(route = "bluetooth") {
            val bluetoothViewModel: BluetoothViewModel = viewModel()
            BluetoothScreen(bluetoothViewModel, navController = navController)
        }
        composable("bluetooth_discovery") {
            val bluetoothViewModel: BluetoothViewModel = viewModel()
            BluetoothDiscoveryScreen(bluetoothViewModel)
        }
        composable("bluetooth_server") {
            val bluetoothViewModel: BluetoothViewModel = viewModel()
            BluetoothServerScreen(bluetoothViewModel)
        }
    }
}