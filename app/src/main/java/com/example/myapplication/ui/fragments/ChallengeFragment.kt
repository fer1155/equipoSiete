package com.example.myapplication.ui.fragments

import androidx.fragment.app.viewModels
import androidx.appcompat.app.AlertDialog
import android.text.Editable
import android.text.TextWatcher
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
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
 * retos, editarlos o eliminarlos. Además, controla la pausa y reanudación
 * de la música de fondo al entrar o salir de esta pantalla.
 */
class ChallengeFragment : Fragment() {

    /** Binding para acceder a las vistas del fragmento. */
    private var _binding: FragmentChallengeBinding? = null
    private val binding get() = _binding!!

    /** ViewModel encargado de la lógica de gestión de retos. */
    private val viewModel: ChallengeViewModel by viewModels()
    
    /** Adaptador para el RecyclerView que muestra la lista de retos. */
    private lateinit var adapter: ChallengeAdapter

    /** Variable para recordar si el audio estaba encendido antes de entrar. */
    private var audioWasOn = false
    
    /** ViewModel compartido para controlar el estado del audio global. */
    private val mainViewModel: MainViewModel by activityViewModels()

    /**
     * Infla el layout del fragmento e inicializa el View Binding.
     */
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentChallengeBinding.inflate(inflater, container, false)
        return binding.root
    }

    /**
     * Configura el RecyclerView, los observadores de LiveData y los listeners
     * de los botones una vez que la vista ha sido creada.
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        // Pausar música si estaba activa según los requerimientos (Criterio 1 de HU 6.0)
        if (audioWasOn) {
            mainViewModel.pauseMusic()
        }

        super.onViewCreated(view, savedInstanceState)

        // Inicializar adaptador con las acciones de editar y eliminar
        adapter = ChallengeAdapter(
            mutableListOf(),
            { challenge -> showEditDialog(challenge) },
            { challenge -> showDeleteDialog(challenge) }
        )

        binding.rvChallenge.layoutManager = LinearLayoutManager(requireContext())
        binding.rvChallenge.adapter = adapter

        // Observar cambios en la lista de retos para actualizar la UI
        viewModel.listChallenge.observe(viewLifecycleOwner) { lista ->
            adapter.updateList(lista)
        }

        // Cargar lista inicial
        viewModel.getListChallenge()

        // Botón atrás: reanuda audio si es necesario y navega al Home
        binding.btnBack.setOnClickListener {
            if (audioWasOn) {
                mainViewModel.resumeMusic()
            }
            findNavController().navigateUp()
        }

        // Botón flotante para agregar reto
        binding.fabAdd.setOnClickListener {
            showAddDialog()
        }
    }

    /**
     * Recupera argumentos iniciales, como el estado del audio.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        audioWasOn = arguments?.getBoolean("audioWasOn") ?: false
    }

    /**
     * Limpia el binding al destruir la vista.
     */
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    /**
     * Muestra el diálogo para agregar un nuevo reto (HU 7.0).
     * Incluye validación del botón guardar (solo se habilita si hay texto).
     */
    private fun showAddDialog() {
        val view = layoutInflater.inflate(R.layout.dialog_add_challenge, null)
        val dialog = AlertDialog.Builder(requireContext()).setView(view).create()
        dialog.setCanceledOnTouchOutside(false)

        val etChallenge = view.findViewById<EditText>(R.id.etChallenge)
        val btnSave = view.findViewById<Button>(R.id.btnSave)
        val btnCancel = view.findViewById<Button>(R.id.btnCancel)

        btnSave.isEnabled = false
        btnSave.backgroundTintList = ContextCompat.getColorStateList(requireContext(), R.color.light_grey)

        etChallenge.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val enable = !s.isNullOrBlank()
                btnSave.isEnabled = enable
                btnSave.backgroundTintList = ContextCompat.getColorStateList(
                    requireContext(), if (enable) R.color.orange else R.color.light_grey
                )
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        btnCancel.setOnClickListener { dialog.dismiss() }

        btnSave.setOnClickListener {
            val challenge = Challenge(description = etChallenge.text.toString())
            viewModel.saveChallenge(challenge) { mensaje ->
                Toast.makeText(requireContext(), mensaje, Toast.LENGTH_SHORT).show()
                dialog.dismiss()
            }
        }
        dialog.show()
    }

    /**
     * Muestra el diálogo para editar un reto existente (HU 8.0).
     * @param challenge El reto a editar.
     */
    private fun showEditDialog(challenge: Challenge) {
        val view = layoutInflater.inflate(R.layout.dialog_edit_challenge, null)
        val dialog = AlertDialog.Builder(requireContext()).setView(view).create()
        dialog.setCanceledOnTouchOutside(false)

        val etChallenge = view.findViewById<EditText>(R.id.etChallenge)
        etChallenge.setText(challenge.description)
        val btnSave = view.findViewById<Button>(R.id.btnSave)
        val btnCancel = view.findViewById<Button>(R.id.btnCancel)

        btnSave.isEnabled = false
        btnSave.backgroundTintList = ContextCompat.getColorStateList(requireContext(), R.color.light_grey)

        etChallenge.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val enable = !s.isNullOrBlank()
                btnSave.isEnabled = enable
                btnSave.backgroundTintList = ContextCompat.getColorStateList(
                    requireContext(), if (enable) R.color.orange else R.color.light_grey
                )
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        btnCancel.setOnClickListener { dialog.dismiss() }

        btnSave.setOnClickListener {
            val updatedChallenge = challenge.copy(description = etChallenge.text.toString())
            viewModel.updateChallenge(updatedChallenge) {
                dialog.dismiss()
            }
        }
        dialog.show()
    }

    /**
     * Muestra el diálogo de confirmación para eliminar un reto (HU 9.0).
     * @param challenge El reto a eliminar.
     */
    private fun showDeleteDialog(challenge: Challenge) {
        val view = layoutInflater.inflate(R.layout.dialog_delete_challenge, null)
        val dialog = AlertDialog.Builder(requireContext()).setView(view).create()
        dialog.setCanceledOnTouchOutside(false)

        val txtDescription = view.findViewById<TextView>(R.id.txtDescription)
        txtDescription.text = challenge.description
        val btnNo = view.findViewById<TextView>(R.id.btnNo)
        val btnYes = view.findViewById<TextView>(R.id.btnYes)

        btnNo.setOnClickListener { dialog.dismiss() }

        btnYes.setOnClickListener {
            viewModel.deleteChallenge(challenge) {
                dialog.dismiss()
            }
        }
        dialog.show()
    }
}