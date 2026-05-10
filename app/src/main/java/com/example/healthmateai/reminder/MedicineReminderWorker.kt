package com.example.healthmateai.reminder

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
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

        ensureChannel()

        val notification = NotificationCompat.Builder(applicationContext, CHANNEL_ID)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle("Medicine Reminder")
            .setContentText("$medicineName ($dosage) at $triggerLabel")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()

        NotificationManagerCompat.from(applicationContext)
            .notify((System.currentTimeMillis() % Int.MAX_VALUE).toInt(), notification)

        return Result.success()
    }

    private fun ensureChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Medicine Reminders",
                NotificationManager.IMPORTANCE_HIGH
            )
            val manager = applicationContext.getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }
    }
}
