package `in`.bewithdhanu.medicinetracker.data.repository

import `in`.bewithdhanu.medicinetracker.data.model.*
import `in`.bewithdhanu.medicinetracker.data.remote.ApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Repository for Insulin tracking
 * Handles glucose readings and insulin dosage management
 */
class InsulinRepository(private val apiService: ApiService) {
    
    suspend fun getInsulinLogs(userId: Int? = null): Result<List<InsulinLog>> = 
        withContext(Dispatchers.IO) {
            try {
                val response = apiService.getInsulinLogs(userId)
                if (response.isSuccessful && response.body() != null) {
                    Result.success(response.body()!!)
                } else {
                    Result.failure(Exception("Failed to fetch insulin logs: ${response.code()}"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    
    suspend fun createInsulinLog(
        userId: Int,
        glucoseReading: Float,
        insulinDosage: Float,
        medicineLogId: Int? = null,
        suggestedDosage: Float? = null,
        notes: String? = null
    ): Result<InsulinLog> = withContext(Dispatchers.IO) {
        try {
            val request = CreateInsulinLogRequest(
                userId = userId,
                medicineLogId = medicineLogId,
                glucoseReading = glucoseReading,
                insulinDosage = insulinDosage,
                suggestedDosage = suggestedDosage,
                notes = notes
            )
            val response = apiService.createInsulinLog(request)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Failed to create insulin log: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun getSuggestedDosage(glucoseReading: Float): Result<InsulinDosageSuggestion> = 
        withContext(Dispatchers.IO) {
            try {
                val response = apiService.getInsulinDosageSuggestion(glucoseReading)
                if (response.isSuccessful && response.body() != null) {
                    Result.success(response.body()!!)
                } else {
                    Result.failure(Exception("Failed to get dosage suggestion: ${response.code()}"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    
    suspend fun getInsulinStats(userId: Int, period: String): Result<InsulinStats> = 
        withContext(Dispatchers.IO) {
            try {
                val response = apiService.getInsulinStats(userId, period)
                if (response.isSuccessful && response.body() != null) {
                    Result.success(response.body()!!)
                } else {
                    Result.failure(Exception("Failed to fetch insulin stats: ${response.code()}"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
}

