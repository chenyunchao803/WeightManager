package com.example.weightmanager.viewmodel

import com.example.weightmanager.data.network.dto.NutritionInfo

sealed interface NutritionSearchUiState {
    data object Idle : NutritionSearchUiState
    data object Loading : NutritionSearchUiState
    data class Success(val results: List<NutritionInfo>) : NutritionSearchUiState
    data class Error(val message: String) : NutritionSearchUiState
}
