package com.example.healthmateai.reminder

import android.app.KeyguardManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Medication
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.healthmateai.ui.theme.AccentCyan
import com.example.healthmateai.ui.theme.BgDark
import com.example.healthmateai.ui.theme.HealthMateAITheme

class MedicineRingingActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Wake up device and show above keyguard
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            setShowWhenLocked(true)
            setTurnScreenOn(true)
            val keyguardManager = getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager
            keyguardManager.requestDismissKeyguard(this, null)
        } else {
            @Suppress("DEPRECATION")
            window.addFlags(
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON or
                WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD or
                WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED or
                WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
            )
        }

        val reminderId = intent.getLongExtra("reminderId", -1L)
        val medicineName = intent.getStringExtra("medicineName") ?: "Medicine"
        val dosage = intent.getStringExtra("dosage") ?: ""

        setContent {
            HealthMateAITheme {
                RingingScreen(
                    medicineName = medicineName,
                    dosage = dosage,
                    onDismiss = {
                        dismissAlarm(reminderId)
                    }
                )
            }
        }
    }

    private fun dismissAlarm(reminderId: Long) {
        val dismissIntent = Intent(this, MedicineRingingService::class.java).apply {
            action = MedicineRingingService.ACTION_DISMISS
            putExtra("reminderId", reminderId)
        }
        startService(dismissIntent)
        finish()
    }
}

@Composable
fun RingingScreen(medicineName: String, dosage: String, onDismiss: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BgDark),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(32.dp),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF16254A))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = AccentCyan.copy(alpha = 0.2f)
                ) {
                    Icon(
                        Icons.Default.Medication,
                        contentDescription = null,
                        tint = AccentCyan,
                        modifier = Modifier
                            .padding(24.dp)
                            .size(64.dp)
                    )
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "Time for Medicine",
                        color = Color(0xFF9BAED7),
                        fontSize = 16.sp
                    )
                    Text(
                        text = medicineName,
                        color = Color.White,
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Dosage: $dosage",
                        color = AccentCyan,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Medium
                    )
                }

                Button(
                    onClick = onDismiss,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = AccentCyan,
                        contentColor = BgDark
                    ),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text("Dismiss", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
