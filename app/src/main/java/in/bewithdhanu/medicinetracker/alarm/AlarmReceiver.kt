package `in`.bewithdhanu.medicinetracker.alarm

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import androidx.core.app.NotificationCompat
import `in`.bewithdhanu.medicinetracker.MainActivity
import `in`.bewithdhanu.medicinetracker.R

/**
 * BroadcastReceiver for handling medicine reminder alarms
 */
class AlarmReceiver : BroadcastReceiver() {
    
    companion object {
        const val CHANNEL_ID = "medicine_reminders"
        const val NOTIFICATION_ID_BASE = 1000
        
        const val EXTRA_REMINDER_ID = "reminder_id"
        const val EXTRA_MEDICINE_NAME = "medicine_name"
        const val EXTRA_MEDICINE_TYPE = "medicine_type"
        const val EXTRA_USER_ID = "user_id"
        const val EXTRA_USER_NAME = "user_name"
        
        const val ACTION_TAKEN = "in.bewithdhanu.medicinetracker.ACTION_TAKEN"
        const val ACTION_SNOOZE = "in.bewithdhanu.medicinetracker.ACTION_SNOOZE"
    }
    
    override fun onReceive(context: Context, intent: Intent) {
        when (intent.action) {
            ACTION_TAKEN -> handleTaken(context, intent)
            ACTION_SNOOZE -> handleSnooze(context, intent)
            else -> showNotification(context, intent)
        }
    }
    
    private fun showNotification(context: Context, intent: Intent) {
        val reminderId = intent.getIntExtra(EXTRA_REMINDER_ID, 0)
        val medicineName = intent.getStringExtra(EXTRA_MEDICINE_NAME) ?: "Medicine"
        val medicineType = intent.getStringExtra(EXTRA_MEDICINE_TYPE) ?: "tablet"
        val userId = intent.getIntExtra(EXTRA_USER_ID, 0)
        val userName = intent.getStringExtra(EXTRA_USER_NAME) ?: "User"
        
        createNotificationChannel(context)
        
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        
        // Intent to open app
        val openAppIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val openAppPendingIntent = PendingIntent.getActivity(
            context,
            reminderId,
            openAppIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        
        // Intent for "Mark as Taken" action
        val takenIntent = Intent(context, AlarmReceiver::class.java).apply {
            action = ACTION_TAKEN
            putExtra(EXTRA_REMINDER_ID, reminderId)
            putExtra(EXTRA_MEDICINE_NAME, medicineName)
            putExtra(EXTRA_USER_ID, userId)
        }
        val takenPendingIntent = PendingIntent.getBroadcast(
            context,
            reminderId * 10 + 1,
            takenIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        
        // Intent for "Snooze" action
        val snoozeIntent = Intent(context, AlarmReceiver::class.java).apply {
            action = ACTION_SNOOZE
            putExtra(EXTRA_REMINDER_ID, reminderId)
            putExtra(EXTRA_MEDICINE_NAME, medicineName)
            putExtra(EXTRA_MEDICINE_TYPE, medicineType)
            putExtra(EXTRA_USER_ID, userId)
            putExtra(EXTRA_USER_NAME, userName)
        }
        val snoozePendingIntent = PendingIntent.getBroadcast(
            context,
            reminderId * 10 + 2,
            snoozeIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        
        // Build notification
        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle("⏰ Medicine Reminder")
            .setContentText("Time to take $medicineName for $userName")
            .setStyle(NotificationCompat.BigTextStyle()
                .bigText("Time to take $medicineName ($medicineType) for $userName"))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setAutoCancel(true)
            .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM))
            .setVibrate(longArrayOf(0, 500, 200, 500, 200, 500))
            .setContentIntent(openAppPendingIntent)
            .addAction(R.drawable.ic_check, "Mark as Taken", takenPendingIntent)
            .addAction(R.drawable.ic_snooze, "Snooze 30 min", snoozePendingIntent)
            .setFullScreenIntent(openAppPendingIntent, true) // Full screen for critical reminders
            .build()
        
        notificationManager.notify(NOTIFICATION_ID_BASE + reminderId, notification)
    }
    
    private fun handleTaken(context: Context, intent: Intent) {
        val reminderId = intent.getIntExtra(EXTRA_REMINDER_ID, 0)
        val medicineName = intent.getStringExtra(EXTRA_MEDICINE_NAME) ?: "Medicine"
        
        // Cancel notification
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancel(NOTIFICATION_ID_BASE + reminderId)
        
        // TODO: Update medicine log in backend via WorkManager
        // For now, just show a confirmation notification
        val confirmNotification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_check)
            .setContentTitle("✓ Marked as Taken")
            .setContentText("$medicineName marked as taken")
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setAutoCancel(true)
            .build()
        
        notificationManager.notify(NOTIFICATION_ID_BASE + reminderId + 5000, confirmNotification)
    }
    
    private fun handleSnooze(context: Context, intent: Intent) {
        val reminderId = intent.getIntExtra(EXTRA_REMINDER_ID, 0)
        val medicineName = intent.getStringExtra(EXTRA_MEDICINE_NAME) ?: "Medicine"
        val medicineType = intent.getStringExtra(EXTRA_MEDICINE_TYPE) ?: "tablet"
        val userId = intent.getIntExtra(EXTRA_USER_ID, 0)
        val userName = intent.getStringExtra(EXTRA_USER_NAME) ?: "User"
        
        // Cancel current notification
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancel(NOTIFICATION_ID_BASE + reminderId)
        
        // Schedule alarm for 30 minutes later
        val alarmScheduler = AlarmScheduler(context)
        alarmScheduler.scheduleSnoozeAlarm(
            reminderId = reminderId,
            medicineName = medicineName,
            medicineType = medicineType,
            userId = userId,
            userName = userName,
            snoozeMinutes = 30
        )
        
        // Show snooze confirmation
        val snoozeNotification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_snooze)
            .setContentTitle("⏰ Snoozed")
            .setContentText("$medicineName reminder snoozed for 30 minutes")
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setAutoCancel(true)
            .build()
        
        notificationManager.notify(NOTIFICATION_ID_BASE + reminderId + 6000, snoozeNotification)
    }
    
    private fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Medicine Reminders"
            val descriptionText = "Notifications for medicine reminders"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
                enableVibration(true)
                vibrationPattern = longArrayOf(0, 500, 200, 500, 200, 500)
                setSound(
                    RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM),
                    null
                )
            }
            
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
}

