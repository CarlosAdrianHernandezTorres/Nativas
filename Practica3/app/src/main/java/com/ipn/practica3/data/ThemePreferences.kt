package com.ipn.practica3.data

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.ipn.practica3.ui.theme.AppTheme
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore by preferencesDataStore(name = "settings")

object ThemePreferences {
    private val THEME_KEY = stringPreferencesKey("app_theme")

    fun getTheme(context: Context): Flow<AppTheme> {
        return context.dataStore.data.map { preferences ->
            when (preferences[THEME_KEY]) {
                "AZUL" -> AppTheme.AZUL
                else -> AppTheme.GUINDA
            }
        }
    }

    suspend fun saveTheme(context: Context, theme: AppTheme) {
        context.dataStore.edit { preferences ->
            preferences[THEME_KEY] = theme.name
        }
    }
}