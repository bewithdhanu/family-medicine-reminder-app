package `in`.bewithdhanu.medicinetracker.data.model

import com.google.gson.annotations.SerializedName

// ==================== User Models ====================

data class User(
    @SerializedName("id") val id: Int,
    @SerializedName("name") val name: String,
    @SerializedName("photo_url") val photoUrl: String?,
    @SerializedName("avatar_emoji") val avatarEmoji: String? = null,
    @SerializedName("created_at") val createdAt: String,
    @SerializedName("updated_at") val updatedAt: String
)

data class CreateUserRequest(
    @SerializedName("name") val name: String,
    @SerializedName("photo_url") val photoUrl: String? = null,
    @SerializedName("avatar_emoji") val avatarEmoji: String? = null
)

data class UpdateUserRequest(
    @SerializedName("name") val name: String? = null,
    @SerializedName("photo_url") val photoUrl: String? = null,
    @SerializedName("avatar_emoji") val avatarEmoji: String? = null
)

// ==================== Bookmark Models ====================

data class Bookmark(
    @SerializedName("id") val id: Int,
    @SerializedName("name") val name: String,
    @SerializedName("phone_number") val phoneNumber: String,
    @SerializedName("contact_type") val contactType: String, // "phone" or "whatsapp"
    @SerializedName("photo_url") val photoUrl: String? = null,
    @SerializedName("avatar_emoji") val avatarEmoji: String? = null,
    @SerializedName("is_active") val isActive: Boolean,
    @SerializedName("created_at") val createdAt: String
)

data class CreateBookmarkRequest(
    @SerializedName("name") val name: String,
    @SerializedName("phone_number") val phoneNumber: String,
    @SerializedName("contact_type") val contactType: String,
    @SerializedName("photo_url") val photoUrl: String? = null,
    @SerializedName("avatar_emoji") val avatarEmoji: String? = null
)

data class UpdateBookmarkRequest(
    @SerializedName("name") val name: String? = null,
    @SerializedName("phone_number") val phoneNumber: String? = null,
    @SerializedName("contact_type") val contactType: String? = null,
    @SerializedName("photo_url") val photoUrl: String? = null,
    @SerializedName("avatar_emoji") val avatarEmoji: String? = null,
    @SerializedName("is_active") val isActive: Boolean? = null
)

// ==================== Medicine Models ====================

enum class MedicineType {
    @SerializedName("tablet") TABLET,
    @SerializedName("injection") INJECTION,
    @SerializedName("insulin") INSULIN
}

data class Medicine(
    @SerializedName("id") val id: Int,
    @SerializedName("user_id") val userId: Int,
    @SerializedName("name") val name: String,
    @SerializedName("type") val type: MedicineType,
    @SerializedName("dosage") val dosage: String?,
    @SerializedName("instructions") val instructions: String?,
    @SerializedName("image_url") val imageUrl: String?,
    @SerializedName("is_active") val isActive: Boolean,
    @SerializedName("created_at") val createdAt: String,
    @SerializedName("updated_at") val updatedAt: String
)

data class CreateMedicineRequest(
    @SerializedName("user_id") val userId: Int,
    @SerializedName("name") val name: String,
    @SerializedName("type") val type: MedicineType,
    @SerializedName("dosage") val dosage: String? = null,
    @SerializedName("instructions") val instructions: String? = null,
    @SerializedName("image_url") val imageUrl: String? = null
)

data class UpdateMedicineRequest(
    @SerializedName("name") val name: String? = null,
    @SerializedName("dosage") val dosage: String? = null,
    @SerializedName("instructions") val instructions: String? = null,
    @SerializedName("image_url") val imageUrl: String? = null,
    @SerializedName("is_active") val isActive: Boolean? = null
)

// ==================== Reminder Models ====================

data class Reminder(
    @SerializedName("id") val id: Int,
    @SerializedName("medicine_id") val medicineId: Int,
    @SerializedName("scheduled_time") val scheduledTime: String, // HH:MM format
    @SerializedName("is_active") val isActive: Boolean,
    @SerializedName("created_at") val createdAt: String
)

data class CreateReminderRequest(
    @SerializedName("medicine_id") val medicineId: Int,
    @SerializedName("scheduled_time") val scheduledTime: String // HH:MM format
)

// ==================== Medicine Log Models ====================

enum class ReminderStatus {
    @SerializedName("pending") PENDING,
    @SerializedName("taken") TAKEN,
    @SerializedName("missed") MISSED,
    @SerializedName("snoozed") SNOOZED
}

data class MedicineLog(
    @SerializedName("id") val id: Int,
    @SerializedName("user_id") val userId: Int,
    @SerializedName("medicine_id") val medicineId: Int,
    @SerializedName("reminder_id") val reminderId: Int?,
    @SerializedName("status") val status: ReminderStatus,
    @SerializedName("scheduled_at") val scheduledAt: String,
    @SerializedName("taken_at") val takenAt: String?,
    @SerializedName("snooze_count") val snoozeCount: Int,
    @SerializedName("notes") val notes: String?,
    @SerializedName("created_at") val createdAt: String
)

data class CreateMedicineLogRequest(
    @SerializedName("user_id") val userId: Int,
    @SerializedName("medicine_id") val medicineId: Int,
    @SerializedName("reminder_id") val reminderId: Int? = null,
    @SerializedName("status") val status: ReminderStatus,
    @SerializedName("scheduled_at") val scheduledAt: String,
    @SerializedName("notes") val notes: String? = null
)

data class UpdateMedicineLogRequest(
    @SerializedName("status") val status: ReminderStatus? = null,
    @SerializedName("taken_at") val takenAt: String? = null,
    @SerializedName("snooze_count") val snoozeCount: Int? = null,
    @SerializedName("notes") val notes: String? = null
)

// ==================== Insulin Models ====================

data class InsulinLog(
    @SerializedName("id") val id: Int,
    @SerializedName("user_id") val userId: Int,
    @SerializedName("medicine_log_id") val medicineLogId: Int?,
    @SerializedName("glucose_reading") val glucoseReading: Float,
    @SerializedName("insulin_dosage") val insulinDosage: Float,
    @SerializedName("suggested_dosage") val suggestedDosage: Float?,
    @SerializedName("notes") val notes: String?,
    @SerializedName("recorded_at") val recordedAt: String,
    @SerializedName("created_at") val createdAt: String
)

data class CreateInsulinLogRequest(
    @SerializedName("user_id") val userId: Int,
    @SerializedName("medicine_log_id") val medicineLogId: Int? = null,
    @SerializedName("glucose_reading") val glucoseReading: Float,
    @SerializedName("insulin_dosage") val insulinDosage: Float,
    @SerializedName("suggested_dosage") val suggestedDosage: Float? = null,
    @SerializedName("notes") val notes: String? = null
)

data class InsulinDosageSuggestion(
    @SerializedName("glucose_reading") val glucoseReading: Float,
    @SerializedName("suggested_dosage") val suggestedDosage: Float,
    @SerializedName("unit") val unit: String,
    @SerializedName("note") val note: String
)

data class InsulinStats(
    @SerializedName("user_id") val userId: Int,
    @SerializedName("period") val period: String,
    @SerializedName("total_entries") val totalEntries: Int,
    @SerializedName("avg_glucose") val avgGlucose: Float,
    @SerializedName("avg_insulin") val avgInsulin: Float,
    @SerializedName("min_glucose") val minGlucose: Float,
    @SerializedName("max_glucose") val maxGlucose: Float,
    @SerializedName("logs") val logs: List<InsulinLog>
)

// ==================== Response Wrappers ====================

data class ApiResponse<T>(
    val data: T? = null,
    val message: String? = null,
    val isSuccess: Boolean = false
)

