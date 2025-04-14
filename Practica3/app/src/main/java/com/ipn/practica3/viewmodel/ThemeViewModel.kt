package com.ipn.practica3.viewmodel

import android.app.Application
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.*
import com.ipn.practica3.data.ThemePreferences
import com.ipn.practica3.ui.theme.AppTheme
import kotlinx.coroutines.launch

class ThemeViewModel(application: Application) : AndroidViewModel(application) {
    private val context = application.applicationContext

    var currentTheme = mutableStateOf(AppTheme.GUINDA)
        private set

    init {
        viewModelScope.launch {
            ThemePreferences.getTheme(context).collect { theme ->
                currentTheme.value = theme
            }
        }
    }

    fun setTheme(theme: AppTheme) {
        currentTheme.value = theme
        viewModelScope.launch {
            ThemePreferences.saveTheme(context, theme)
        }
    }
}