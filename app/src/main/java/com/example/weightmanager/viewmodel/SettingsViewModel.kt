package com.example.weightmanager.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.weightmanager.datastore.UserPreferencesRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val userPreferencesRepository: UserPreferencesRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    init {
        loadPreferences()
    }

    private fun loadPreferences() {
        viewModelScope.launch {
            val unit = userPreferencesRepository.weightUnit.first()
            val height = userPreferencesRepository.height.first()
            val theme = userPreferencesRepository.themeMode.first()
            val notification = userPreferencesRepository.notificationEnabled.first()
            val targetWeight = userPreferencesRepository.targetWeight.first()

            _uiState.update {
                it.copy(
                    weightUnit = unit,
                    height = height,
                    themeMode = theme,
                    notificationEnabled = notification,
                    heightText = height.toInt().toString(),
                    targetWeightText = targetWeight.toInt().toString()
                )
            }
        }
    }

    fun onWeightUnitChange(unit: String) {
        _uiState.update { it.copy(weightUnit = unit) }
        viewModelScope.launch {
            userPreferencesRepository.setWeightUnit(unit)
        }
    }

    fun onHeightChange(heightText: String) {
        _uiState.update { it.copy(heightText = heightText) }
        val height = heightText.toFloatOrNull()
        if (height != null && height > 0 && height < 300) {
            viewModelScope.launch {
                userPreferencesRepository.setHeight(height)
                _uiState.update { it.copy(height = height) }
            }
        }
    }

    fun onTargetWeightChange(targetText: String) {
        _uiState.update { it.copy(targetWeightText = targetText) }
        val weight = targetText.toFloatOrNull()
        if (weight != null && weight > 0 && weight < 500) {
            viewModelScope.launch {
                userPreferencesRepository.setTargetWeight(weight)
            }
        }
    }

    fun onThemeModeChange(mode: String) {
        _uiState.update { it.copy(themeMode = mode) }
        viewModelScope.launch {
            userPreferencesRepository.setThemeMode(mode)
        }
    }

    fun onNotificationToggle(enabled: Boolean) {
        _uiState.update { it.copy(notificationEnabled = enabled) }
        viewModelScope.launch {
            userPreferencesRepository.setNotificationEnabled(enabled)
        }
    }

    class Factory(
        private val userPreferencesRepository: UserPreferencesRepository
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return SettingsViewModel(userPreferencesRepository) as T
        }
    }
}
