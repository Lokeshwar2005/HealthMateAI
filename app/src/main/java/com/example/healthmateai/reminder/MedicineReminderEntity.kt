package com.example.healthmateai.reminder

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "medicine_reminders")
data class MedicineReminderEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val medicineName: String,
    val dosage: String,
    val hourOfDay: Int,
    val minute: Int,
    val frequency: ReminderFrequency,
    val customIntervalHours: Int?,
    val nextTriggerAtMillis: Long
)
