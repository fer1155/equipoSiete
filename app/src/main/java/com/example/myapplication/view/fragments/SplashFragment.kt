package com.example.myapplication.view.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.myapplication.R
import com.example.myapplication.databinding.FragmentSplashBinding
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * Fragment que muestra la pantalla de bienvenida de la aplicación.
 *
 * Se encarga de presentar el logo de la aplicación mediante una animación,
 * ocultar temporalmente la barra de estado para ofrecer una experiencia de
 * pantalla completa y, tras un tiempo de espera, navegar automáticamente
 * hacia la pantalla principal del juego.
 */
class SplashFragment : Fragment() {

    private var _binding: FragmentSplashBinding? = null
    private val binding get() = _binding!!

    /**
     * Infla el layout del fragmento y configura la pantalla completa
     * ocultando la barra de estado.
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

        activity?.window?.let { window ->
            val controller = WindowCompat.getInsetsController(window, window.decorView)
            controller.hide(WindowInsetsCompat.Type.statusBars())
            controller.systemBarsBehavior =
                WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }

        _binding = FragmentSplashBinding.inflate(inflater, container, false)
        return binding.root
    }

    /**
     * Inicializa la animación del logotipo y programa la navegación
     * automática hacia la pantalla principal después de cinco segundos.
     *
     * @param view Vista raíz del fragmento.
     * @param savedInstanceState Estado previamente guardado del fragmento.
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val pulse = AnimationUtils.loadAnimation(
            requireContext(),
            android.R.anim.fade_in
        )

        binding.imgLogo.startAnimation(pulse)

        lifecycleScope.launch {
            delay(5000)
            findNavController().navigate(R.id.action_splashFragment_to_homeFragment)
        }
    }

    /**
     * Restaura la barra de estado y libera la referencia del
     * View Binding para evitar fugas de memoria.
     */
    override fun onDestroyView() {
        super.onDestroyView()

        activity?.window?.let { window ->
            WindowCompat.getInsetsController(window, window.decorView)
                .show(WindowInsetsCompat.Type.statusBars())
        }

        _binding = null
    }
}