package com.example.weightmanager.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.weightmanager.data.entity.WeightRecordEntity
import com.example.weightmanager.data.repository.TargetRepository
import com.example.weightmanager.data.repository.WeightRepository
import com.example.weightmanager.datastore.UserPreferencesRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

class WeightViewModel(
    private val weightRepository: WeightRepository,
    private val targetRepository: TargetRepository,
    private val userPreferencesRepository: UserPreferencesRepository
) : ViewModel() {

    private val _listUiState = MutableStateFlow<WeightListUiState>(WeightListUiState.Loading)
    val listUiState: StateFlow<WeightListUiState> = _listUiState.asStateFlow()

    private val _addEditUiState = MutableStateFlow(AddEditWeightUiState())
    val addEditUiState: StateFlow<AddEditWeightUiState> = _addEditUiState.asStateFlow()

    private val _statsUiState = MutableStateFlow(StatsUiState())
    val statsUiState: StateFlow<StatsUiState> = _statsUiState.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _isSearching = MutableStateFlow(false)
    val isSearching: StateFlow<Boolean> = _isSearching.asStateFlow()

    private var editingRecordId: Long? = null

    init {
        loadRecords()
        loadStats()
    }

    private fun loadRecords() {
        viewModelScope.launch {
            weightRepository.getAllRecords().collect { records ->
                _listUiState.value = if (records.isEmpty()) {
                    WeightListUiState.Success(emptyList(), isEmpty = true)
                } else {
                    WeightListUiState.Success(records)
                }
            }
        }
    }

    fun loadStats() {
        viewModelScope.launch {
            _statsUiState.update { it.copy(isLoading = true) }
            try {
                val now = LocalDate.now()
                val weekAgo = now.minusWeeks(1).format(DateTimeFormatter.ISO_LOCAL_DATE)
                val today = now.format(DateTimeFormatter.ISO_LOCAL_DATE)

                val recordCount = weightRepository.getRecordCount()
                val avgWeight = weightRepository.getAverageWeight(weekAgo, today) ?: 0.0
                val minWeight = weightRepository.getMinWeight(weekAgo, today) ?: 0.0
                val maxWeight = weightRepository.getMaxWeight(weekAgo, today) ?: 0.0

                val recentRecords = weightRepository.getRecordsBetween(weekAgo, today).first()
                val weightChange = if (recentRecords.size >= 2) {
                    recentRecords.last().weight - recentRecords.first().weight
                } else 0.0

                val height = userPreferencesRepository.height.first()
                val bmi = if (height > 0 && avgWeight > 0) {
                    val heightM = height / 100.0
                    avgWeight / (heightM * heightM)
                } else 0.0

                val bmiCategory = when {
                    bmi <= 0 -> ""
                    bmi < 18.5 -> "偏瘦"
                    bmi < 24.0 -> "正常"
                    bmi < 28.0 -> "偏胖"
                    else -> "肥胖"
                }

                _statsUiState.update {
                    it.copy(
                        recordCount = recordCount,
                        averageWeight = avgWeight,
                        minWeight = minWeight,
                        maxWeight = maxWeight,
                        weightChange = weightChange,
                        bmi = bmi,
                        bmiCategory = bmiCategory,
                        weeklyRecords = recentRecords,
                        isLoading = false
                    )
                }
            } catch (e: Exception) {
                _statsUiState.update { it.copy(isLoading = false) }
            }
        }
    }

    // 搜索
    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
        if (query.isBlank()) {
            _isSearching.value = false
            loadRecords()
        } else {
            _isSearching.value = true
            viewModelScope.launch {
                userPreferencesRepository.setLastSearchQuery(query)
                weightRepository.searchRecords(query).collect { records ->
                    _listUiState.value = if (records.isEmpty()) {
                        WeightListUiState.Success(emptyList(), isEmpty = true)
                    } else {
                        WeightListUiState.Success(records)
                    }
                }
            }
        }
    }

    fun clearSearch() {
        _searchQuery.value = ""
        _isSearching.value = false
        loadRecords()
    }

    // 添加/编辑记录
    fun onWeightChange(weight: String) {
        _addEditUiState.update {
            it.copy(weight = weight, weightError = null)
        }
    }

    fun onDateChange(date: String) {
        _addEditUiState.update {
            it.copy(date = date, dateError = null)
        }
    }

    fun onNoteChange(note: String) {
        _addEditUiState.update { it.copy(note = note) }
    }

    fun onMoodChange(mood: Int) {
        _addEditUiState.update { it.copy(mood = mood) }
    }

    fun loadRecordForEdit(recordId: Long) {
        viewModelScope.launch {
            val record = weightRepository.getRecordById(recordId)
            if (record != null) {
                editingRecordId = recordId
                _addEditUiState.update {
                    it.copy(
                        weight = record.weight.toString(),
                        date = record.recordDate,
                        note = record.note,
                        mood = record.mood,
                        isEditMode = true
                    )
                }
            }
        }
    }

    fun resetAddEditState() {
        editingRecordId = null
        _addEditUiState.value = AddEditWeightUiState()
    }

    fun saveRecord() {
        val state = _addEditUiState.value
        var hasError = false

        // 输入验证
        if (state.weight.isBlank()) {
            _addEditUiState.update { it.copy(weightError = "请输入体重") }
            hasError = true
        } else {
            val weightValue = state.weight.toDoubleOrNull()
            if (weightValue == null || weightValue <= 0 || weightValue > 500) {
                _addEditUiState.update { it.copy(weightError = "请输入有效的体重 (0-500)") }
                hasError = true
            }
        }

        if (state.date.isBlank()) {
            _addEditUiState.update { it.copy(dateError = "请选择日期") }
            hasError = true
        }

        if (hasError) return

        viewModelScope.launch {
            try {
                _addEditUiState.update { it.copy(isSaving = true, errorMessage = null) }

                val record = WeightRecordEntity(
                    id = editingRecordId ?: 0,
                    weight = state.weight.toDouble(),
                    recordDate = state.date,
                    note = state.note,
                    mood = state.mood
                )

                if (editingRecordId != null) {
                    weightRepository.update(record)
                } else {
                    // 检查同一天是否已有记录
                    val existing = weightRepository.getRecordByDate(state.date)
                    if (existing != null) {
                        weightRepository.update(record.copy(id = existing.id))
                    } else {
                        weightRepository.insert(record)
                    }
                }

                _addEditUiState.update { it.copy(isSaving = false, saveSuccess = true) }
                loadStats()
            } catch (e: Exception) {
                _addEditUiState.update {
                    it.copy(isSaving = false, errorMessage = "保存失败: ${e.message}")
                }
            }
        }
    }

    fun deleteRecord(record: WeightRecordEntity) {
        viewModelScope.launch {
            try {
                weightRepository.delete(record)
                loadStats()
            } catch (e: Exception) {
                // 处理错误
            }
        }
    }

    fun deleteRecordById(id: Long) {
        viewModelScope.launch {
            try {
                weightRepository.deleteById(id)
                loadStats()
            } catch (e: Exception) {
                // 处理错误
            }
        }
    }

    // 统计相关
    fun getDateRangeLabel(): String {
        val now = LocalDate.now()
        val weekAgo = now.minusWeeks(1)
        return "${weekAgo.format(DateTimeFormatter.ofPattern("MM/dd"))} - ${now.format(DateTimeFormatter.ofPattern("MM/dd"))}"
    }

    class Factory(
        private val weightRepository: WeightRepository,
        private val targetRepository: TargetRepository,
        private val userPreferencesRepository: UserPreferencesRepository
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return WeightViewModel(weightRepository, targetRepository, userPreferencesRepository) as T
        }
    }
}
