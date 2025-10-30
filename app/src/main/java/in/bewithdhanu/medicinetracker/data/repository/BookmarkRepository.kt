package `in`.bewithdhanu.medicinetracker.data.repository

import `in`.bewithdhanu.medicinetracker.data.model.Bookmark
import `in`.bewithdhanu.medicinetracker.data.model.CreateBookmarkRequest
import `in`.bewithdhanu.medicinetracker.data.model.UpdateBookmarkRequest
import `in`.bewithdhanu.medicinetracker.data.remote.ApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class BookmarkRepository(private val apiService: ApiService) {

    suspend fun getBookmarks(): Result<List<Bookmark>> = withContext(Dispatchers.IO) {
        try {
            val resp = apiService.getBookmarks()
            if (resp.isSuccessful && resp.body() != null) Result.success(resp.body()!!)
            else Result.failure(Exception("Failed to fetch bookmarks: ${resp.code()}"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun createBookmark(request: CreateBookmarkRequest): Result<Bookmark> = withContext(Dispatchers.IO) {
        try {
            val resp = apiService.createBookmark(request)
            if (resp.isSuccessful && resp.body() != null) Result.success(resp.body()!!)
            else Result.failure(Exception("Failed to create bookmark: ${resp.code()}"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateBookmark(id: Int, request: UpdateBookmarkRequest): Result<Bookmark> = withContext(Dispatchers.IO) {
        try {
            val resp = apiService.updateBookmark(id, request)
            if (resp.isSuccessful && resp.body() != null) Result.success(resp.body()!!)
            else Result.failure(Exception("Failed to update bookmark: ${resp.code()}"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteBookmark(id: Int): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val resp = apiService.deleteBookmark(id)
            if (resp.isSuccessful) Result.success(Unit)
            else Result.failure(Exception("Failed to delete bookmark: ${resp.code()}"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}


