package `in`.bewithdhanu.medicinetracker.data.repository

import `in`.bewithdhanu.medicinetracker.data.model.*
import `in`.bewithdhanu.medicinetracker.data.remote.ApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Repository for Reminder data
 * Handles reminder scheduling and medicine logs
 */
class ReminderRepository(private val apiService: ApiService) {
    
    // ==================== Reminders ====================
    
    suspend fun getReminders(medicineId: Int? = null): Result<List<Reminder>> = 
        withContext(Dispatchers.IO) {
            try {
                val response = apiService.getReminders(medicineId)
                if (response.isSuccessful && response.body() != null) {
                    Result.success(response.body()!!)
                } else {
                    Result.failure(Exception("Failed to fetch reminders: ${response.code()}"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    
    suspend fun createReminder(
        medicineId: Int,
        scheduledTime: String
    ): Result<Reminder> = withContext(Dispatchers.IO) {
        try {
            val request = CreateReminderRequest(
                medicineId = medicineId,
                scheduledTime = scheduledTime
            )
            val response = apiService.createReminder(request)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Failed to create reminder: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun deleteReminder(reminderId: Int): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.deleteReminder(reminderId)
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("Failed to delete reminder: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    // ==================== Medicine Logs ====================
    
    suspend fun getMedicineLogs(
        userId: Int? = null,
        medicineId: Int? = null
    ): Result<List<MedicineLog>> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.getMedicineLogs(userId, medicineId)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Failed to fetch logs: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun getMissedMedicines(userId: Int? = null): Result<List<MedicineLog>> = 
        withContext(Dispatchers.IO) {
            try {
                val response = apiService.getMissedMedicines(userId)
                if (response.isSuccessful && response.body() != null) {
                    Result.success(response.body()!!)
                } else {
                    Result.failure(Exception("Failed to fetch missed medicines: ${response.code()}"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    
    suspend fun createMedicineLog(
        userId: Int,
        medicineId: Int,
        reminderId: Int? = null,
        status: ReminderStatus,
        scheduledAt: String,
        notes: String? = null
    ): Result<MedicineLog> = withContext(Dispatchers.IO) {
        try {
            val request = CreateMedicineLogRequest(
                userId = userId,
                medicineId = medicineId,
                reminderId = reminderId,
                status = status,
                scheduledAt = scheduledAt,
                notes = notes
            )
            val response = apiService.createMedicineLog(request)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Failed to create log: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun updateMedicineLog(
        logId: Int,
        status: ReminderStatus? = null,
        takenAt: String? = null,
        snoozeCount: Int? = null,
        notes: String? = null
    ): Result<MedicineLog> = withContext(Dispatchers.IO) {
        try {
            val request = UpdateMedicineLogRequest(
                status = status,
                takenAt = takenAt,
                snoozeCount = snoozeCount,
                notes = notes
            )
            val response = apiService.updateMedicineLog(logId, request)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Failed to update log: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

