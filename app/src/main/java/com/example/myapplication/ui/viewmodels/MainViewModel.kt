package com.example.myapplication.ui.viewmodels

import android.app.Application
import android.media.MediaPlayer
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.myapplication.R
import com.example.myapplication.model.Challenge
import com.example.myapplication.repository.ChallengeRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * ViewModel principal de la aplicación.
 *
 * Administra el estado general del juego, incluyendo el control del
 * sonido de fondo, la animación del giro de la botella, la cuenta
 * regresiva y la obtención de retos aleatorios.
 *
 * @param application Instancia de la aplicación utilizada para acceder
 * a recursos y crear los reproductores de audio.
 */
class MainViewModel(application: Application) : AndroidViewModel(application) {

    /** Estado que indica si el sonido de la aplicación está habilitado. */
    private val _isSoundEnabled = MutableLiveData(true)
    val isSoundEnabled: LiveData<Boolean> = _isSoundEnabled

    /** Reproductor encargado de la música de fondo. */
    private var backgroundPlayer: MediaPlayer? = null

    /** Valor observable utilizado para mostrar la cuenta regresiva. */
    private val _counter = MutableLiveData<Int?>(null)
    val counter: LiveData<Int?> = _counter

    /** Estado que indica si actualmente la botella se encuentra girando. */
    private val _isSpinning = MutableLiveData(false)
    val isSpinning: LiveData<Boolean> = _isSpinning

    /** Indica cuándo debe mostrarse el diálogo con el reto aleatorio. */
    private val _showChallengeDialog = MutableLiveData(false)
    val showChallengeDialog: LiveData<Boolean> = _showChallengeDialog

    /** Ángulo de rotación actual de la botella. */
    private val _bottleAngle = MutableLiveData(0f)
    val bottleAngle: LiveData<Float> = _bottleAngle

    /** Reto seleccionado aleatoriamente para la ronda actual. */
    private val _randomChallenge = MutableLiveData<Challenge?>(null)
    val randomChallenge: LiveData<Challenge?> = _randomChallenge

    /** Reproductor encargado del sonido del giro de la botella. */
    private var bottlePlayer: MediaPlayer? = null

    /** Repositorio utilizado para obtener los retos almacenados. */
    private val repository = ChallengeRepository(application)

    init {
        setupBackgroundPlayer()
    }

    /**
     * Inicializa el reproductor de música de fondo.
     *
     * La reproducción comienza automáticamente si el sonido
     * se encuentra habilitado.
     */
    private fun setupBackgroundPlayer() {
        backgroundPlayer = MediaPlayer.create(getApplication(), R.raw.background_music).apply {
            isLooping = true
            if (_isSoundEnabled.value == true) start()
        }
    }

    /**
     * Activa o desactiva el sonido de la aplicación.
     *
     * Cuando el sonido está habilitado se reproduce la música de fondo;
     * de lo contrario, la reproducción se pausa.
     */
    fun toggleSound() {
        val newState = !(_isSoundEnabled.value ?: true)
        _isSoundEnabled.value = newState
        if (newState) backgroundPlayer?.start() else backgroundPlayer?.pause()
    }

    /**
     * Indica si el sonido se encuentra actualmente habilitado.
     *
     * @return `true` si el sonido está activo; en caso contrario `false`.
     */
    fun isSoundOn(): Boolean = _isSoundEnabled.value == true

    /**
     * Pausa la música de fondo si el sonido está habilitado y
     * actualmente se encuentra reproduciéndose.
     */
    fun pauseMusic() {
        if (_isSoundEnabled.value == true && backgroundPlayer?.isPlaying == true) {
            backgroundPlayer?.pause()
        }
    }

    /**
     * Reanuda la reproducción de la música de fondo cuando el sonido
     * está habilitado y el reproductor se encuentra pausado.
     */
    fun resumeMusic() {
        if (_isSoundEnabled.value == true && backgroundPlayer?.isPlaying == false) {
            backgroundPlayer?.start()
        }
    }

    /**
     * Inicia el giro de la botella.
     *
     * Durante el proceso se pausa la música de fondo, se reproduce el
     * sonido de la botella, se calcula un nuevo ángulo de giro y, al
     * finalizar la animación, se ejecuta la cuenta regresiva y se
     * selecciona un reto aleatorio.
     *
     * @param spinDurationMs Duración de la animación de giro en milisegundos.
     */
    fun startSpin(spinDurationMs: Long = 4000L) {
        if (_isSpinning.value == true) return
        _isSpinning.value = true

        pauseMusic()
        startBottleSound()

        val extraDegrees = (720..1440).random().toFloat()
        val finalAngle = (_bottleAngle.value ?: 0f) + extraDegrees
        _bottleAngle.value = finalAngle

        viewModelScope.launch {
            delay(spinDurationMs)
            stopBottleSound()

            for (i in 3 downTo 0) {
                _counter.value = i
                delay(1000L)
            }
            _counter.value = null

            loadRandomChallenge()
            _showChallengeDialog.value = true
        }
    }

    /**
     * Indica que el diálogo del reto ya fue mostrado al usuario,
     * restableciendo el estado correspondiente.
     */
    fun onChallengeDialogShown() {
        _showChallengeDialog.value = false
    }

    /**
     * Finaliza el estado de giro de la botella y reanuda la
     * reproducción de la música de fondo.
     */
    fun onChallengeDialogClosed() {
        _isSpinning.value = false
        resumeMusic()
    }

    /**
     * Obtiene un reto aleatorio desde el repositorio y actualiza
     * el estado observable correspondiente.
     *
     * Si no existen retos almacenados, el valor será `null`.
     */
    private fun loadRandomChallenge() {
        viewModelScope.launch {
            val list = repository.getListChallenge()
            _randomChallenge.value = if (list.isNotEmpty()) list.random() else null
        }
    }

    /**
     * Ejecuta únicamente la cuenta regresiva sin iniciar el giro
     * de la botella.
     *
     * Esta funcionalidad puede reutilizarse de forma independiente
     * en otras pantallas de la aplicación.
     */
    fun startCountdown() {
        viewModelScope.launch {
            for (i in 3 downTo 0) {
                _counter.value = i
                delay(1000L)
            }
            _counter.value = null
        }
    }

    /**
     * Inicia la reproducción continua del sonido asociado al
     * movimiento de la botella.
     */
    private fun startBottleSound() {
        try {
            bottlePlayer?.release()
            bottlePlayer = MediaPlayer.create(getApplication(), R.raw.bottle_rolling).apply {
                isLooping = true
                start()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * Detiene la reproducción del sonido de la botella y libera
     * los recursos utilizados por el reproductor.
     */
    private fun stopBottleSound() {
        try {
            bottlePlayer?.stop()
            bottlePlayer?.release()
            bottlePlayer = null
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * Libera los recursos utilizados por los reproductores de audio
     * cuando el ViewModel deja de existir.
     */
    override fun onCleared() {
        super.onCleared()
        backgroundPlayer?.release()
        backgroundPlayer = null
        bottlePlayer?.release()
        bottlePlayer = null
    }
}