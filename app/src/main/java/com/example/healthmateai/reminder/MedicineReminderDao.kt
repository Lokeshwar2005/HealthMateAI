package com.example.healthmateai.reminder

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface MedicineReminderDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(reminder: MedicineReminderEntity): Long

    @Update
    suspend fun update(reminder: MedicineReminderEntity)

    @Query("SELECT * FROM medicine_reminders ORDER BY nextTriggerAtMillis ASC")
    fun observeAll(): Flow<List<MedicineReminderEntity>>

    @Query("SELECT * FROM medicine_reminders WHERE isEnabled = 1 ORDER BY nextTriggerAtMillis ASC LIMIT 1")
    fun observeUpcoming(): Flow<MedicineReminderEntity?>

    @Query("SELECT * FROM medicine_reminders WHERE id = :id")
    suspend fun getById(id: Long): MedicineReminderEntity?

    @Query("DELETE FROM medicine_reminders WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("UPDATE medicine_reminders SET isEnabled = :enabled WHERE id = :id")
    suspend fun setEnabled(id: Long, enabled: Boolean)
}
