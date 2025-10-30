package `in`.bewithdhanu.medicinetracker.data.repository

import `in`.bewithdhanu.medicinetracker.data.model.*
import `in`.bewithdhanu.medicinetracker.data.remote.ApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Repository for Medicine data
 * Handles API calls for medicine CRUD operations
 */
class MedicineRepository(private val apiService: ApiService) {
    
    suspend fun getMedicines(
        userId: Int? = null,
        isActive: Boolean? = null
    ): Result<List<Medicine>> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.getMedicines(userId, isActive)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Failed to fetch medicines: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun getMedicine(medicineId: Int): Result<Medicine> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.getMedicine(medicineId)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Failed to fetch medicine: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun createMedicine(
        userId: Int,
        name: String,
        type: MedicineType,
        dosage: String? = null,
        instructions: String? = null,
        imageUrl: String? = null
    ): Result<Medicine> = withContext(Dispatchers.IO) {
        try {
            val request = CreateMedicineRequest(
                userId = userId,
                name = name,
                type = type,
                dosage = dosage,
                instructions = instructions,
                imageUrl = imageUrl
            )
            val response = apiService.createMedicine(request)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Failed to create medicine: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun updateMedicine(
        medicineId: Int,
        name: String? = null,
        dosage: String? = null,
        instructions: String? = null,
        imageUrl: String? = null,
        isActive: Boolean? = null
    ): Result<Medicine> = withContext(Dispatchers.IO) {
        try {
            val request = UpdateMedicineRequest(
                name = name,
                dosage = dosage,
                instructions = instructions,
                imageUrl = imageUrl,
                isActive = isActive
            )
            val response = apiService.updateMedicine(medicineId, request)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Failed to update medicine: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun deleteMedicine(medicineId: Int): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.deleteMedicine(medicineId)
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("Failed to delete medicine: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

