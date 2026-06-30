package com.example.myapplication.ui.fragments

import androidx.fragment.app.viewModels
import androidx.appcompat.app.AlertDialog
import android.text.Editable
import android.text.TextWatcher
import android.widget.Button
import android.widget.EditText
import com.example.myapplication.ui.viewmodels.ChallengeViewModel

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myapplication.R
import com.example.myapplication.adapter.ChallengeAdapter
import com.example.myapplication.databinding.FragmentChallengeBinding
import com.example.myapplication.model.Challenge
import com.example.myapplication.ui.viewmodels.MainViewModel
import kotlin.getValue

/**
 * Fragment encargado de administrar los retos del juego.
 *
 * Permite visualizar la lista de retos disponibles, agregar nuevos
 * retos y editar los ya existentes. Además, controla la reproducción
 * de la música de fondo mientras el usuario interactúa con esta pantalla.
 */
class ChallengeFragment : Fragment() {

    private var _binding: FragmentChallengeBinding? = null
    private val binding get() = _binding!!

    /** ViewModel encargado de la gestión de los retos. */
    private val viewModel: ChallengeViewModel by viewModels()

    /** Adaptador utilizado para mostrar la lista de retos. */
    private lateinit var adapter: ChallengeAdapter

    /** Indica si la música estaba activa antes de abrir este fragmento. */
    private var audioWasOn = false

    /** ViewModel compartido para controlar la música de fondo. */
    private val mainViewModel: MainViewModel by activityViewModels()

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

        _binding = FragmentChallengeBinding.inflate(
            inflater,
            container,
            false
        )

        return binding.root
    }

    /**
     * Configura la interfaz de usuario, el RecyclerView y los eventos
     * asociados a la pantalla una vez creada la vista.
     *
     * @param view Vista raíz del fragmento.
     * @param savedInstanceState Estado previamente guardado del fragmento.
     */
    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?
    ) {
        if (audioWasOn) {
            mainViewModel.pauseMusic()
        }

        super.onViewCreated(view, savedInstanceState)

        adapter = ChallengeAdapter(
            mutableListOf(),
            { challenge -> showEditDialog(challenge) },
            {}
        )

        binding.rvChallenge.layoutManager = LinearLayoutManager(requireContext())
        binding.rvChallenge.adapter = adapter

        viewModel.listChallenge.observe(viewLifecycleOwner) { lista ->
            adapter.updateList(lista)
        }

        viewModel.getListChallenge()

        binding.btnBack.setOnClickListener {
            if (audioWasOn) {
                mainViewModel.resumeMusic()
            }
            findNavController().navigateUp()
        }

        binding.fabAdd.setOnClickListener {
            showAddDialog()
        }

    }

    /**
     * Obtiene los argumentos enviados al fragmento, incluyendo
     * el estado de la reproducción de música.
     *
     * @param savedInstanceState Estado previamente guardado del fragmento.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        audioWasOn = arguments?.getBoolean("audioWasOn") ?: false
    }

    /**
     * Libera la referencia del View Binding para evitar fugas de memoria.
     */
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    /**
     * Muestra el diálogo utilizado para registrar un nuevo reto.
     *
     * El botón de guardar permanece deshabilitado hasta que el usuario
     * ingrese una descripción válida.
     */
    private fun showAddDialog() {

        val view = layoutInflater.inflate(
            R.layout.dialog_add_challenge,
            null
        )

        val dialog = AlertDialog.Builder(requireContext())
            .setView(view)
            .create()

        dialog.setCanceledOnTouchOutside(false)

        val etChallenge = view.findViewById<EditText>(R.id.etChallenge)
        val btnSave = view.findViewById<Button>(R.id.btnSave)
        val btnCancel = view.findViewById<Button>(R.id.btnCancel)

        btnSave.isEnabled = false

        btnSave.backgroundTintList =
            ContextCompat.getColorStateList(
                requireContext(),
                R.color.light_grey
            )

        etChallenge.addTextChangedListener(object : TextWatcher {

            override fun beforeTextChanged(
                s: CharSequence?,
                start: Int,
                count: Int,
                after: Int
            ) {}

            override fun onTextChanged(
                s: CharSequence?,
                start: Int,
                before: Int,
                count: Int
            ) {
                val enable = !s.isNullOrBlank()
                btnSave.isEnabled = enable

                if (enable) {
                    btnSave.backgroundTintList =
                        ContextCompat.getColorStateList(
                            requireContext(),
                            R.color.orange
                        )
                } else {
                    btnSave.backgroundTintList =
                        ContextCompat.getColorStateList(
                            requireContext(),
                            R.color.light_grey
                        )
                }
            }

            override fun afterTextChanged(s: Editable?) {}

        })

        btnCancel.setOnClickListener {
            dialog.dismiss()
        }

        btnSave.setOnClickListener {

            val challenge = Challenge(
                description = etChallenge.text.toString()
            )

            viewModel.saveChallenge(challenge) { mensaje ->

                Toast.makeText(
                    requireContext(),
                    mensaje,
                    Toast.LENGTH_SHORT
                ).show()

                viewModel.getListChallenge()

                dialog.dismiss()
            }
        }

        dialog.show()
    }

    /**
     * Muestra el diálogo para editar un reto existente.
     *
     * @param challenge Reto que será modificado.
     */
    private fun showEditDialog(challenge: Challenge) {

        val view = layoutInflater.inflate(
            R.layout.dialog_edit_challenge,
            null
        )

        val dialog = AlertDialog.Builder(requireContext())
            .setView(view)
            .create()

        dialog.setCanceledOnTouchOutside(false)

        val etChallenge = view.findViewById<EditText>(R.id.etChallenge)
        etChallenge.setText(challenge.description)
        val btnSave = view.findViewById<Button>(R.id.btnSave)
        val btnCancel = view.findViewById<Button>(R.id.btnCancel)

        btnSave.isEnabled = false

        btnSave.backgroundTintList =
            ContextCompat.getColorStateList(
                requireContext(),
                R.color.light_grey
            )

        etChallenge.addTextChangedListener(object : TextWatcher {

            override fun beforeTextChanged(
                s: CharSequence?,
                start: Int,
                count: Int,
                after: Int
            ) {}

            override fun onTextChanged(
                s: CharSequence?,
                start: Int,
                before: Int,
                count: Int
            ) {
                val enable = !s.isNullOrBlank()
                btnSave.isEnabled = enable

                if (enable) {
                    btnSave.backgroundTintList =
                        ContextCompat.getColorStateList(
                            requireContext(),
                            R.color.orange
                        )
                } else {
                    btnSave.backgroundTintList =
                        ContextCompat.getColorStateList(
                            requireContext(),
                            R.color.light_grey
                        )
                }
            }

            override fun afterTextChanged(s: Editable?) {}

        })

        btnCancel.setOnClickListener {
            dialog.dismiss()
        }

        btnSave.setOnClickListener {

            val challenge = challenge.copy(
                description = etChallenge.text.toString()
            )

            viewModel.updateChallenge(challenge)

            viewModel.getListChallenge()

            dialog.dismiss()
        }

        dialog.show()
    }
}