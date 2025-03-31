package com.ipn.practica2.datastore

import android.content.Context
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore by preferencesDataStore(name = "user_prefs")

class UserPreferences(private val context: Context) {

    companion object {
        val USER_ID = stringPreferencesKey("user_id")
        val USER_NAME = stringPreferencesKey("user_name")
        val USER_EMAIL = stringPreferencesKey("user_email")
        val USER_ROLE = stringPreferencesKey("user_role")
        val USER_TOKEN = stringPreferencesKey("user_token")
        val USER_IMAGE_URI = stringPreferencesKey("user_image_uri")
    }

    suspend fun saveUser(
        id: String,
        name: String,
        email: String,
        role: String,
        token: String,
        imageUri: String = ""
    ) {
        context.dataStore.edit { prefs ->
            prefs[USER_ID] = id
            prefs[USER_NAME] = name
            prefs[USER_EMAIL] = email
            prefs[USER_ROLE] = role
            prefs[USER_TOKEN] = token
            prefs[USER_IMAGE_URI] = imageUri
        }
    }

    fun getUserData(): Flow<UserData> {
        return context.dataStore.data.map { prefs ->
            UserData(
                id = prefs[USER_ID] ?: "",
                name = prefs[USER_NAME] ?: "",
                email = prefs[USER_EMAIL] ?: "",
                role = prefs[USER_ROLE] ?: "",
                token = prefs[USER_TOKEN] ?: "",
                imageUri = prefs[USER_IMAGE_URI] ?: ""
            )
        }
    }

    suspend fun clearData() {
        context.dataStore.edit { it.clear() }
    }

    data class UserData(
        val id: String,
        val name: String,
        val email: String,
        val role: String,
        val token: String,
        val imageUri: String = ""
    )
}