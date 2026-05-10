package com.example.healthmateai.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Medication
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.NotificationsOff
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TimePickerDefaults
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.healthmateai.reminder.MedicineReminderEntity
import com.example.healthmateai.reminder.MedicineReminderViewModel
import com.example.healthmateai.reminder.MedicineReminderViewModelFactory
import com.example.healthmateai.reminder.ReminderFrequency
import com.example.healthmateai.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MedicineReminderScreen(contentPadding: PaddingValues) {
    val context = LocalContext.current
    val viewModel: MedicineReminderViewModel = viewModel(
        factory = MedicineReminderViewModelFactory(context)
    )

    val uiState by viewModel.uiState.collectAsState()
    val reminders by viewModel.reminders.collectAsState()
    val upcoming by viewModel.upcomingReminder.collectAsState()

    var showAddDialog by remember { mutableStateOf(false) }
    var editReminder by remember { mutableStateOf<MedicineReminderEntity?>(null) }
    var deleteTarget by remember { mutableStateOf<MedicineReminderEntity?>(null) }

    LaunchedEffect(uiState.successMessage, uiState.saveError) {
        if (uiState.successMessage != null || uiState.saveError != null) {
            kotlinx.coroutines.delay(1500)
            viewModel.clearTransientState()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BgDark)
            .padding(contentPadding)
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(14.dp),
            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 20.dp)
        ) {
            // Hero Header
            item {
                ReminderHeroCard(upcoming = upcoming)
            }

            // Status message
            item {
                AnimatedVisibility(
                    visible = uiState.successMessage != null || uiState.saveError != null,
                    enter = slideInVertically() + fadeIn(),
                    exit = shrinkVertically() + fadeOut()
                ) {
                    val isError = uiState.saveError != null
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        color = if (isError) Color(0xFF3B1D24) else Color(0xFF1D3B2A),
                        border = BorderStroke(1.dp, if (isError) Color(0xFF5B2D34) else Color(0xFF2D5B3A))
                    ) {
                        Text(
                            text = uiState.saveError ?: uiState.successMessage ?: "",
                            color = if (isError) Color(0xFFFF8C8C) else Color(0xFF8CFFB8),
                            fontSize = 13.sp,
                            modifier = Modifier.padding(12.dp)
                        )
                    }
                }
            }

            // Section header
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            "Your Reminders",
                            style = MaterialTheme.typography.titleLarge,
                            color = Color(0xFFF2F6FF),
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            "${reminders.size} active reminder${if (reminders.size != 1) "s" else ""}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color(0xFF9BAED7)
                        )
                    }
                }
            }

            if (reminders.isEmpty()) {
                item {
                    EmptyRemindersState()
                }
            } else {
                items(reminders, key = { it.id }) { reminder ->
                    ReminderCard(
                        reminder = reminder,
                        onToggle = { viewModel.toggleReminder(reminder.id, it) },
                        onEdit = { editReminder = reminder },
                        onDelete = { deleteTarget = reminder }
                    )
                }
            }

            item { Spacer(modifier = Modifier.height(80.dp)) }
        }

        // FAB
        FloatingActionButton(
            onClick = { showAddDialog = true },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(20.dp),
            shape = RoundedCornerShape(18.dp),
            containerColor = AccentCyan,
            contentColor = BgDark
        ) {
            Icon(Icons.Default.Add, contentDescription = "Add Reminder")
        }
    }

    // Add Dialog
    if (showAddDialog) {
        ReminderFormDialog(
            title = "New Reminder",
            onDismiss = { showAddDialog = false },
            onSave = { name, dosage, hour, minute, frequency, customHours ->
                viewModel.addReminder(name, dosage, hour, minute, frequency, customHours)
                showAddDialog = false
            }
        )
    }

    // Edit Dialog
    editReminder?.let { reminder ->
        ReminderFormDialog(
            title = "Edit Reminder",
            initialName = reminder.medicineName,
            initialDosage = reminder.dosage,
            initialHour = reminder.hourOfDay,
            initialMinute = reminder.minute,
            initialFrequency = reminder.frequency,
            initialCustomHours = reminder.customIntervalHours ?: 12,
            onDismiss = { editReminder = null },
            onSave = { name, dosage, hour, minute, frequency, customHours ->
                viewModel.updateReminder(reminder.id, name, dosage, hour, minute, frequency, customHours)
                editReminder = null
            }
        )
    }

    // Delete Confirmation
    deleteTarget?.let { reminder ->
        AlertDialog(
            onDismissRequest = { deleteTarget = null },
            containerColor = SurfaceCard,
            titleContentColor = TextPrimary,
            textContentColor = TextSecondary,
            title = {
                Text("Delete Reminder?", fontWeight = FontWeight.Bold)
            },
            text = {
                Text("\"${reminder.medicineName}\" reminder will be permanently removed and notifications cancelled.")
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.deleteReminder(reminder.id)
                        deleteTarget = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ErrorRed)
                ) {
                    Text("Delete", color = Color.White)
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { deleteTarget = null }) {
                    Text("Cancel", color = TextPrimary)
                }
            }
        )
    }
}

@Composable
private fun ReminderHeroCard(upcoming: String) {
    val shimmer = rememberInfiniteTransition(label = "reminderShimmer")
    val glowAlpha by shimmer.animateFloat(
        initialValue = 0.06f,
        targetValue = 0.16f,
        animationSpec = infiniteRepeatable(animation = tween(2000), repeatMode = RepeatMode.Reverse),
        label = "reminderGlow"
    )

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        elevation = CardDefaults.cardElevation(defaultElevation = 10.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.linearGradient(
                        listOf(Color(0xFF0F2040), Color(0xFF162A4D), Color(0xFF0E1D38))
                    )
                )
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = AccentCyan.copy(alpha = 0.15f),
                    border = BorderStroke(1.dp, AccentCyan.copy(alpha = 0.4f))
                ) {
                    Icon(
                        Icons.Default.Medication,
                        contentDescription = null,
                        tint = AccentCyan,
                        modifier = Modifier.padding(10.dp)
                    )
                }
                Column {
                    Text(
                        "Medicine Reminders",
                        style = MaterialTheme.typography.headlineSmall,
                        color = Color(0xFFF3F8FF),
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        "Never miss a dose",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color(0xFF9BAED7)
                    )
                }
            }

            Surface(
                shape = RoundedCornerShape(12.dp),
                color = AccentCyan.copy(alpha = glowAlpha),
                border = BorderStroke(1.dp, AccentCyan.copy(alpha = 0.3f))
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        Icons.Default.Schedule,
                        contentDescription = null,
                        tint = AccentCyanBright,
                        modifier = Modifier.size(18.dp)
                    )
                    Text(
                        text = upcoming,
                        color = AccentCyanBright,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}

@Composable
private fun EmptyRemindersState() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF141E36))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = AccentCyan.copy(alpha = 0.1f)
            ) {
                Icon(
                    Icons.Default.NotificationsOff,
                    contentDescription = null,
                    tint = Color(0xFF5B7A9D),
                    modifier = Modifier
                        .padding(16.dp)
                        .size(40.dp)
                )
            }
            Text(
                "No reminders yet",
                style = MaterialTheme.typography.titleMedium,
                color = Color(0xFFC0D0EA),
                fontWeight = FontWeight.SemiBold
            )
            Text(
                "Tap + to add your first medicine reminder",
                style = MaterialTheme.typography.bodyMedium,
                color = Color(0xFF7B8DAF)
            )
        }
    }
}

@Composable
private fun ReminderCard(
    reminder: MedicineReminderEntity,
    onToggle: (Boolean) -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    val enabled = reminder.isEnabled
    val cardAlpha by animateFloatAsState(
        targetValue = if (enabled) 1f else 0.6f,
        animationSpec = tween(300),
        label = "reminderCardAlpha"
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .graphicsLayer { alpha = cardAlpha },
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (enabled) Color(0xFF162845) else Color(0xFF121C30)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = if (enabled) 6.dp else 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = if (enabled) AccentCyan.copy(alpha = 0.15f) else Color(0xFF1A2440),
                    border = BorderStroke(
                        1.dp,
                        if (enabled) AccentCyan.copy(alpha = 0.3f) else Color(0xFF2A3455)
                    )
                ) {
                    Icon(
                        Icons.Default.Medication,
                        contentDescription = null,
                        tint = if (enabled) AccentCyan else Color(0xFF5B6A80),
                        modifier = Modifier.padding(8.dp)
                    )
                }

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        reminder.medicineName,
                        style = MaterialTheme.typography.titleMedium,
                        color = if (enabled) Color(0xFFF2F7FF) else Color(0xFF8899B5),
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        reminder.dosage,
                        style = MaterialTheme.typography.bodyMedium,
                        color = if (enabled) Color(0xFFB5C4E4) else Color(0xFF6B7A90)
                    )
                }

                Switch(
                    checked = enabled,
                    onCheckedChange = onToggle,
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = AccentCyan,
                        checkedTrackColor = AccentCyan.copy(alpha = 0.3f),
                        uncheckedThumbColor = Color(0xFF5B6A80),
                        uncheckedTrackColor = Color(0xFF1A2440)
                    )
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = Color(0xFF1A2845),
                        border = BorderStroke(1.dp, Color(0xFF2A3855))
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                Icons.Default.AccessTime,
                                contentDescription = null,
                                tint = AccentCyanBright,
                                modifier = Modifier.size(14.dp)
                            )
                            Text(
                                String.format("%02d:%02d", reminder.hourOfDay, reminder.minute),
                                color = Color(0xFFCCDDF5),
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }

                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = Color(0xFF1A2845),
                        border = BorderStroke(1.dp, Color(0xFF2A3855))
                    ) {
                        Text(
                            text = when (reminder.frequency) {
                                ReminderFrequency.DAILY -> "Daily"
                                ReminderFrequency.WEEKLY -> "Weekly"
                                ReminderFrequency.CUSTOM -> "Every ${reminder.customIntervalHours}h"
                            },
                            color = Color(0xFFB5C4E4),
                            fontSize = 12.sp,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                        )
                    }
                }

                Row {
                    IconButton(onClick = onEdit, modifier = Modifier.size(36.dp)) {
                        Icon(
                            Icons.Default.Edit,
                            contentDescription = "Edit",
                            tint = AccentCyan,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    IconButton(onClick = onDelete, modifier = Modifier.size(36.dp)) {
                        Icon(
                            Icons.Default.Delete,
                            contentDescription = "Delete",
                            tint = ErrorRed,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ReminderFormDialog(
    title: String,
    initialName: String = "",
    initialDosage: String = "",
    initialHour: Int = 20,
    initialMinute: Int = 0,
    initialFrequency: ReminderFrequency = ReminderFrequency.DAILY,
    initialCustomHours: Int = 12,
    onDismiss: () -> Unit,
    onSave: (name: String, dosage: String, hour: Int, minute: Int, frequency: ReminderFrequency, customHours: Int?) -> Unit
) {
    var medicineName by remember { mutableStateOf(initialName) }
    var dosage by remember { mutableStateOf(initialDosage) }
    var selectedFrequency by remember { mutableStateOf(initialFrequency) }
    var customHours by remember { mutableIntStateOf(initialCustomHours) }
    val timePickerState = rememberTimePickerState(
        initialHour = initialHour,
        initialMinute = initialMinute,
        is24Hour = true
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Color(0xFF141E36),
        titleContentColor = TextPrimary,
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = AccentCyan.copy(alpha = 0.15f)
                ) {
                    Icon(
                        Icons.Default.Medication,
                        contentDescription = null,
                        tint = AccentCyan,
                        modifier = Modifier.padding(6.dp)
                    )
                }
                Text(title, fontWeight = FontWeight.Bold)
            }
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                val fieldColors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = AccentCyan,
                    unfocusedBorderColor = SurfaceOutline,
                    focusedContainerColor = SurfaceCardAlt,
                    unfocusedContainerColor = SurfaceCardAlt,
                    cursorColor = AccentCyan,
                    focusedLabelColor = AccentCyan,
                    unfocusedLabelColor = TextLight,
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary
                )

                OutlinedTextField(
                    value = medicineName,
                    onValueChange = { medicineName = it },
                    label = { Text("Medicine Name") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = fieldColors,
                    singleLine = true
                )

                OutlinedTextField(
                    value = dosage,
                    onValueChange = { dosage = it },
                    label = { Text("Dosage (e.g. 500mg)") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = fieldColors,
                    singleLine = true
                )

                Text(
                    "Reminder Time",
                    color = TextSecondary,
                    style = MaterialTheme.typography.labelLarge
                )

                TimePicker(
                    state = timePickerState,
                    colors = TimePickerDefaults.colors(
                        clockDialColor = SurfaceCardAlt,
                        selectorColor = AccentCyan,
                        containerColor = Color.Transparent,
                        clockDialSelectedContentColor = BgDark,
                        clockDialUnselectedContentColor = TextPrimary,
                        periodSelectorBorderColor = SurfaceOutline,
                        periodSelectorSelectedContainerColor = AccentCyan.copy(alpha = 0.2f),
                        periodSelectorSelectedContentColor = AccentCyan,
                        periodSelectorUnselectedContentColor = TextSecondary,
                        timeSelectorSelectedContainerColor = AccentCyan.copy(alpha = 0.2f),
                        timeSelectorSelectedContentColor = AccentCyan,
                        timeSelectorUnselectedContainerColor = SurfaceCardAlt,
                        timeSelectorUnselectedContentColor = TextPrimary
                    )
                )

                Text(
                    "Frequency",
                    color = TextSecondary,
                    style = MaterialTheme.typography.labelLarge
                )

                SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
                    ReminderFrequency.entries.forEachIndexed { index, frequency ->
                        SegmentedButton(
                            shape = SegmentedButtonDefaults.itemShape(index, ReminderFrequency.entries.size),
                            selected = selectedFrequency == frequency,
                            onClick = { selectedFrequency = frequency },
                            label = {
                                Text(
                                    frequency.name.lowercase().replaceFirstChar { it.uppercase() },
                                    fontSize = 12.sp
                                )
                            }
                        )
                    }
                }

                AnimatedVisibility(visible = selectedFrequency == ReminderFrequency.CUSTOM) {
                    OutlinedTextField(
                        value = customHours.toString(),
                        onValueChange = {
                            customHours = it.filter(Char::isDigit).toIntOrNull()?.coerceIn(1, 168) ?: customHours
                        },
                        label = { Text("Repeat every N hours") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = fieldColors,
                        singleLine = true
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onSave(
                        medicineName,
                        dosage,
                        timePickerState.hour,
                        timePickerState.minute,
                        selectedFrequency,
                        if (selectedFrequency == ReminderFrequency.CUSTOM) customHours else null
                    )
                },
                enabled = medicineName.isNotBlank() && dosage.isNotBlank(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = AccentCyan,
                    contentColor = BgDark,
                    disabledContainerColor = SurfaceOutline
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Save", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            OutlinedButton(
                onClick = onDismiss,
                border = BorderStroke(1.dp, SurfaceOutline)
            ) {
                Text("Cancel", color = TextSecondary)
            }
        }
    )
}
