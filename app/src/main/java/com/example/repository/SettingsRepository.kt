package com.example.repository

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore by preferencesDataStore(name = "settings")

class SettingsRepository(private val context: Context) {
    private val USERNAME_KEY = stringPreferencesKey("username")

    val userNameFlow: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[USERNAME_KEY] ?: "Pengguna"
    }

    suspend fun setUserName(name: String) {
        context.dataStore.edit { preferences ->
            preferences[USERNAME_KEY] = name
        }
    }
}
