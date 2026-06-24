package com.example.myapplication.ui.fragments

import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.myapplication.databinding.FragmentHomeBinding
import com.example.myapplication.ui.viewmodels.MainViewModel
import androidx.core.net.toUri
import androidx.navigation.fragment.findNavController
import com.example.myapplication.R


class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val viewModel: MainViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel

        setupToolbarListeners()
        setupMainButtonAnimation()

        return binding.root
    }

    private fun setupToolbarListeners() {
        val toolbar = binding.includeToolbar
        
        toolbar.btnStar.setOnClickListener {
            val url = "https://play.google.com/store/apps/details?id=com.nequi.MobileApp&hl=es_419&gl=es"
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = url.toUri()

            // 3. Inicia la actividad
            startActivity(intent)
        }

        // El botón de sonido ya tiene el onClick en el XML vinculado al ViewModel
        // Pero lo reforzamos aquí si es necesario o lo dejamos que el XML lo maneje.
        // toolbar.btnSound.setOnClickListener { viewModel.toggleSound() }

        toolbar.btnInfo.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_instructionsFragment)
        }

        toolbar.btnAdd.setOnClickListener {
            // Acción para agregar
            findNavController().navigate(R.id.action_homeFragment_to_challengeFragment)
        }

        toolbar.btnShare.setOnClickListener {
            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_SUBJECT, "Pico Botella")
                putExtra(Intent.EXTRA_TEXT, "¡Mira esta app de Pico Botella! https://play.google.com/store/apps/details?id=com.nequi.MobileApp&hl=es_419&gl=es ")
            }
            startActivity(Intent.createChooser(shareIntent, "Compartir usando"))
        }
    }

    private fun setupMainButtonAnimation() {
        // Animación de pulso para el botón
        val scaleX = PropertyValuesHolder.ofFloat(View.SCALE_X, 1f, 1.1f)
        val scaleY = PropertyValuesHolder.ofFloat(View.SCALE_Y, 1f, 1.1f)
        
        ObjectAnimator.ofPropertyValuesHolder(binding.btnSpin, scaleX, scaleY).apply {
            duration = 800
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.REVERSE
            interpolator = AccelerateDecelerateInterpolator()
            start()
        }

        binding.btnSpin.setOnClickListener {
            viewModel.startCountdown()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}