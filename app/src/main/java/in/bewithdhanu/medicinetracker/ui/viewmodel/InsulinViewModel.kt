package `in`.bewithdhanu.medicinetracker.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import `in`.bewithdhanu.medicinetracker.data.model.InsulinDosageSuggestion
import `in`.bewithdhanu.medicinetracker.data.model.InsulinLog
import `in`.bewithdhanu.medicinetracker.data.model.InsulinStats
import `in`.bewithdhanu.medicinetracker.data.repository.InsulinRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel for Insulin tracking
 */
class InsulinViewModel(private val repository: InsulinRepository) : ViewModel() {
    
    private val _uiState = MutableStateFlow<InsulinUiState>(InsulinUiState.Loading)
    val uiState: StateFlow<InsulinUiState> = _uiState.asStateFlow()
    
    private val _insulinLogs = MutableStateFlow<List<InsulinLog>>(emptyList())
    val insulinLogs: StateFlow<List<InsulinLog>> = _insulinLogs.asStateFlow()
    
    private val _stats = MutableStateFlow<InsulinStats?>(null)
    val stats: StateFlow<InsulinStats?> = _stats.asStateFlow()
    
    private val _dosageSuggestion = MutableStateFlow<InsulinDosageSuggestion?>(null)
    val dosageSuggestion: StateFlow<InsulinDosageSuggestion?> = _dosageSuggestion.asStateFlow()
    
    fun loadInsulinLogs(userId: Int? = null) {
        viewModelScope.launch {
            _uiState.value = InsulinUiState.Loading
            repository.getInsulinLogs(userId)
                .onSuccess { logs ->
                    _insulinLogs.value = logs
                    _uiState.value = InsulinUiState.Success
                }
                .onFailure { error ->
                    _uiState.value = InsulinUiState.Error(error.message ?: "Unknown error")
                }
        }
    }
    
    fun createInsulinLog(
        userId: Int,
        glucoseReading: Float,
        insulinDosage: Float,
        notes: String? = null
    ) {
        viewModelScope.launch {
            _uiState.value = InsulinUiState.Loading
            repository.createInsulinLog(
                userId = userId,
                glucoseReading = glucoseReading,
                insulinDosage = insulinDosage,
                suggestedDosage = _dosageSuggestion.value?.suggestedDosage,
                notes = notes
            )
                .onSuccess { newLog ->
                    _insulinLogs.value = listOf(newLog) + _insulinLogs.value
                    _dosageSuggestion.value = null // Clear suggestion
                    _uiState.value = InsulinUiState.Success
                }
                .onFailure { error ->
                    _uiState.value = InsulinUiState.Error(
                        error.message ?: "Failed to create insulin log"
                    )
                }
        }
    }
    
    fun getSuggestedDosage(glucoseReading: Float) {
        viewModelScope.launch {
            repository.getSuggestedDosage(glucoseReading)
                .onSuccess { suggestion ->
                    _dosageSuggestion.value = suggestion
                }
                .onFailure { error ->
                    _dosageSuggestion.value = null
                }
        }
    }
    
    fun loadStats(userId: Int, period: String = "weekly") {
        viewModelScope.launch {
            val result = when (period.lowercase()) {
                "weekly" -> repository.getWeeklyStats(userId)
                "monthly" -> repository.getMonthlyStats(userId)
                else -> repository.getWeeklyStats(userId)
            }
            result.onSuccess { statsData ->
                _stats.value = statsData
            }.onFailure { error ->
                _stats.value = null
            }
        }
    }
    
    fun clearDosageSuggestion() {
        _dosageSuggestion.value = null
    }
}

/**
 * UI State for Insulin screen
 */
sealed class InsulinUiState {
    object Loading : InsulinUiState()
    object Success : InsulinUiState()
    data class Error(val message: String) : InsulinUiState()
}

