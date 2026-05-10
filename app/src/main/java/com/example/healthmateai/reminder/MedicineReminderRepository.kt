package com.example.healthmateai.reminder

import android.content.Context
import androidx.work.Constraints
import androidx.work.Data
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import kotlinx.coroutines.flow.Flow
import java.util.Calendar
import java.util.concurrent.TimeUnit

class MedicineReminderRepository(context: Context) {

    private val appDatabase = AppDatabase.getInstance(context)
    private val dao = appDatabase.medicineReminderDao()
    private val workManager = WorkManager.getInstance(context)

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
            nextTriggerAtMillis = nextTrigger
        )

        val id = dao.upsert(reminder)
        scheduleReminderWork(
            reminderId = id,
            medicineName = medicineName,
            dosage = dosage,
            hourOfDay = hourOfDay,
            minute = minute,
            frequency = frequency,
            customIntervalHours = customIntervalHours,
            nextTriggerAtMillis = nextTrigger
        )
    }

    private fun scheduleReminderWork(
        reminderId: Long,
        medicineName: String,
        dosage: String,
        hourOfDay: Int,
        minute: Int,
        frequency: ReminderFrequency,
        customIntervalHours: Int?,
        nextTriggerAtMillis: Long
    ) {
        val repeatInterval = when (frequency) {
            ReminderFrequency.DAILY -> 1L to TimeUnit.DAYS
            ReminderFrequency.WEEKLY -> 7L to TimeUnit.DAYS
            ReminderFrequency.CUSTOM -> (customIntervalHours ?: 24).toLong() to TimeUnit.HOURS
        }

        val now = System.currentTimeMillis()
        val initialDelay = (nextTriggerAtMillis - now).coerceAtLeast(0L)
        val timeLabel = String.format("%02d:%02d", hourOfDay, minute)

        val request = PeriodicWorkRequestBuilder<MedicineReminderWorker>(
            repeatInterval.first,
            repeatInterval.second
        )
            .setInitialDelay(initialDelay, TimeUnit.MILLISECONDS)
            .setConstraints(
                Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.NOT_REQUIRED)
                    .build()
            )
            .setInputData(
                Data.Builder()
                    .putString("medicineName", medicineName)
                    .putString("dosage", dosage)
                    .putString("timeLabel", timeLabel)
                    .build()
            )
            .build()

        workManager.enqueueUniquePeriodicWork(
            "medicine_reminder_$reminderId",
            ExistingPeriodicWorkPolicy.UPDATE,
            request
        )
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
