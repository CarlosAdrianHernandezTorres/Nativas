package com.ipn.practica3.data

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.recentFilesDataStore by preferencesDataStore("recent_files")

object RecentFilesStore {
    private val RECENT_FILES_KEY = stringSetPreferencesKey("recent_files")

    fun getRecentFiles(context: Context): Flow<Set<String>> {
        return context.dataStore.data.map { prefs ->
            prefs[RECENT_FILES_KEY] ?: emptySet()
        }
    }

    suspend fun addFileToHistory(context: Context, path: String) {
        context.dataStore.edit { prefs ->
            val current = prefs[RECENT_FILES_KEY]?.toMutableSet() ?: mutableSetOf()
            current.add(path)
            prefs[RECENT_FILES_KEY] = current
        }
    }

    suspend fun clearHistory(context: Context) {
        context.dataStore.edit { prefs ->
            prefs[RECENT_FILES_KEY] = emptySet()
        }
    }
}