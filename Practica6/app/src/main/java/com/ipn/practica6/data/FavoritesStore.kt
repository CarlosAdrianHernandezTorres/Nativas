package com.ipn.practica6.data

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.favoritesDataStore by preferencesDataStore("favorite_files")

object FavoritesStore {
    private val FAVORITES_KEY = stringSetPreferencesKey("favorite_files")

    fun getFavorites(context: Context): Flow<Set<String>> {
        return context.favoritesDataStore.data.map { prefs ->
            prefs[FAVORITES_KEY] ?: emptySet()
        }
    }

    suspend fun toggleFavorite(context: Context, path: String) {
        context.favoritesDataStore.edit { prefs ->
            val current = prefs[FAVORITES_KEY]?.toMutableSet() ?: mutableSetOf()
            if (path in current) current.remove(path) else current.add(path)
            prefs[FAVORITES_KEY] = current
        }
    }

    suspend fun clearFavorites(context: Context) {
        context.favoritesDataStore.edit { prefs ->
            prefs[FAVORITES_KEY] = emptySet()
        }
    }
}