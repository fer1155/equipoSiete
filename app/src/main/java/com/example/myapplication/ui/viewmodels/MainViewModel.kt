package com.example.myapplication.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MainViewModel : ViewModel() {
    // Criterio 6: Estado del sonido
    private val _isSoundEnabled = MutableStateFlow(true)
    val isSoundEnabled: StateFlow<Boolean> = _isSoundEnabled.asStateFlow()

    // Criterio 4: Estado del contador
    private val _counter = MutableStateFlow<Int?>(null)
    val counter: StateFlow<Int?> = _counter.asStateFlow()

    fun toggleSound() {
        _isSoundEnabled.value = !_isSoundEnabled.value
    }

    fun startCountdown() {
        viewModelScope.launch {
            for (i in 3 downTo 0) {
                _counter.value = i
                delay(1000)
            }
            _counter.value = null
        }
    }
}