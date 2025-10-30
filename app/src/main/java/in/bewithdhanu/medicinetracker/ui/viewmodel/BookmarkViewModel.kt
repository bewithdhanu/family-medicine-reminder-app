package `in`.bewithdhanu.medicinetracker.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import `in`.bewithdhanu.medicinetracker.data.model.Bookmark
import `in`.bewithdhanu.medicinetracker.data.model.CreateBookmarkRequest
import `in`.bewithdhanu.medicinetracker.data.model.UpdateBookmarkRequest
import `in`.bewithdhanu.medicinetracker.data.repository.BookmarkRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class BookmarkViewModel(private val repository: BookmarkRepository) : ViewModel() {

    private val _bookmarks = MutableStateFlow<List<Bookmark>>(emptyList())
    val bookmarks: StateFlow<List<Bookmark>> = _bookmarks.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    fun loadBookmarks() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            repository.getBookmarks()
                .onSuccess { _bookmarks.value = it }
                .onFailure { _error.value = it.message }
            _isLoading.value = false
        }
    }

    fun upsertBookmark(existingId: Int?, name: String, phone: String, type: String, photoUrl: String?, emoji: String?) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            if (existingId == null) {
                repository.createBookmark(
                    CreateBookmarkRequest(
                        name = name,
                        phoneNumber = phone,
                        contactType = type,
                        photoUrl = photoUrl,
                        avatarEmoji = emoji
                    )
                ).onSuccess { _bookmarks.value = _bookmarks.value + it }
                 .onFailure { _error.value = it.message }
            } else {
                repository.updateBookmark(
                    existingId,
                    UpdateBookmarkRequest(
                        name = name,
                        phoneNumber = phone,
                        contactType = type,
                        photoUrl = photoUrl,
                        avatarEmoji = emoji
                    )
                ).onSuccess { updated ->
                    _bookmarks.value = _bookmarks.value.map { if (it.id == existingId) updated else it }
                }.onFailure { _error.value = it.message }
            }
            _isLoading.value = false
        }
    }

    fun deleteBookmark(id: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            repository.deleteBookmark(id)
                .onSuccess { _bookmarks.value = _bookmarks.value.filterNot { it.id == id } }
                .onFailure { _error.value = it.message }
            _isLoading.value = false
        }
    }
}


