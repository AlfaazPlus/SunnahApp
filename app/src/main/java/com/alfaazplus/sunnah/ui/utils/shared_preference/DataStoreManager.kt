package com.alfaazplus.sunnah.ui.utils.shared_preference

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map


private const val DATASTORE_NAME = "app_preferences"

private val Context.dataStore by preferencesDataStore(name = DATASTORE_NAME)

object DataStoreManager {
    private lateinit var appContext: Context

    fun init(context: Context) {
        appContext = context.applicationContext
    }

    suspend fun <T> write(key: Preferences.Key<T>, value: T) {
        appContext.dataStore.edit { preferences ->
            preferences[key] = value
        }
    }

    suspend fun <T> read(key: Preferences.Key<T>, defaultValue: T): T {
        val preferences = appContext.dataStore.data.first()
        return preferences[key] ?: defaultValue
    }

    @Composable
    fun <T> observe(key: Preferences.Key<T>, defaultValue: T) =
        appContext.dataStore.data.map { preferences ->
            preferences[key] ?: defaultValue
        }.collectAsState(defaultValue).value
}
