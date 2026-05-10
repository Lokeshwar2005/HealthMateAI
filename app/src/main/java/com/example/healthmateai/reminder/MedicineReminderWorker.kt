package com.example.healthmateai.reminder

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.healthmateai.MainActivity
import com.example.healthmateai.R

private const val CHANNEL_ID = "medicine_reminders"

class MedicineReminderWorker(
    appContext: Context,
    params: WorkerParameters
) : CoroutineWorker(appContext, params) {

    override suspend fun doWork(): Result {
        val medicineName = inputData.getString("medicineName").orEmpty()
        val dosage = inputData.getString("dosage").orEmpty()
        val triggerLabel = inputData.getString("timeLabel").orEmpty()
        val reminderId = inputData.getLong("reminderId", 0L)

        ensureChannel()

        val tapIntent = Intent(applicationContext, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra("navigateTo", "medicineReminder")
        }
        val pendingIntent = PendingIntent.getActivity(
            applicationContext,
            reminderId.toInt(),
            tapIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(applicationContext, CHANNEL_ID)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle("💊 Medicine Reminder")
            .setContentText("$medicineName ($dosage)")
            .setSubText("Scheduled at $triggerLabel")
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText("Time to take $medicineName\nDosage: $dosage\nScheduled: $triggerLabel")
                    .setBigContentTitle("💊 Medicine Reminder")
            )
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_REMINDER)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            .build()

        try {
            NotificationManagerCompat.from(applicationContext)
                .notify(reminderId.toInt(), notification)
        } catch (e: SecurityException) {
            // POST_NOTIFICATIONS permission not granted
            android.util.Log.w("MedicineReminder", "Notification permission not granted", e)
        }

        return Result.success()
    }

    private fun ensureChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Medicine Reminders",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Alerts for scheduled medicine reminders"
                enableVibration(true)
                enableLights(true)
            }
            val manager = applicationContext.getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }
    }
}
