package com.example.myapplication.view.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.myapplication.databinding.FragmentInstructionsBinding
import com.example.myapplication.utils.AnimationUtils
import androidx.fragment.app.activityViewModels
import com.example.myapplication.viewmodels.MainViewModel

/**
 * Fragment encargado de mostrar las instrucciones del juego.
 *
 * Presenta las reglas básicas al usuario, reproduce la animación
 * ilustrativa de la pantalla y controla el estado de la música de
 * fondo mientras las instrucciones permanecen visibles.
 */
class InstructionsFragment : Fragment() {

    private var _binding: FragmentInstructionsBinding? = null
    private val binding get() = _binding!!

    /** Indica si la música estaba reproduciéndose antes de abrir el fragmento. */
    private var audioWasOn = false

    /** ViewModel compartido encargado de controlar la música del juego. */
    private val MainViewModel: MainViewModel by activityViewModels()

    /**
     * Infla el layout del fragmento e inicializa el View Binding.
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

        _binding = FragmentInstructionsBinding.inflate(
            inflater,
            container,
            false
        )

        return binding.root
    }

    /**
     * Configura la interfaz del fragmento una vez creada la vista.
     *
     * Si la música estaba activa al ingresar, esta se pausa mientras
     * el usuario visualiza las instrucciones. Además, inicia la
     * animación ilustrativa y configura el botón para regresar a la
     * pantalla anterior.
     *
     * @param view Vista raíz del fragmento.
     * @param savedInstanceState Estado previamente guardado del fragmento.
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (audioWasOn) {
            MainViewModel.pauseMusic()
        }

        AnimationUtils.victoryAnimation(binding.imgVictory)

        binding.btnBack.setOnClickListener {
            if (audioWasOn) {
                MainViewModel.resumeMusic()
            }

            findNavController().navigateUp()
        }
    }

    /**
     * Libera la referencia del View Binding para evitar fugas de memoria.
     */
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    /**
     * Recupera los argumentos enviados al fragmento, incluyendo el
     * estado de reproducción de la música.
     *
     * @param savedInstanceState Estado previamente guardado del fragmento.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        audioWasOn = arguments?.getBoolean("audioWasOn") ?: false
    }
}