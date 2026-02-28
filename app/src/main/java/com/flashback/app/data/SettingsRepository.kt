package com.flashback.app.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.doublePreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

/** DataStore 設定存取 */
class SettingsRepository(private val context: Context) {

    private object Keys {
        val VOLUME_THRESHOLD = doublePreferencesKey("volume_threshold_db")
        val CLASSIFICATION_CONFIDENCE = floatPreferencesKey("classification_confidence")
        val MIN_DURATION = longPreferencesKey("min_duration_ms")
        val START_HOUR = intPreferencesKey("start_hour")
        val END_HOUR = intPreferencesKey("end_hour")
        val CLASSIFICATION_ENABLED = booleanPreferencesKey("classification_enabled")
        val TIME_WINDOW_ENABLED = booleanPreferencesKey("time_window_enabled")
        val FLASH_ENABLED = booleanPreferencesKey("flash_enabled")
    }

    val settings: Flow<UserSettings> = context.dataStore.data.map { prefs ->
        UserSettings(
            volumeThresholdDb = prefs[Keys.VOLUME_THRESHOLD]
                ?: UserSettings().volumeThresholdDb,
            classificationConfidence = prefs[Keys.CLASSIFICATION_CONFIDENCE]
                ?: UserSettings().classificationConfidence,
            minDurationMs = prefs[Keys.MIN_DURATION]
                ?: UserSettings().minDurationMs,
            startHour = prefs[Keys.START_HOUR]
                ?: UserSettings().startHour,
            endHour = prefs[Keys.END_HOUR]
                ?: UserSettings().endHour,
            classificationEnabled = prefs[Keys.CLASSIFICATION_ENABLED]
                ?: UserSettings().classificationEnabled,
            timeWindowEnabled = prefs[Keys.TIME_WINDOW_ENABLED]
                ?: UserSettings().timeWindowEnabled,
            flashEnabled = prefs[Keys.FLASH_ENABLED]
                ?: UserSettings().flashEnabled
        )
    }

    suspend fun updateSettings(settings: UserSettings) {
        context.dataStore.edit { prefs ->
            prefs[Keys.VOLUME_THRESHOLD] = settings.volumeThresholdDb
            prefs[Keys.CLASSIFICATION_CONFIDENCE] = settings.classificationConfidence
            prefs[Keys.MIN_DURATION] = settings.minDurationMs
            prefs[Keys.START_HOUR] = settings.startHour
            prefs[Keys.END_HOUR] = settings.endHour
            prefs[Keys.CLASSIFICATION_ENABLED] = settings.classificationEnabled
            prefs[Keys.TIME_WINDOW_ENABLED] = settings.timeWindowEnabled
            prefs[Keys.FLASH_ENABLED] = settings.flashEnabled
        }
    }
}
