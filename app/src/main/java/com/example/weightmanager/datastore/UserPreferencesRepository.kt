package com.example.weightmanager.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_preferences")

class UserPreferencesRepository(private val context: Context) {

    companion object {
        val KEY_WEIGHT_UNIT = stringPreferencesKey("weight_unit") // "kg" or "lb"
        val KEY_HEIGHT = floatPreferencesKey("height_cm")
        val KEY_TARGET_WEIGHT = floatPreferencesKey("target_weight")
        val KEY_THEME_MODE = stringPreferencesKey("theme_mode") // "system", "light", "dark"
        val KEY_LAST_SEARCH_QUERY = stringPreferencesKey("last_search_query")
        val KEY_NOTIFICATION_ENABLED = booleanPreferencesKey("notification_enabled")
        val KEY_FIRST_LAUNCH = booleanPreferencesKey("first_launch")
    }

    // 体重单位偏好
    val weightUnit: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[KEY_WEIGHT_UNIT] ?: "kg"
    }

    suspend fun setWeightUnit(unit: String) {
        context.dataStore.edit { preferences ->
            preferences[KEY_WEIGHT_UNIT] = unit
        }
    }

    // 身高偏好
    val height: Flow<Float> = context.dataStore.data.map { preferences ->
        preferences[KEY_HEIGHT] ?: 170f
    }

    suspend fun setHeight(heightCm: Float) {
        context.dataStore.edit { preferences ->
            preferences[KEY_HEIGHT] = heightCm
        }
    }

    // 主题模式偏好
    val themeMode: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[KEY_THEME_MODE] ?: "system"
    }

    suspend fun setThemeMode(mode: String) {
        context.dataStore.edit { preferences ->
            preferences[KEY_THEME_MODE] = mode
        }
    }

    // 最近搜索词
    val lastSearchQuery: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[KEY_LAST_SEARCH_QUERY] ?: ""
    }

    suspend fun setLastSearchQuery(query: String) {
        context.dataStore.edit { preferences ->
            preferences[KEY_LAST_SEARCH_QUERY] = query
        }
    }

    // 通知开关
    val notificationEnabled: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[KEY_NOTIFICATION_ENABLED] ?: true
    }

    suspend fun setNotificationEnabled(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[KEY_NOTIFICATION_ENABLED] = enabled
        }
    }

    // 是否首次启动
    val isFirstLaunch: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[KEY_FIRST_LAUNCH] ?: true
    }

    suspend fun setFirstLaunchCompleted() {
        context.dataStore.edit { preferences ->
            preferences[KEY_FIRST_LAUNCH] = false
        }
    }

    // 目标体重
    val targetWeight: Flow<Float> = context.dataStore.data.map { preferences ->
        preferences[KEY_TARGET_WEIGHT] ?: 65f
    }

    suspend fun setTargetWeight(weight: Float) {
        context.dataStore.edit { preferences ->
            preferences[KEY_TARGET_WEIGHT] = weight
        }
    }
}
