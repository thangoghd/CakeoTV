package com.thangoghd.cakeotv.data.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.thangoghd.cakeotv.ui.model.UIMode
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

@Singleton
class PreferencesRepository @Inject constructor(
    @ApplicationContext private val context: Context
) {
    companion object {
        private const val KEY_UI_MODE = "ui_mode"
        private const val KEY_FIRST_LAUNCH = "first_launch"
        private const val KEY_DARK_MODE = "dark_mode"
        private const val KEY_SYSTEM_THEME = "system_theme"
        private const val PREF_BACKGROUND_PLAYBACK = "pref_background_playback"
        private const val PREF_PICTURE_IN_PICTURE = "pref_picture_in_picture"
    }

    private val UI_MODE_KEY = stringPreferencesKey(KEY_UI_MODE)
    private val FIRST_LAUNCH_KEY = booleanPreferencesKey(KEY_FIRST_LAUNCH)

    fun getUIMode(): Flow<UIMode> = context.dataStore.data.map { preferences ->
        UIMode.valueOf(preferences[UI_MODE_KEY] ?: UIMode.TV.name)
    }

    suspend fun setUIMode(mode: UIMode) {
        context.dataStore.edit { preferences ->
            preferences[UI_MODE_KEY] = mode.name
        }
    }

    suspend fun isFirstLaunch(): Boolean = context.dataStore.data.map { preferences ->
        preferences[FIRST_LAUNCH_KEY] ?: true
    }.first()

    suspend fun setFirstLaunch(isFirst: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[FIRST_LAUNCH_KEY] = isFirst
        }
    }

    suspend fun setDarkMode(isDarkMode: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[booleanPreferencesKey(KEY_DARK_MODE)] = isDarkMode
        }
    }

    suspend fun setSystemTheme(useSystemTheme: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[booleanPreferencesKey(KEY_SYSTEM_THEME)] = useSystemTheme
        }
    }

    suspend fun setBackgroundPlayback(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[booleanPreferencesKey(PREF_BACKGROUND_PLAYBACK)] = enabled
        }
    }

    suspend fun setPictureInPicture(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[booleanPreferencesKey(PREF_PICTURE_IN_PICTURE)] = enabled
        }
    }

    fun getDarkMode(): Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[booleanPreferencesKey(KEY_DARK_MODE)] ?: false
    }

    fun getSystemTheme(): Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[booleanPreferencesKey(KEY_SYSTEM_THEME)] ?: true
    }

    fun getBackgroundPlayback(): Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[booleanPreferencesKey(PREF_BACKGROUND_PLAYBACK)] ?: false
    }

    fun getPictureInPicture(): Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[booleanPreferencesKey(PREF_PICTURE_IN_PICTURE)] ?: false
    }
}
