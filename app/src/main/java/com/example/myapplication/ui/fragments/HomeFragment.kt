package com.example.myapplication.ui.fragments

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.myapplication.R
import com.example.myapplication.databinding.FragmentHomeBinding
import com.example.myapplication.ui.viewmodels.MainViewModel


/**
 * Fragment principal de la aplicación.
 *
 * Gestiona la pantalla inicial del juego, incluyendo la animación de la
 * botella, la cuenta regresiva, la barra de herramientas y la navegación
 * hacia las demás pantallas de la aplicación.
 */
class HomeFragment : Fragment() {


    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val viewModel: MainViewModel by activityViewModels()

    private var spinAnimator: ObjectAnimator? = null
    private var blinkAnimator: ValueAnimator? = null
    private var currentBottleAngle = 0f


    /**
     * Infla la vista del fragmento e inicializa el View Binding.
     *
     * @param inflater Inflador utilizado para crear la vista.
     * @param container Contenedor padre del fragmento.
     * @param savedInstanceState Estado previamente guardado del fragmento.
     * @return Vista raíz del fragmento.
     */
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel
        return binding.root
    }

    /**
     * Inicializa los componentes visuales y configura los eventos del fragmento.
     *
     * @param view Vista raíz del fragmento.
     * @param savedInstanceState Estado previamente guardado del fragmento.
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupToolbarListeners()
        setupBlinkAnimation()
        setupSpinButton()
        setupObservers()
    }


    /**
     * Configura los eventos de la barra de herramientas.
     *
     * Define las acciones para los botones de calificación,
     * información, administración de retos y compartir la aplicación.
     */
    private fun setupToolbarListeners() {
        val toolbar = binding.includeToolbar

        toolbar.btnStar.setOnClickListener {
            val url = "https://play.google.com/store/apps/details?id=com.nequi.MobileApp&hl=es_419&gl=es"
            val intent = Intent(Intent.ACTION_VIEW).apply { data = url.toUri() }
            startActivity(intent)
        }

        toolbar.btnInfo.setOnClickListener {
            val bundle = Bundle().apply { putBoolean("audioWasOn", viewModel.isSoundOn()) }
            findNavController().navigate(R.id.action_homeFragment_to_instructionsFragment, bundle)
        }

        toolbar.btnAdd.setOnClickListener {
            val bundle = Bundle().apply { putBoolean("audioWasOn", viewModel.isSoundOn()) }
            findNavController().navigate(R.id.action_homeFragment_to_challengeFragment, bundle)
        }

        toolbar.btnShare.setOnClickListener {
            val shareText = """
                App pico botella
                Solo los valientes lo juegan !!
                https://play.google.com/store/apps/details?id=com.nequi.MobileApp&hl=es_419&gl=es
            """.trimIndent()

            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_SUBJECT, "App pico botella")
                putExtra(Intent.EXTRA_TEXT, shareText)
            }
            startActivity(Intent.createChooser(shareIntent, "Compartir usando"))
        }
    }

    // ── Blink del botón ──────────────────────────────────────────────────────

    /**
     * Inicializa la animación de parpadeo del botón de giro.
     *
     * El efecto permanece activo hasta que el fragmento es destruido.
     */
    private fun setupBlinkAnimation() {
        blinkAnimator = ValueAnimator.ofFloat(1f, 0.15f, 1f).apply {
            duration = 900
            repeatCount = ValueAnimator.INFINITE
            repeatMode = ValueAnimator.RESTART
            addUpdateListener { anim ->
                binding.btnSpin.alpha = anim.animatedValue as Float
            }
            start()
        }
    }


    /**
     * Configura el botón encargado de iniciar el giro de la botella.
     */
    private fun setupSpinButton() {
        binding.btnSpin.setOnClickListener {
            viewModel.startSpin(spinDurationMs = 4000L)
        }
    }


    /**
     * Registra los observadores del ViewModel para mantener sincronizada
     * la interfaz con el estado de la aplicación.
     */
    private fun setupObservers() {

        viewModel.bottleAngle.observe(viewLifecycleOwner) { targetAngle ->
            if (viewModel.isSpinning.value == true) {
                animateBottleTo(targetAngle)
            }
        }

        viewModel.isSpinning.observe(viewLifecycleOwner) { spinning ->
            binding.btnSpin.visibility = if (spinning) View.INVISIBLE else View.VISIBLE
        }

        viewModel.counter.observe(viewLifecycleOwner) { count ->
            if (count != null) {
                binding.txtCounter.text = count.toString()
                binding.txtCounter.setTextColor(android.graphics.Color.parseColor("#FF840B"))
                binding.txtCounter.visibility = View.VISIBLE
            } else {
                binding.txtCounter.visibility = View.GONE
            }
        }

        viewModel.showChallengeDialog.observe(viewLifecycleOwner) { show ->
            if (show) {
                viewModel.onChallengeDialogShown()
                showChallengeDialog()
            }
        }
    }


    /**
     * Ejecuta la animación de giro de la botella hasta el ángulo indicado.
     *
     * @param targetAngle Ángulo final al que debe girar la botella.
     */
    private fun animateBottleTo(targetAngle: Float) {
        spinAnimator?.cancel()

        spinAnimator = ObjectAnimator.ofFloat(
            binding.imgBottle,
            "rotation",
            currentBottleAngle,
            targetAngle
        ).apply {
            duration = 4000L
            interpolator = BottleSpinInterpolator()
            addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    currentBottleAngle = targetAngle % 360f
                    binding.imgBottle.rotation = currentBottleAngle
                }
            })
            start()
        }
    }


    /**
     * Muestra el diálogo con el reto aleatorio generado.
     *
     * Si el diálogo ya se encuentra visible, no crea una nueva instancia.
     */
    private fun showChallengeDialog() {
        if (parentFragmentManager.findFragmentByTag(RandomChallengeDialog.TAG) != null) return
        RandomChallengeDialog().show(parentFragmentManager, RandomChallengeDialog.TAG)
    }


    /**
     * Libera los recursos utilizados por el fragmento y cancela las
     * animaciones activas para evitar fugas de memoria.
     */
    override fun onDestroyView() {
        super.onDestroyView()
        spinAnimator?.cancel()
        blinkAnimator?.cancel()
        _binding = null
    }
}

/**
 * Interpolador personalizado para la animación de giro de la botella.
 *
 * Simula una aceleración inicial, una velocidad constante durante
 * la mayor parte del recorrido y una desaceleración progresiva al
 * finalizar el giro, proporcionando un movimiento más natural.
 */
private class BottleSpinInterpolator : android.view.animation.Interpolator {
    override fun getInterpolation(input: Float): Float {
        return when {
            input < 0.2f -> {
                val t = input / 0.2f
                t * t * 0.3f
            }
            input < 0.75f -> {
                0.3f + (input - 0.2f) / 0.55f * 0.55f
            }
            else -> {
                val t = (input - 0.75f) / 0.25f
                0.85f + (1f - (1f - t) * (1f - t) * (1f - t)) * 0.15f
            }
        }
    }
}