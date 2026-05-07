package com.example.healthmateai.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.healthmateai.data.SplashContentRepository
import com.example.healthmateai.ui.model.SplashContent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SplashViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = SplashContentRepository(application.applicationContext)

    private val _content = MutableStateFlow(SplashContent())
    val content: StateFlow<SplashContent> = _content

    init {
        viewModelScope.launch {
            val loaded = withContext(Dispatchers.IO) {
                repository.loadContent()
            }
            _content.value = loaded
        }
    }
}
