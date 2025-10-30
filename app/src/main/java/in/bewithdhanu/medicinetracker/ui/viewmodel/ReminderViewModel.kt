package `in`.bewithdhanu.medicinetracker.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import `in`.bewithdhanu.medicinetracker.data.model.Medicine
import `in`.bewithdhanu.medicinetracker.data.model.Reminder
import `in`.bewithdhanu.medicinetracker.data.model.ReminderStatus
import `in`.bewithdhanu.medicinetracker.data.repository.ReminderRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel for Reminder management
 */
class ReminderViewModel(private val repository: ReminderRepository) : ViewModel() {
    
    private val _uiState = MutableStateFlow<ReminderUiState>(ReminderUiState.Loading)
    val uiState: StateFlow<ReminderUiState> = _uiState.asStateFlow()
    
    private val _reminders = MutableStateFlow<List<Reminder>>(emptyList())
    val reminders: StateFlow<List<Reminder>> = _reminders.asStateFlow()
    
    fun loadReminders(medicineId: Int? = null) {
        viewModelScope.launch {
            _uiState.value = ReminderUiState.Loading
            repository.getReminders(medicineId)
                .onSuccess { remindersList ->
                    _reminders.value = remindersList
                    _uiState.value = ReminderUiState.Success
                }
                .onFailure { error ->
                    _uiState.value = ReminderUiState.Error(error.message ?: "Unknown error")
                }
        }
    }
    
    fun createReminder(medicineId: Int, scheduledTime: String) {
        viewModelScope.launch {
            _uiState.value = ReminderUiState.Loading
            repository.createReminder(medicineId, scheduledTime)
                .onSuccess { newReminder ->
                    _reminders.value = _reminders.value + newReminder
                    _uiState.value = ReminderUiState.Success
                }
                .onFailure { error ->
                    _uiState.value = ReminderUiState.Error(
                        error.message ?: "Failed to create reminder"
                    )
                }
        }
    }
    
    fun deleteReminder(reminderId: Int) {
        viewModelScope.launch {
            _uiState.value = ReminderUiState.Loading
            repository.deleteReminder(reminderId)
                .onSuccess {
                    _reminders.value = _reminders.value.filter { it.id != reminderId }
                    _uiState.value = ReminderUiState.Success
                }
                .onFailure { error ->
                    _uiState.value = ReminderUiState.Error(
                        error.message ?: "Failed to delete reminder"
                    )
                }
        }
    }
    
    fun markAsTaken(userId: Int, medicineId: Int, reminderId: Int, scheduledAt: String) {
        viewModelScope.launch {
            repository.createMedicineLog(
                userId = userId,
                medicineId = medicineId,
                reminderId = reminderId,
                status = ReminderStatus.TAKEN,
                scheduledAt = scheduledAt,
                notes = null
            )
        }
    }
}

/**
 * UI State for Reminder screen
 */
sealed class ReminderUiState {
    object Loading : ReminderUiState()
    object Success : ReminderUiState()
    data class Error(val message: String) : ReminderUiState()
}

