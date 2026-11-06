package com.project.knotbook.utils

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Why DataStore?
 * - It persists tokens across app restarts.
 * - It exposes values as Flow.
 * - It works naturally with coroutines.
 * - It avoids blocking the main thread.
 */

private val Context.dataStore by preferencesDataStore(
    name = "knotbook_preferences"
)

@Singleton
class TokenDataStore @Inject constructor(
    @ApplicationContext private val context:Context
){
    private object Keys {
        val ACCESS_TOKEN = stringPreferencesKey("access_token")
        val REFRESH_TOKEN = stringPreferencesKey("refresh_token")
    }

    // observer receive automatic updates when saved or cleared
    val accessToken: Flow<String?> =
        context.dataStore.data.map { preferences ->
            preferences[Keys.ACCESS_TOKEN]
        }

    val refreshToken: Flow<String?> =
        context.dataStore.data.map { preferences ->
            preferences[Keys.REFRESH_TOKEN]
        }

    suspend fun saveTokens(
        accessToken: String,
        refreshToken: String
    ) {
        context.dataStore.edit { preferences ->
            preferences[Keys.ACCESS_TOKEN] = accessToken
            preferences[Keys.REFRESH_TOKEN] = refreshToken
        }
    }

    suspend fun clearTokens() {
        context.dataStore.edit { preferences ->
            preferences.remove(Keys.ACCESS_TOKEN)
            preferences.remove(Keys.REFRESH_TOKEN)
        }
    }
}