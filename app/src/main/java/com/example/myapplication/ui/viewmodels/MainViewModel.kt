package com.example.myapplication.ui.viewmodels

import android.app.Application
import android.media.MediaPlayer
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.myapplication.R
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val _isSoundEnabled = MutableLiveData(true)
    val isSoundEnabled: LiveData<Boolean> = _isSoundEnabled

    private val _counter = MutableLiveData<Int?>(null)
    val counter: LiveData<Int?> = _counter

    private var mediaPlayer: MediaPlayer? = null

    init {
        setupMediaPlayer()
    }

    private fun setupMediaPlayer() {
        mediaPlayer = MediaPlayer.create(getApplication(), R.raw.background_music).apply {
            isLooping = true
            if (_isSoundEnabled.value == true) {
                start()
            }
        }
    }

    fun toggleSound() {
        val newState = !(_isSoundEnabled.value ?: true)
        _isSoundEnabled.value = newState
        if (newState) {
            mediaPlayer?.start()
        } else {
            mediaPlayer?.pause()
        }
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

    override fun onCleared() {
        super.onCleared()
        mediaPlayer?.release()
        mediaPlayer = null
    }
}
