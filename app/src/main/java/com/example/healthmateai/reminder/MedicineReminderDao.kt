package com.example.healthmateai.reminder

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface MedicineReminderDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(reminder: MedicineReminderEntity): Long

    @Query("SELECT * FROM medicine_reminders ORDER BY nextTriggerAtMillis ASC")
    fun observeAll(): Flow<List<MedicineReminderEntity>>

    @Query("SELECT * FROM medicine_reminders ORDER BY nextTriggerAtMillis ASC LIMIT 1")
    fun observeUpcoming(): Flow<MedicineReminderEntity?>
}
