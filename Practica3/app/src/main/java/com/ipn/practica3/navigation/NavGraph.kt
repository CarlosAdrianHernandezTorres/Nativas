package com.ipn.practica3.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.ipn.practica3.screens.CameraMicScreen
import com.ipn.practica3.screens.FileManagerScreen
import com.ipn.practica3.screens.HomeScreen
import com.ipn.practica3.screens.ImageViewerScreen
import com.ipn.practica3.screens.TextViewerScreen
import com.ipn.practica3.screens.AudioGalleryScreen
import com.ipn.practica3.screens.FavoriteFilesScreen
import com.ipn.practica3.screens.RecentFilesScreen
import com.ipn.practica3.viewmodel.ThemeViewModel
import java.net.URLDecoder

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
    }
}