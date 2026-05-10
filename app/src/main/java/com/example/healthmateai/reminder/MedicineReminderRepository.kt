package com.example.healthmateai.reminder

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import kotlinx.coroutines.flow.Flow
import java.util.Calendar
import android.util.Log

class MedicineReminderRepository(private val context: Context) {

    private val appDatabase = AppDatabase.getInstance(context)
    private val dao = appDatabase.medicineReminderDao()
    private val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    fun observeAll(): Flow<List<MedicineReminderEntity>> = dao.observeAll()

    fun observeUpcoming(): Flow<MedicineReminderEntity?> = dao.observeUpcoming()

    suspend fun addReminder(
        medicineName: String,
        dosage: String,
        hourOfDay: Int,
        minute: Int,
        frequency: ReminderFrequency,
        customIntervalHours: Int?
    ) {
        val now = System.currentTimeMillis()
        val nextTrigger = computeNextTrigger(now, hourOfDay, minute)
        val reminder = MedicineReminderEntity(
            medicineName = medicineName,
            dosage = dosage,
            hourOfDay = hourOfDay,
            minute = minute,
            frequency = frequency,
            customIntervalHours = customIntervalHours,
            nextTriggerAtMillis = nextTrigger,
            isEnabled = true
        )

        val id = dao.upsert(reminder)
        scheduleAlarm(
            reminderId = id,
            medicineName = medicineName,
            dosage = dosage,
            nextTriggerAtMillis = nextTrigger
        )
    }

    suspend fun updateReminder(
        id: Long,
        medicineName: String,
        dosage: String,
        hourOfDay: Int,
        minute: Int,
        frequency: ReminderFrequency,
        customIntervalHours: Int?
    ) {
        val now = System.currentTimeMillis()
        val nextTrigger = computeNextTrigger(now, hourOfDay, minute)
        val updated = MedicineReminderEntity(
            id = id,
            medicineName = medicineName,
            dosage = dosage,
            hourOfDay = hourOfDay,
            minute = minute,
            frequency = frequency,
            customIntervalHours = customIntervalHours,
            nextTriggerAtMillis = nextTrigger,
            isEnabled = true
        )
        dao.update(updated)
        cancelAlarm(id)
        scheduleAlarm(
            reminderId = id,
            medicineName = medicineName,
            dosage = dosage,
            nextTriggerAtMillis = nextTrigger
        )
    }

    suspend fun deleteReminder(id: Long) {
        dao.deleteById(id)
        cancelAlarm(id)
    }

    suspend fun toggleReminder(id: Long, enabled: Boolean) {
        dao.setEnabled(id, enabled)
        if (enabled) {
            val reminder = dao.getById(id) ?: return
            
            // Recompute next trigger if it's in the past
            val now = System.currentTimeMillis()
            var nextTrigger = reminder.nextTriggerAtMillis
            if (nextTrigger <= now) {
                nextTrigger = computeNextTrigger(now, reminder.hourOfDay, reminder.minute)
                dao.update(reminder.copy(nextTriggerAtMillis = nextTrigger))
            }
            
            scheduleAlarm(
                reminderId = reminder.id,
                medicineName = reminder.medicineName,
                dosage = reminder.dosage,
                nextTriggerAtMillis = nextTrigger
            )
        } else {
            cancelAlarm(id)
        }
    }

    private fun cancelAlarm(reminderId: Long) {
        val intent = Intent(context, MedicineAlarmReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            reminderId.toInt(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        alarmManager.cancel(pendingIntent)
        Log.d("ReminderRepo", "Cancelled alarm for id: ${reminderId}")
    }

    fun scheduleAlarm(
        reminderId: Long,
        medicineName: String,
        dosage: String,
        nextTriggerAtMillis: Long
    ) {
        val intent = Intent(context, MedicineAlarmReceiver::class.java).apply {
            putExtra("reminderId", reminderId)
            putExtra("medicineName", medicineName)
            putExtra("dosage", dosage)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            reminderId.toInt(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        try {
            val alarmClockInfo = AlarmManager.AlarmClockInfo(nextTriggerAtMillis, pendingIntent)
            alarmManager.setAlarmClock(alarmClockInfo, pendingIntent)
            Log.d("ReminderRepo", "Scheduled exact alarm for id: ${reminderId} at $nextTriggerAtMillis")
        } catch (e: SecurityException) {
            Log.e("ReminderRepo", "Exact alarm permission missing", e)
            // Fallback
            alarmManager.set(AlarmManager.RTC_WAKEUP, nextTriggerAtMillis, pendingIntent)
        }
    }

    private fun computeNextTrigger(nowMillis: Long, hour: Int, minute: Int): Long {
        val calendar = Calendar.getInstance().apply {
            timeInMillis = nowMillis
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        if (calendar.timeInMillis <= nowMillis) {
            calendar.add(Calendar.DAY_OF_YEAR, 1)
        }
        return calendar.timeInMillis
    }
}
