package `in`.bewithdhanu.medicinetracker.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import `in`.bewithdhanu.medicinetracker.data.model.User
import `in`.bewithdhanu.medicinetracker.data.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel for User management
 */
class UserViewModel(private val repository: UserRepository) : ViewModel() {
    
    private val _uiState = MutableStateFlow<UserUiState>(UserUiState.Loading)
    val uiState: StateFlow<UserUiState> = _uiState.asStateFlow()
    
    private val _users = MutableStateFlow<List<User>>(emptyList())
    val users: StateFlow<List<User>> = _users.asStateFlow()
    
    init {
        loadUsers()
    }
    
    fun loadUsers() {
        viewModelScope.launch {
            _uiState.value = UserUiState.Loading
            repository.getUsers()
                .onSuccess { usersList ->
                    _users.value = usersList
                    _uiState.value = UserUiState.Success
                }
                .onFailure { error ->
                    _uiState.value = UserUiState.Error(error.message ?: "Unknown error")
                }
        }
    }
    
    fun createUser(name: String, photoUrl: String? = null) {
        viewModelScope.launch {
            _uiState.value = UserUiState.Loading
            repository.createUser(name, photoUrl)
                .onSuccess { newUser ->
                    _users.value = _users.value + newUser
                    _uiState.value = UserUiState.Success
                }
                .onFailure { error ->
                    _uiState.value = UserUiState.Error(error.message ?: "Failed to create user")
                }
        }
    }
    
    fun updateUser(userId: Int, name: String? = null, photoUrl: String? = null) {
        viewModelScope.launch {
            _uiState.value = UserUiState.Loading
            repository.updateUser(userId, name, photoUrl)
                .onSuccess { updatedUser ->
                    _users.value = _users.value.map { 
                        if (it.id == userId) updatedUser else it 
                    }
                    _uiState.value = UserUiState.Success
                }
                .onFailure { error ->
                    _uiState.value = UserUiState.Error(error.message ?: "Failed to update user")
                }
        }
    }
    
    fun deleteUser(userId: Int) {
        viewModelScope.launch {
            _uiState.value = UserUiState.Loading
            repository.deleteUser(userId)
                .onSuccess {
                    _users.value = _users.value.filter { it.id != userId }
                    _uiState.value = UserUiState.Success
                }
                .onFailure { error ->
                    _uiState.value = UserUiState.Error(error.message ?: "Failed to delete user")
                }
        }
    }
}

/**
 * UI State for User screen
 */
sealed class UserUiState {
    object Loading : UserUiState()
    object Success : UserUiState()
    data class Error(val message: String) : UserUiState()
}

