package com.example.healthmateai.ai.diet

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class DietPlannerUiState(
    val isLoading: Boolean = false,
    val dietPlan: DietPlan? = null,
    val error: String? = null
)

class DietPlannerViewModel(
    private val repository: DietPlannerRepository = DietPlannerRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(DietPlannerUiState())
    val uiState: StateFlow<DietPlannerUiState> = _uiState.asStateFlow()

    fun generatePlan(payload: DietPlanPayload) {
        viewModelScope.launch {
            _uiState.value = DietPlannerUiState(isLoading = true)
            runCatching { repository.generateDietPlan(payload) }
                .onSuccess { plan ->
                    _uiState.value = DietPlannerUiState(dietPlan = plan)
                }
                .onFailure { error ->
                    _uiState.value = DietPlannerUiState(error = repository.toReadableError(error))
                }
        }
    }
}

object DietPlannerViewModelFactory : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return DietPlannerViewModel() as T
    }
}
