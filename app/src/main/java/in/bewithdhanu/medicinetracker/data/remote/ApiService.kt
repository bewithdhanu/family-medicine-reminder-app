package `in`.bewithdhanu.medicinetracker.data.remote

import `in`.bewithdhanu.medicinetracker.data.model.*
import retrofit2.Response
import retrofit2.http.*

/**
 * API Service interface for all backend endpoints
 * Base URL and API Key are configured in ApiConfig (from local.properties)
 */
interface ApiService {
    
    // ==================== Users ====================
    
    @GET("api/users/")
    suspend fun getUsers(): Response<List<User>>
    
    @GET("api/users/{id}")
    suspend fun getUser(@Path("id") userId: Int): Response<User>
    
    @POST("api/users/")
    suspend fun createUser(@Body request: CreateUserRequest): Response<User>
    
    @PUT("api/users/{id}")
    suspend fun updateUser(
        @Path("id") userId: Int,
        @Body request: UpdateUserRequest
    ): Response<User>
    
    @DELETE("api/users/{id}")
    suspend fun deleteUser(@Path("id") userId: Int): Response<Unit>
    
    // ==================== Medicines ====================
    
    @GET("api/medicines/")
    suspend fun getMedicines(
        @Query("user_id") userId: Int? = null,
        @Query("is_active") isActive: Boolean? = null
    ): Response<List<Medicine>>
    
    @GET("api/medicines/{id}")
    suspend fun getMedicine(@Path("id") medicineId: Int): Response<Medicine>
    
    @POST("api/medicines/")
    suspend fun createMedicine(@Body request: CreateMedicineRequest): Response<Medicine>
    
    @PUT("api/medicines/{id}")
    suspend fun updateMedicine(
        @Path("id") medicineId: Int,
        @Body request: UpdateMedicineRequest
    ): Response<Medicine>
    
    @DELETE("api/medicines/{id}")
    suspend fun deleteMedicine(@Path("id") medicineId: Int): Response<Unit>
    
    // ==================== Reminders ====================
    
    @GET("api/reminders/")
    suspend fun getReminders(
        @Query("medicine_id") medicineId: Int? = null
    ): Response<List<Reminder>>
    
    @POST("api/reminders/")
    suspend fun createReminder(@Body request: CreateReminderRequest): Response<Reminder>
    
    @DELETE("api/reminders/{id}")
    suspend fun deleteReminder(@Path("id") reminderId: Int): Response<Unit>
    
    // ==================== Medicine Logs ====================
    
    @GET("api/reminders/logs")
    suspend fun getMedicineLogs(
        @Query("user_id") userId: Int? = null,
        @Query("medicine_id") medicineId: Int? = null
    ): Response<List<MedicineLog>>
    
    @GET("api/reminders/logs/missed")
    suspend fun getMissedMedicines(
        @Query("user_id") userId: Int? = null
    ): Response<List<MedicineLog>>
    
    @POST("api/reminders/logs")
    suspend fun createMedicineLog(@Body request: CreateMedicineLogRequest): Response<MedicineLog>
    
    @PUT("api/reminders/logs/{id}")
    suspend fun updateMedicineLog(
        @Path("id") logId: Int,
        @Body request: UpdateMedicineLogRequest
    ): Response<MedicineLog>
    
    // ==================== Insulin ====================
    
    @GET("api/insulin/")
    suspend fun getInsulinLogs(
        @Query("user_id") userId: Int? = null
    ): Response<List<InsulinLog>>
    
    @GET("api/insulin/daily")
    suspend fun getDailyInsulinLogs(
        @Query("user_id") userId: Int,
        @Query("date") date: String? = null
    ): Response<List<InsulinLog>>
    
    @GET("api/insulin/weekly")
    suspend fun getWeeklyInsulinStats(
        @Query("user_id") userId: Int
    ): Response<InsulinStats>
    
    @GET("api/insulin/monthly")
    suspend fun getMonthlyInsulinStats(
        @Query("user_id") userId: Int
    ): Response<InsulinStats>
    
    @GET("api/insulin/suggest-dosage")
    suspend fun suggestInsulinDosage(
        @Query("glucose_reading") glucoseReading: Float
    ): Response<InsulinDosageSuggestion>
    
    @POST("api/insulin/")
    suspend fun createInsulinLog(@Body request: CreateInsulinLogRequest): Response<InsulinLog>
}

