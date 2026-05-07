package com.example.healthmateai.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.healthmateai.data.AuthContentRepository
import com.example.healthmateai.ui.model.AuthUiContent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AuthContentViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = AuthContentRepository(application.applicationContext)

    private val _content = MutableStateFlow(AuthUiContent())
    val content: StateFlow<AuthUiContent> = _content

    init {
        viewModelScope.launch {
            val loaded = withContext(Dispatchers.IO) {
                repository.loadContent()
            }
            _content.value = loaded
        }
    }
}
