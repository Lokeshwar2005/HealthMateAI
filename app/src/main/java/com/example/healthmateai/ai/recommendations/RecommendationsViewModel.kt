package com.example.healthmateai.ai.recommendations

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.healthmateai.ai.HealthPredictionSnapshot
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class RecommendationsUiState(
    val isLoading: Boolean = false,
    val sections: List<RecommendationSection> = emptyList(),
    val error: String? = null
)

class RecommendationsViewModel : ViewModel() {

    private val repository = RecommendationsRepository()

    private val _uiState = MutableStateFlow(RecommendationsUiState())
    val uiState: StateFlow<RecommendationsUiState> = _uiState.asStateFlow()

    fun loadRecommendations(snapshot: HealthPredictionSnapshot) {
        if (_uiState.value.isLoading) return

        viewModelScope.launch {
            _uiState.value = RecommendationsUiState(isLoading = true)
            runCatching {
                repository.getRecommendations(snapshot)
            }.onSuccess { sections ->
                _uiState.value = RecommendationsUiState(sections = sections)
            }.onFailure { error ->
                _uiState.value = RecommendationsUiState(
                    error = error.message ?: "Could not generate recommendations"
                )
            }
        }
    }

    fun retry(snapshot: HealthPredictionSnapshot) {
        repository.clearCache()
        loadRecommendations(snapshot)
    }
}
