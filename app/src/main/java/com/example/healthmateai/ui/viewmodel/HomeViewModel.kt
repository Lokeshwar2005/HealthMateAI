package com.example.healthmateai.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.healthmateai.data.HomeContentRepository
import com.example.healthmateai.ui.model.HomeUiContent
import com.example.healthmateai.ui.model.HomeUiState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HomeViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = HomeContentRepository(application.applicationContext)

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState

    init {
        viewModelScope.launch {
            val content = withContext(Dispatchers.IO) {
                repository.loadContent()
            }
            _uiState.value = _uiState.value.copy(content = content)
        }
    }

    fun selectDisease(index: Int) {
        _uiState.value = _uiState.value.copy(selectedDiseaseIndex = index)
    }

    fun setChatOpen(isOpen: Boolean) {
        _uiState.value = _uiState.value.copy(isChatOpen = isOpen)
    }
}
