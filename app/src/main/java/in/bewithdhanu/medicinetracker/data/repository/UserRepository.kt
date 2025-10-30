package `in`.bewithdhanu.medicinetracker.data.repository

import `in`.bewithdhanu.medicinetracker.data.model.*
import `in`.bewithdhanu.medicinetracker.data.remote.ApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Repository for User data
 * Handles API calls and data caching
 */
class UserRepository(private val apiService: ApiService) {
    
    suspend fun getUsers(): Result<List<User>> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.getUsers()
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Failed to fetch users: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun getUser(userId: Int): Result<User> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.getUser(userId)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Failed to fetch user: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun createUser(name: String, photoUrl: String? = null): Result<User> = 
        withContext(Dispatchers.IO) {
            try {
                val request = CreateUserRequest(name = name, photoUrl = photoUrl)
                val response = apiService.createUser(request)
                if (response.isSuccessful && response.body() != null) {
                    Result.success(response.body()!!)
                } else {
                    Result.failure(Exception("Failed to create user: ${response.code()}"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    
    suspend fun updateUser(
        userId: Int,
        name: String? = null,
        photoUrl: String? = null
    ): Result<User> = withContext(Dispatchers.IO) {
        try {
            val request = UpdateUserRequest(name = name, photoUrl = photoUrl)
            val response = apiService.updateUser(userId, request)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Failed to update user: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun deleteUser(userId: Int): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.deleteUser(userId)
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("Failed to delete user: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

