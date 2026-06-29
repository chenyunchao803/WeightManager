package com.example.weightmanager.viewmodel

import com.example.weightmanager.data.entity.WeightRecordEntity

sealed interface WeightListUiState {
    data object Loading : WeightListUiState
    data class Success(
        val records: List<WeightRecordEntity>,
        val isEmpty: Boolean = false
    ) : WeightListUiState
    data class Error(val message: String) : WeightListUiState
}

sealed interface WeightDetailUiState {
    data object Loading : WeightDetailUiState
    data class Success(val record: WeightRecordEntity) : WeightDetailUiState
    data class Error(val message: String) : WeightDetailUiState
}

data class AddEditWeightUiState(
    val weight: String = "",
    val date: String = "",
    val note: String = "",
    val mood: Int = 0,
    val isSaving: Boolean = false,
    val isEditMode: Boolean = false,
    val weightError: String? = null,
    val dateError: String? = null,
    val saveSuccess: Boolean = false,
    val errorMessage: String? = null
)

data class StatsUiState(
    val recordCount: Int = 0,
    val averageWeight: Double = 0.0,
    val minWeight: Double = 0.0,
    val maxWeight: Double = 0.0,
    val weightChange: Double = 0.0,
    val bmi: Double = 0.0,
    val bmiCategory: String = "",
    val weeklyRecords: List<WeightRecordEntity> = emptyList(),
    val isLoading: Boolean = true
)
