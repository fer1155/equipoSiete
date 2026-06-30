package com.example.myapplication.ui.fragments

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import com.example.myapplication.databinding.DialogRandomChallengeBinding
import com.example.myapplication.ui.viewmodels.MainViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONArray
import java.net.URL

/**
 * Diálogo encargado de mostrar un reto aleatorio al finalizar
 * el giro de la botella.
 *
 * Además del reto seleccionado, obtiene de manera aleatoria un
 * Pokémon desde una Pokédex pública y muestra su imagen como
 * elemento visual dentro del diálogo.
 */
class RandomChallengeDialog : DialogFragment() {

    private var _binding: DialogRandomChallengeBinding? = null
    private val binding get() = _binding!!

    /** ViewModel compartido que proporciona el reto aleatorio y controla el estado del juego. */
    private val viewModel: MainViewModel by activityViewModels()

    /** URL del archivo JSON que contiene la información de la Pokédex. */
    private val POKEDEX_URL =
        "https://raw.githubusercontent.com/Biuni/PokemonGO-Pokedex/master/pokedex.json"

    /**
     * Infla el layout del diálogo e inicializa el View Binding.
     *
     * @param inflater Inflador utilizado para crear la vista.
     * @param container Contenedor padre del diálogo.
     * @param savedInstanceState Estado previamente guardado del diálogo.
     * @return Vista raíz del diálogo.
     */
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DialogRandomChallengeBinding.inflate(inflater, container, false)
        return binding.root
    }

    /**
     * Configura la interfaz del diálogo, observa el reto generado,
     * inicia la carga del Pokémon aleatorio y configura el botón
     * para cerrar el diálogo.
     *
     * @param view Vista raíz del diálogo.
     * @param savedInstanceState Estado previamente guardado del diálogo.
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        isCancelable = false

        viewModel.randomChallenge.observe(viewLifecycleOwner) { challenge ->
            binding.txtChallengeDescription.text =
                challenge?.description ?: "No hay retos disponibles. ¡Agrega alguno primero!"
        }

        loadRandomPokemon()

        binding.btnCloseDialog.setOnClickListener {
            viewModel.onChallengeDialogClosed()
            dismiss()
        }
    }

    /**
     * Configura el tamaño y el fondo de la ventana del diálogo
     * una vez este ha sido mostrado.
     */
    override fun onStart() {
        super.onStart()
        dialog?.window?.apply {
            setLayout(
                (resources.displayMetrics.widthPixels * 0.9).toInt(),
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            setBackgroundDrawableResource(android.R.color.transparent)
        }
    }

    /**
     * Descarga la Pokédex desde Internet, selecciona un Pokémon
     * de manera aleatoria y muestra su imagen dentro del diálogo.
     *
     * La descarga y el procesamiento del JSON se realizan en un
     * hilo de entrada/salida para evitar bloquear la interfaz.
     */
    private fun loadRandomPokemon() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val jsonText = URL(POKEDEX_URL).readText()
                val root = org.json.JSONObject(jsonText)
                val pokemonArray: JSONArray = root.getJSONArray("pokemon")

                val randomIndex = (0 until pokemonArray.length()).random()
                val pokemon = pokemonArray.getJSONObject(randomIndex)
                val imgUrl = pokemon
                    .getString("img")
                    .replace("http://", "https://")

                val bitmap: Bitmap = URL(imgUrl).openStream().use { stream ->
                    BitmapFactory.decodeStream(stream)
                }

                withContext(Dispatchers.Main) {
                    if (_binding != null) {
                        binding.imgPokemon.setImageBitmap(bitmap)
                    }
                }
            } catch (e: Exception) {
                android.util.Log.e("PokemonDebug", "Error: ${e.message}", e)
            }
        }
    }

    /**
     * Libera la referencia del View Binding para evitar
     * fugas de memoria cuando el diálogo es destruido.
     */
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {

        /** Etiqueta utilizada para identificar este diálogo dentro del FragmentManager. */
        const val TAG = "RandomChallengeDialog"
    }
}