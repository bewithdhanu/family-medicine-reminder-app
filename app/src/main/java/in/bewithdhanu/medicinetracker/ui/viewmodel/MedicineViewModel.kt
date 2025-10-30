package `in`.bewithdhanu.medicinetracker.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import `in`.bewithdhanu.medicinetracker.data.model.Medicine
import `in`.bewithdhanu.medicinetracker.data.model.MedicineType
import `in`.bewithdhanu.medicinetracker.data.repository.MedicineRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel for Medicine management
 */
class MedicineViewModel(private val repository: MedicineRepository) : ViewModel() {
    
    private val _uiState = MutableStateFlow<MedicineUiState>(MedicineUiState.Loading)
    val uiState: StateFlow<MedicineUiState> = _uiState.asStateFlow()
    
    private val _medicines = MutableStateFlow<List<Medicine>>(emptyList())
    val medicines: StateFlow<List<Medicine>> = _medicines.asStateFlow()
    
    private val _selectedUserId = MutableStateFlow<Int?>(null)
    val selectedUserId: StateFlow<Int?> = _selectedUserId.asStateFlow()
    
    fun loadMedicines(userId: Int? = null, isActive: Boolean? = true) {
        viewModelScope.launch {
            _uiState.value = MedicineUiState.Loading
            _selectedUserId.value = userId
            
            repository.getMedicines(userId, isActive)
                .onSuccess { medicinesList ->
                    _medicines.value = medicinesList
                    _uiState.value = MedicineUiState.Success
                }
                .onFailure { error ->
                    _uiState.value = MedicineUiState.Error(error.message ?: "Unknown error")
                }
        }
    }
    
    fun createMedicine(
        userId: Int,
        name: String,
        type: MedicineType,
        dosage: String? = null,
        instructions: String? = null,
        imageUrl: String? = null
    ) {
        viewModelScope.launch {
            _uiState.value = MedicineUiState.Loading
            repository.createMedicine(userId, name, type, dosage, instructions, imageUrl)
                .onSuccess { newMedicine ->
                    _medicines.value = _medicines.value + newMedicine
                    _uiState.value = MedicineUiState.Success
                }
                .onFailure { error ->
                    _uiState.value = MedicineUiState.Error(
                        error.message ?: "Failed to create medicine"
                    )
                }
        }
    }
    
    fun updateMedicine(
        medicineId: Int,
        name: String? = null,
        dosage: String? = null,
        instructions: String? = null,
        imageUrl: String? = null,
        isActive: Boolean? = null
    ) {
        viewModelScope.launch {
            _uiState.value = MedicineUiState.Loading
            repository.updateMedicine(medicineId, name, dosage, instructions, imageUrl, isActive)
                .onSuccess { updatedMedicine ->
                    _medicines.value = _medicines.value.map { 
                        if (it.id == medicineId) updatedMedicine else it 
                    }
                    _uiState.value = MedicineUiState.Success
                }
                .onFailure { error ->
                    _uiState.value = MedicineUiState.Error(
                        error.message ?: "Failed to update medicine"
                    )
                }
        }
    }
    
    fun deleteMedicine(medicineId: Int) {
        viewModelScope.launch {
            _uiState.value = MedicineUiState.Loading
            repository.deleteMedicine(medicineId)
                .onSuccess {
                    _medicines.value = _medicines.value.filter { it.id != medicineId }
                    _uiState.value = MedicineUiState.Success
                }
                .onFailure { error ->
                    _uiState.value = MedicineUiState.Error(
                        error.message ?: "Failed to delete medicine"
                    )
                }
        }
    }
    
    fun toggleMedicineStatus(medicineId: Int, currentStatus: Boolean) {
        updateMedicine(medicineId, isActive = !currentStatus)
    }
}

/**
 * UI State for Medicine screen
 */
sealed class MedicineUiState {
    object Loading : MedicineUiState()
    object Success : MedicineUiState()
    data class Error(val message: String) : MedicineUiState()
}

