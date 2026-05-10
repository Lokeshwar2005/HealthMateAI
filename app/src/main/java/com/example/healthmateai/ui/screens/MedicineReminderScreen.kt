package com.example.healthmateai.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.healthmateai.reminder.MedicineReminderViewModel
import com.example.healthmateai.reminder.MedicineReminderViewModelFactory
import com.example.healthmateai.reminder.ReminderFrequency
import com.example.healthmateai.ui.theme.BgDark

@Composable
fun MedicineReminderScreen(contentPadding: PaddingValues) {
    val context = LocalContext.current
    val viewModel: MedicineReminderViewModel = viewModel(
        factory = MedicineReminderViewModelFactory(context)
    )

    val uiState by viewModel.uiState.collectAsState()
    val reminders by viewModel.reminders.collectAsState()
    val upcoming by viewModel.upcomingReminder.collectAsState()

    var medicineName by remember { mutableStateOf("") }
    var dosage by remember { mutableStateOf("") }
    var hourText by remember { mutableStateOf("20") }
    var minuteText by remember { mutableStateOf("00") }
    var selectedFrequency by remember { mutableStateOf(ReminderFrequency.DAILY) }
    var customHours by remember { mutableIntStateOf(12) }

    LaunchedEffect(uiState.successMessage, uiState.saveError) {
        if (uiState.successMessage != null || uiState.saveError != null) {
            viewModel.clearTransientState()
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(BgDark)
            .padding(contentPadding),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(16.dp)
    ) {
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF14284A))
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = "Medicine Reminder",
                        style = MaterialTheme.typography.headlineSmall,
                        color = Color(0xFFF3F8FF)
                    )
                    Text(
                        text = upcoming,
                        color = Color(0xFF6CE9FF),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )

                    OutlinedTextField(
                        value = medicineName,
                        onValueChange = { medicineName = it },
                        label = { Text("Medicine Name") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = dosage,
                        onValueChange = { dosage = it },
                        label = { Text("Dosage") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
                        OutlinedTextField(
                            value = hourText,
                            onValueChange = { hourText = it.filter(Char::isDigit).take(2) },
                            label = { Text("Hour") },
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = minuteText,
                            onValueChange = { minuteText = it.filter(Char::isDigit).take(2) },
                            label = { Text("Minute") },
                            modifier = Modifier.weight(1f)
                        )
                    }

                    SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
                        ReminderFrequency.entries.forEachIndexed { index, frequency ->
                            SegmentedButton(
                                shape = SegmentedButtonDefaults.itemShape(index, ReminderFrequency.entries.size),
                                selected = selectedFrequency == frequency,
                                onClick = { selectedFrequency = frequency },
                                label = { Text(frequency.name.lowercase().replaceFirstChar { it.uppercase() }) }
                            )
                        }
                    }

                    if (selectedFrequency == ReminderFrequency.CUSTOM) {
                        OutlinedTextField(
                            value = customHours.toString(),
                            onValueChange = {
                                customHours = it.filter(Char::isDigit).toIntOrNull()?.coerceIn(1, 24 * 7) ?: customHours
                            },
                            label = { Text("Repeat every N hours") },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    Button(
                        onClick = {
                            val hour = hourText.toIntOrNull()?.coerceIn(0, 23) ?: 20
                            val minute = minuteText.toIntOrNull()?.coerceIn(0, 59) ?: 0
                            viewModel.addReminder(
                                medicineName = medicineName,
                                dosage = dosage,
                                hourOfDay = hour,
                                minute = minute,
                                frequency = selectedFrequency,
                                customIntervalHours = if (selectedFrequency == ReminderFrequency.CUSTOM) customHours else null
                            )
                            medicineName = ""
                            dosage = ""
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(if (uiState.isSaving) "Saving..." else "Save Reminder")
                    }

                    uiState.saveError?.let { error ->
                        Text(error, color = Color(0xFFFFB8C4))
                    }
                }
            }
        }

        item {
            Text("Upcoming Schedule", color = Color(0xFFCEDFFC), style = MaterialTheme.typography.titleMedium)
        }

        items(reminders, key = { it.id }) { reminder ->
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF182947))
            ) {
                Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text("${reminder.medicineName} - ${reminder.dosage}", color = Color(0xFFF2F7FF), fontWeight = FontWeight.SemiBold)
                    Text(
                        "${String.format("%02d:%02d", reminder.hourOfDay, reminder.minute)} | ${reminder.frequency.name}",
                        color = Color(0xFF9FBBE7)
                    )
                }
            }
        }
    }
}
