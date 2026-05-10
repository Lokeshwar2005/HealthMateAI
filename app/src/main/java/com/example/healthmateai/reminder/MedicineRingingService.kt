package com.example.healthmateai.reminder

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.media.RingtoneManager
import android.os.Build
import android.os.IBinder
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.healthmateai.R

class MedicineRingingService : Service() {

    private var mediaPlayer: MediaPlayer? = null
    private var vibrator: Vibrator? = null

    companion object {
        const val ACTION_DISMISS = "com.example.healthmateai.ACTION_DISMISS"
        private const val CHANNEL_ID = "medicine_alarm_channel"
        private const val NOTIFICATION_ID = 1001
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent?.action == ACTION_DISMISS) {
            val reminderId = intent.getLongExtra("reminderId", -1L)
            if (reminderId != -1L) {
                scheduleNextReminder(reminderId)
            }
            stopAlarm()
            stopSelf()
            return START_NOT_STICKY
        }

        val reminderId = intent?.getLongExtra("reminderId", -1L) ?: -1L
        val medicineName = intent?.getStringExtra("medicineName") ?: "Medicine"
        val dosage = intent?.getStringExtra("dosage") ?: ""

        createNotificationChannel()

        val fullScreenIntent = Intent(this, MedicineRingingActivity::class.java).apply {
            putExtra("reminderId", reminderId)
            putExtra("medicineName", medicineName)
            putExtra("dosage", dosage)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        }

        val fullScreenPendingIntent = PendingIntent.getActivity(
            this,
            reminderId.toInt(),
            fullScreenIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val dismissIntent = Intent(this, MedicineRingingService::class.java).apply {
            action = ACTION_DISMISS
            putExtra("reminderId", reminderId)
        }
        val dismissPendingIntent = PendingIntent.getService(
            this,
            reminderId.toInt(),
            dismissIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle("Time to take $medicineName")
            .setContentText("Dosage: $dosage")
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setFullScreenIntent(fullScreenPendingIntent, true)
            .addAction(0, "Dismiss", dismissPendingIntent)
            .setOngoing(true)
            .build()

        if (Build.VERSION.SDK_INT >= 34) {
            startForeground(NOTIFICATION_ID, notification, android.content.pm.ServiceInfo.FOREGROUND_SERVICE_TYPE_SHORT_SERVICE)
        } else {
            startForeground(NOTIFICATION_ID, notification)
        }
        playAlarmSoundAndVibrate()

        return START_STICKY
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Medicine Alarms",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "High priority alarms for medicine"
                setBypassDnd(true)
            }
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }
    }

    private fun playAlarmSoundAndVibrate() {
        try {
            val alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
                ?: RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE)
            
            mediaPlayer = MediaPlayer().apply {
                setDataSource(this@MedicineRingingService, alarmUri)
                setAudioAttributes(
                    AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_ALARM)
                        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                        .build()
                )
                isLooping = true
                prepare()
                start()
            }
        } catch (e: Exception) {
            Log.e("MedicineRingingService", "Failed to play alarm", e)
        }

        vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vibratorManager = getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
            vibratorManager.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        }

        val pattern = longArrayOf(0, 1000, 1000)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator?.vibrate(VibrationEffect.createWaveform(pattern, 0))
        } else {
            @Suppress("DEPRECATION")
            vibrator?.vibrate(pattern, 0)
        }
    }

    private fun stopAlarm() {
        mediaPlayer?.stop()
        mediaPlayer?.release()
        mediaPlayer = null
        vibrator?.cancel()
        vibrator = null
    }

    private fun scheduleNextReminder(reminderId: Long) {
        // Run in background thread to update DB and set next alarm
        Thread {
            val appDb = AppDatabase.getInstance(this)
            val dao = appDb.medicineReminderDao()
            val repo = MedicineReminderRepository(this)
            
            val reminder = kotlinx.coroutines.runBlocking { dao.getById(reminderId) }
            if (reminder != null && reminder.isEnabled) {
                // Calculate next occurrence
                val calendar = java.util.Calendar.getInstance()
                val now = calendar.timeInMillis
                
                calendar.set(java.util.Calendar.HOUR_OF_DAY, reminder.hourOfDay)
                calendar.set(java.util.Calendar.MINUTE, reminder.minute)
                calendar.set(java.util.Calendar.SECOND, 0)
                calendar.set(java.util.Calendar.MILLISECOND, 0)
                
                when (reminder.frequency) {
                    ReminderFrequency.DAILY -> {
                        calendar.add(java.util.Calendar.DAY_OF_YEAR, 1)
                    }
                    ReminderFrequency.WEEKLY -> {
                        calendar.add(java.util.Calendar.WEEK_OF_YEAR, 1)
                    }
                    ReminderFrequency.CUSTOM -> {
                        val hours = reminder.customIntervalHours ?: 24
                        calendar.timeInMillis = now + (hours * 60 * 60 * 1000L)
                    }
                }
                
                val nextTrigger = calendar.timeInMillis
                kotlinx.coroutines.runBlocking {
                    dao.update(reminder.copy(nextTriggerAtMillis = nextTrigger))
                }
                
                repo.scheduleAlarm(
                    reminderId = reminder.id,
                    medicineName = reminder.medicineName,
                    dosage = reminder.dosage,
                    nextTriggerAtMillis = nextTrigger
                )
            }
        }.start()
    }

    override fun onDestroy() {
        super.onDestroy()
        stopAlarm()
    }
}
