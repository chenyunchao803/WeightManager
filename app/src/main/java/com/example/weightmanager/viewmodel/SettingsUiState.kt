package com.example.weightmanager.viewmodel

data class SettingsUiState(
    val weightUnit: String = "kg",
    val height: Float = 170f,
    val themeMode: String = "system",
    val notificationEnabled: Boolean = true,
    val heightText: String = "170",
    val targetWeightText: String = "65"
)
