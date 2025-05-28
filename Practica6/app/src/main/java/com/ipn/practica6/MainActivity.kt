package com.ipn.practica6

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.navigation.compose.rememberNavController
import com.ipn.practica6.navigation.AppNavGraph
import com.ipn.practica6.ui.theme.Practica3Theme
import com.ipn.practica6.viewmodel.ThemeViewModel


class MainActivity : ComponentActivity() {
    private val themeViewModel: ThemeViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val currentTheme = themeViewModel.currentTheme.value
            Practica3Theme(theme = currentTheme) {
                val navController = rememberNavController()
                AppNavGraph(navController, themeViewModel)
            }
        }
    }
}

