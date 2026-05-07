package com.example.healthmateai.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.healthmateai.network.PredictionRepository
import com.example.healthmateai.network.PredictionRequest
import com.example.healthmateai.network.PredictionResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class PredictionUiState(
    val isLoading: Boolean = false,
    val result: PredictionResponse? = null,
    val errorMessage: String? = null
)

class PredictionViewModel(
    private val repository: PredictionRepository = PredictionRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(PredictionUiState())
    val uiState: StateFlow<PredictionUiState> = _uiState.asStateFlow()

    fun predict(disease: String, inputs: Map<String, Any>) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            try {
                Log.d("PredictionViewModel", "Sending $disease request with ${inputs.size} inputs")
                val response = repository.predict(
                    PredictionRequest(
                        disease = disease,
                        inputs = inputs
                    )
                )
                _uiState.value = PredictionUiState(result = response)
            } catch (exception: Exception) {
                Log.e("PredictionViewModel", "Prediction failed", exception)
                _uiState.value = PredictionUiState(
                    errorMessage = exception.message ?: "Prediction request failed"
                )
            }
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }

    fun clearResult() {
        _uiState.value = _uiState.value.copy(result = null)
    }
}

object PredictionViewModelFactory : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return PredictionViewModel() as T
    }
}