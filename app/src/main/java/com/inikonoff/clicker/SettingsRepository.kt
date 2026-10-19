package com.inikonoff.clicker

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

object SettingsRepository {
    private val KEY_DIGIT_STYLE = stringPreferencesKey("digit_style") // "mechanical" or "electronic"
    private val KEY_THEME = stringPreferencesKey("theme") // "light", "dark", "system"
    private val KEY_VIBRATION = booleanPreferencesKey("vibration")
    private val KEY_SOUND = booleanPreferencesKey("sound")

    fun digitStyle(context: Context): Flow<String> =
        context.dataStore.data.map { it[KEY_DIGIT_STYLE] ?: "mechanical" }

    fun theme(context: Context): Flow<String> =
        context.dataStore.data.map { it[KEY_THEME] ?: "system" }

    fun vibration(context: Context): Flow<Boolean> =
        context.dataStore.data.map { it[KEY_VIBRATION] ?: true }

    fun sound(context: Context): Flow<Boolean> =
        context.dataStore.data.map { it[KEY_SOUND] ?: false }

    suspend fun setDigitStyle(context: Context, style: String) {
        context.dataStore.edit { it[KEY_DIGIT_STYLE] = style }
    }

    suspend fun setTheme(context: Context, theme: String) {
        context.dataStore.edit { it[KEY_THEME] = theme }
    }

    suspend fun setVibration(context: Context, enabled: Boolean) {
        context.dataStore.edit { it[KEY_VIBRATION] = enabled }
    }

    suspend fun setSound(context: Context, enabled: Boolean) {
        context.dataStore.edit { it[KEY_SOUND] = enabled }
    }
}
