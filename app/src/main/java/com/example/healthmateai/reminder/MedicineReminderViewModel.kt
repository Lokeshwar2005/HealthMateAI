package com.example.healthmateai.reminder

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class MedicineReminderUiState(
    val isSaving: Boolean = false,
    val saveError: String? = null,
    val successMessage: String? = null
)

class MedicineReminderViewModel(
    private val repository: MedicineReminderRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(MedicineReminderUiState())
    val uiState: StateFlow<MedicineReminderUiState> = _uiState.asStateFlow()

    val reminders = repository.observeAll().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = emptyList()
    )

    val upcomingReminder = repository.observeUpcoming().map { reminder ->
        reminder?.let {
            "Next: ${it.medicineName} - ${String.format("%02d:%02d", it.hourOfDay, it.minute)}"
        } ?: "No reminders yet"
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = "No reminders yet"
    )

    fun addReminder(
        medicineName: String,
        dosage: String,
        hourOfDay: Int,
        minute: Int,
        frequency: ReminderFrequency,
        customIntervalHours: Int?
    ) {
        if (medicineName.isBlank() || dosage.isBlank()) {
            _uiState.value = MedicineReminderUiState(saveError = "Medicine name and dosage are required")
            return
        }

        viewModelScope.launch {
            _uiState.value = MedicineReminderUiState(isSaving = true)
            runCatching {
                repository.addReminder(
                    medicineName = medicineName.trim(),
                    dosage = dosage.trim(),
                    hourOfDay = hourOfDay,
                    minute = minute,
                    frequency = frequency,
                    customIntervalHours = customIntervalHours
                )
            }.onSuccess {
                _uiState.value = MedicineReminderUiState(successMessage = "Reminder saved")
            }.onFailure { throwable ->
                _uiState.value = MedicineReminderUiState(
                    saveError = throwable.message ?: "Could not save reminder"
                )
            }
        }
    }

    fun updateReminder(
        id: Long,
        medicineName: String,
        dosage: String,
        hourOfDay: Int,
        minute: Int,
        frequency: ReminderFrequency,
        customIntervalHours: Int?
    ) {
        if (medicineName.isBlank() || dosage.isBlank()) {
            _uiState.value = MedicineReminderUiState(saveError = "Medicine name and dosage are required")
            return
        }

        viewModelScope.launch {
            _uiState.value = MedicineReminderUiState(isSaving = true)
            runCatching {
                repository.updateReminder(
                    id = id,
                    medicineName = medicineName.trim(),
                    dosage = dosage.trim(),
                    hourOfDay = hourOfDay,
                    minute = minute,
                    frequency = frequency,
                    customIntervalHours = customIntervalHours
                )
            }.onSuccess {
                _uiState.value = MedicineReminderUiState(successMessage = "Reminder updated")
            }.onFailure { throwable ->
                _uiState.value = MedicineReminderUiState(
                    saveError = throwable.message ?: "Could not update reminder"
                )
            }
        }
    }

    fun deleteReminder(id: Long) {
        viewModelScope.launch {
            runCatching { repository.deleteReminder(id) }
                .onSuccess {
                    _uiState.value = MedicineReminderUiState(successMessage = "Reminder deleted")
                }
                .onFailure {
                    _uiState.value = MedicineReminderUiState(saveError = "Could not delete reminder")
                }
        }
    }

    fun toggleReminder(id: Long, enabled: Boolean) {
        viewModelScope.launch {
            runCatching { repository.toggleReminder(id, enabled) }
                .onFailure {
                    _uiState.value = MedicineReminderUiState(saveError = "Could not update reminder")
                }
        }
    }

    fun clearTransientState() {
        _uiState.value = _uiState.value.copy(saveError = null, successMessage = null)
    }
}

class MedicineReminderViewModelFactory(
    private val context: Context
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return MedicineReminderViewModel(MedicineReminderRepository(context.applicationContext)) as T
    }
}
