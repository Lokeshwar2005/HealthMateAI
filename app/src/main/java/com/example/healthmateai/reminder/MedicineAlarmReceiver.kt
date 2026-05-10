package com.example.healthmateai.reminder

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log

class MedicineAlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val reminderId = intent.getLongExtra("reminderId", -1L)
        val medicineName = intent.getStringExtra("medicineName") ?: "Medicine"
        val dosage = intent.getStringExtra("dosage") ?: ""
        
        Log.d("MedicineAlarmReceiver", "Alarm triggered for $medicineName (id: $reminderId)")

        if (reminderId == -1L) return

        val serviceIntent = Intent(context, MedicineRingingService::class.java).apply {
            putExtra("reminderId", reminderId)
            putExtra("medicineName", medicineName)
            putExtra("dosage", dosage)
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(serviceIntent)
        } else {
            context.startService(serviceIntent)
        }
    }
}
