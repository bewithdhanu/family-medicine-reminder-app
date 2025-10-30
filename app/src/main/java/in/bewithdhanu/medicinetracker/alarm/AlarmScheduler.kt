package `in`.bewithdhanu.medicinetracker.alarm

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import java.util.*

/**
 * Manages scheduling and canceling of medicine reminder alarms
 */
class AlarmScheduler(private val context: Context) {
    
    private val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    
    /**
     * Schedule a daily reminder at the specified time
     * @param reminderId Unique ID for the reminder
     * @param medicineName Name of the medicine
     * @param medicineType Type (tablet/injection/insulin)
     * @param userId User ID who needs to take the medicine
     * @param userName User's name
     * @param hour Hour in 24-hour format (0-23)
     * @param minute Minute (0-59)
     */
    fun scheduleDailyAlarm(
        reminderId: Int,
        medicineName: String,
        medicineType: String,
        userId: Int,
        userName: String,
        hour: Int,
        minute: Int
    ) {
        val intent = createAlarmIntent(reminderId, medicineName, medicineType, userId, userName)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            reminderId,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        
        // Calculate the alarm time
        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
            
            // If the time has already passed today, schedule for tomorrow
            if (timeInMillis <= System.currentTimeMillis()) {
                add(Calendar.DAY_OF_YEAR, 1)
            }
        }
        
        // Schedule repeating alarm
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                calendar.timeInMillis,
                pendingIntent
            )
        } else {
            alarmManager.setExact(
                AlarmManager.RTC_WAKEUP,
                calendar.timeInMillis,
                pendingIntent
            )
        }
    }
    
    /**
     * Schedule a snooze alarm (one-time, after X minutes)
     */
    fun scheduleSnoozeAlarm(
        reminderId: Int,
        medicineName: String,
        medicineType: String,
        userId: Int,
        userName: String,
        snoozeMinutes: Int = 30
    ) {
        val intent = createAlarmIntent(reminderId, medicineName, medicineType, userId, userName)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            reminderId + 10000, // Different ID for snooze
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        
        val triggerTime = System.currentTimeMillis() + (snoozeMinutes * 60 * 1000)
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                triggerTime,
                pendingIntent
            )
        } else {
            alarmManager.setExact(
                AlarmManager.RTC_WAKEUP,
                triggerTime,
                pendingIntent
            )
        }
    }
    
    /**
     * Cancel a scheduled alarm
     */
    fun cancelAlarm(reminderId: Int) {
        val intent = Intent(context, AlarmReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            reminderId,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        
        alarmManager.cancel(pendingIntent)
        pendingIntent.cancel()
    }
    
    /**
     * Cancel all alarms (useful for testing or when disabling all reminders)
     */
    fun cancelAllAlarms(reminderIds: List<Int>) {
        reminderIds.forEach { reminderId ->
            cancelAlarm(reminderId)
        }
    }
    
    private fun createAlarmIntent(
        reminderId: Int,
        medicineName: String,
        medicineType: String,
        userId: Int,
        userName: String
    ): Intent {
        return Intent(context, AlarmReceiver::class.java).apply {
            putExtra(AlarmReceiver.EXTRA_REMINDER_ID, reminderId)
            putExtra(AlarmReceiver.EXTRA_MEDICINE_NAME, medicineName)
            putExtra(AlarmReceiver.EXTRA_MEDICINE_TYPE, medicineType)
            putExtra(AlarmReceiver.EXTRA_USER_ID, userId)
            putExtra(AlarmReceiver.EXTRA_USER_NAME, userName)
        }
    }
}

