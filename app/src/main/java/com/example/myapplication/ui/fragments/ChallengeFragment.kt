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

class ChallengeFragment : Fragment() {

    private var _binding: FragmentChallengeBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ChallengeViewModel by viewModels()
    private lateinit var adapter: ChallengeAdapter

    private var audioWasOn = false
    private val mainViewModel: MainViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentChallengeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        if (audioWasOn) {
            mainViewModel.pauseMusic()
        }

        super.onViewCreated(view, savedInstanceState)

        adapter = ChallengeAdapter(
            mutableListOf(),
            { challenge -> showEditDialog(challenge) },
            { challenge -> showDeleteDialog(challenge) }
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        audioWasOn = arguments?.getBoolean("audioWasOn") ?: false
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

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