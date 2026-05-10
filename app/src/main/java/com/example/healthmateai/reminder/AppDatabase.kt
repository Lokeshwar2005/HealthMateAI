package com.example.healthmateai.reminder

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters

@Database(
    entities = [MedicineReminderEntity::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(ReminderTypeConverters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun medicineReminderDao(): MedicineReminderDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "healthmate.db"
                ).build().also { INSTANCE = it }
            }
        }
    }
}

class ReminderTypeConverters {
    @TypeConverter
    fun fromFrequency(value: ReminderFrequency): String = value.name

    @TypeConverter
    fun toFrequency(value: String): ReminderFrequency = ReminderFrequency.valueOf(value)
}
