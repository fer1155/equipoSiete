package com.example.myapplication.ui.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.myapplication.model.Challenge
import com.example.myapplication.repository.ChallengeRepository
import kotlinx.coroutines.launch

/**
 * ViewModel encargado de administrar los retos del juego.
 *
 * Actúa como intermediario entre la interfaz de usuario y el
 * repositorio de retos, permitiendo registrar, consultar,
 * actualizar y eliminar retos, además de exponer el estado
 * de progreso de las operaciones.
 *
 * @param application Instancia de la aplicación utilizada para
 * obtener el contexto necesario para inicializar el repositorio.
 */
class ChallengeViewModel(application: Application) : AndroidViewModel(application) {

    /** Contexto de la aplicación. */
    val context = getApplication<Application>()

    /** Repositorio encargado de la persistencia de los retos. */
    private val challengeRepository = ChallengeRepository(context)

    /** Lista observable de retos disponibles. */
    private val _listChallenge = MutableLiveData<MutableList<Challenge>>()
    val listChallenge: LiveData<MutableList<Challenge>> get() = _listChallenge

    /** Estado observable que indica si existe una operación en progreso. */
    private val _progresState = MutableLiveData(false)


    /**
     * Guarda un nuevo reto en el repositorio.
     *
     * Durante la operación se actualiza el estado de progreso y,
     * al finalizar correctamente, se invoca el callback con el
     * mensaje correspondiente.
     *
     * @param challenge Reto que será almacenado.
     * @param message Función que recibe el mensaje de resultado
     * de la operación.
     */
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

    /**
     * Obtiene la lista completa de retos almacenados y actualiza
     * el estado observable utilizado por la interfaz.
     */
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

    /**
     * Elimina un reto existente del repositorio.
     *
     * @param challenge Reto que será eliminado.
     * @param onComplete Callback ejecutado al finalizar la operación exitosamente.
     */
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

    /**
     * Actualiza la información de un reto existente.
     *
     * @param challenge Reto con la información actualizada.
     * @param onComplete Callback ejecutado al finalizar la operación exitosamente.
     */
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
