package com.example.weightmanager.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.weightmanager.data.repository.NutritionRepository
import com.example.weightmanager.datastore.UserPreferencesRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class NutritionViewModel(
    private val nutritionRepository: NutritionRepository,
    private val userPreferencesRepository: UserPreferencesRepository
) : ViewModel() {

    private val _searchUiState = MutableStateFlow<NutritionSearchUiState>(NutritionSearchUiState.Idle)
    val searchUiState: StateFlow<NutritionSearchUiState> = _searchUiState.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private var searchJob: Job? = null

    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
        // 搜索防抖：500ms 后才发起搜索
        searchJob?.cancel()
        if (query.isBlank()) {
            _searchUiState.value = NutritionSearchUiState.Idle
            return
        }
        searchJob = viewModelScope.launch {
            delay(500)
            searchFood(query)
        }
    }

    fun searchFood(query: String) {
        if (query.isBlank()) return

        viewModelScope.launch {
            _searchUiState.value = NutritionSearchUiState.Loading
            try {
                // 保存搜索历史
                userPreferencesRepository.setLastSearchQuery(query)

                val result = nutritionRepository.searchFoodNutrition(query)
                result.fold(
                    onSuccess = { nutritionList ->
                        if (nutritionList.isEmpty()) {
                            _searchUiState.value = NutritionSearchUiState.Error("未找到相关食品营养信息")
                        } else {
                            _searchUiState.value = NutritionSearchUiState.Success(nutritionList)
                        }
                    },
                    onFailure = { error ->
                        _searchUiState.value = NutritionSearchUiState.Error(
                            error.message ?: "搜索失败，请稍后重试"
                        )
                    }
                )
            } catch (e: Exception) {
                _searchUiState.value = NutritionSearchUiState.Error(
                    e.message ?: "网络请求失败"
                )
            }
        }
    }

    fun clearSearch() {
        _searchQuery.value = ""
        _searchUiState.value = NutritionSearchUiState.Idle
    }

    class Factory(
        private val nutritionRepository: NutritionRepository,
        private val userPreferencesRepository: UserPreferencesRepository
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return NutritionViewModel(nutritionRepository, userPreferencesRepository) as T
        }
    }
}
