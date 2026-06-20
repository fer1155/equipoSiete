package com.example.myapplication.ui.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainViewModel : ViewModel() {

    private val _isSoundEnabled = MutableLiveData(true)
    val isSoundEnabled: LiveData<Boolean> = _isSoundEnabled

    private val _counter = MutableLiveData<Int?>(null)
    val counter: LiveData<Int?> = _counter

    fun toggleSound() {
        _isSoundEnabled.value = !(_isSoundEnabled.value ?: true)
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