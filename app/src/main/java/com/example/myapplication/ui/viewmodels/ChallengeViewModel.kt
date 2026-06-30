package com.example.myapplication.ui.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.myapplication.model.Challenge
import com.example.myapplication.repository.ChallengeRepository
import kotlinx.coroutines.launch

class ChallengeViewModel(application: Application) : AndroidViewModel(application) {

    val context = getApplication<Application>()
    private val challengeRepository = ChallengeRepository(context)

    private val _listChallenge = MutableLiveData<MutableList<Challenge>>()
    val listChallenge: LiveData<MutableList<Challenge>> get() = _listChallenge

    private val _progresState = MutableLiveData(false)

    fun saveChallenge(challenge: Challenge, message: (String) -> Unit) {
        viewModelScope.launch {
            _progresState.value = true
            try {
                challengeRepository.saveChallenge(challenge) { msg ->
                    message(msg)
                }
                // Refrescamos la lista después de guardar
                _listChallenge.value = challengeRepository.getListChallenge()
                _progresState.value = false
            } catch (e: Exception) {
                e.printStackTrace()
                _progresState.value = false
            }
        }
    }

    fun getListChallenge() {
        viewModelScope.launch {
            _progresState.value = true
            try {
                _listChallenge.value = challengeRepository.getListChallenge()
                _progresState.value = false
            } catch (e: Exception) {
                e.printStackTrace()
                _progresState.value = false
            }

        }
    }

    fun deleteChallenge(challenge: Challenge, onComplete: () -> Unit) {
        viewModelScope.launch {
            _progresState.value = true
            try {
                challengeRepository.deleteChallenge(challenge)
                // Refrescamos la lista inmediatamente después de borrar
                _listChallenge.value = challengeRepository.getListChallenge()
                _progresState.value = false
                onComplete()
            } catch (e: Exception) {
                e.printStackTrace()
                _progresState.value = false
            }
        }
    }

    fun updateChallenge(challenge: Challenge, onComplete: () -> Unit) {
        viewModelScope.launch {
            _progresState.value = true
            try {
                challengeRepository.updateRepository(challenge)
                // Refrescamos la lista después de actualizar
                _listChallenge.value = challengeRepository.getListChallenge()
                _progresState.value = false
                onComplete()
            } catch (e: Exception) {
                e.printStackTrace()
                _progresState.value = false
            }
        }
    }
}