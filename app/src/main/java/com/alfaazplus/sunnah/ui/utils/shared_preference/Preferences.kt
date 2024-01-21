package com.alfaazplus.sunnah.ui.utils.shared_preference

import android.content.Context
import android.content.SharedPreferences
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.SnapshotMutationPolicy
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember

object Preferences {
    private const val prefFile = "preferences"
    private lateinit var preferences: SharedPreferences

    fun init(context: Context) {
        preferences = context.getSharedPreferences(prefFile, Context.MODE_PRIVATE)
    }

    fun getBoolean(key: String, defValue: Boolean) = preferences.getBoolean(key, defValue)
    fun getString(key: String, defValue: String) = preferences.getString(key, defValue)
    fun getInt(key: String, defValue: Int) = preferences.getInt(key, defValue)
    fun getFloat(key: String, defValue: Float) = preferences.getFloat(key, defValue)
    fun getStringSet(key: String, defValue: Set<String>) = preferences.getStringSet(key, defValue)

    fun edit(action: SharedPreferences.Editor.() -> Unit) {
        preferences.edit().apply(action).apply()
    }
}

@Composable
fun rememberPreference(key: String, defaultValue: Boolean): MutableState<Boolean> {
    return remember {
        mutableStatePreferenceOf(Preferences.getBoolean(key, defaultValue)) {
            Preferences.edit { putBoolean(key, it) }
        }
    }
}

@Composable
fun rememberPreference(key: String, defaultValue: Int): MutableState<Int> {
    return remember {
        mutableStatePreferenceOf(Preferences.getInt(key, defaultValue)) {
            Preferences.edit { putInt(key, it) }
        }
    }
}

@Composable
fun rememberPreference(key: String, defaultValue: Float): MutableState<Float> {
    return remember {
        mutableStatePreferenceOf(Preferences.getFloat(key, defaultValue)) {
            Preferences.edit { putFloat(key, it) }
        }
    }
}

@Composable
fun rememberPreference(key: String, defaultValue: String): MutableState<String> {
    return remember {
        mutableStatePreferenceOf(Preferences.getString(key, defaultValue) ?: defaultValue) {
            Preferences.edit { putString(key, it) }
        }
    }
}

inline fun <T> mutableStatePreferenceOf(
    value: T,
    crossinline onStructuralInequality: (newValue: T) -> Unit
) =
    mutableStateOf(
        value = value,
        policy = object : SnapshotMutationPolicy<T> {
            override fun equivalent(a: T, b: T): Boolean {
                if (a == b) return true
                onStructuralInequality(b)
                return false
            }
        }
    )