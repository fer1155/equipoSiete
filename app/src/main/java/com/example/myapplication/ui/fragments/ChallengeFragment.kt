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
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myapplication.R
import com.example.myapplication.adapter.ChallengeAdapter
import com.example.myapplication.databinding.FragmentChallengeBinding
import com.example.myapplication.model.Challenge

class ChallengeFragment : Fragment() {

    private var _binding: FragmentChallengeBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ChallengeViewModel by viewModels()
    private lateinit var adapter: ChallengeAdapter

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

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?
    ) {
        
        super.onViewCreated(view, savedInstanceState)

        adapter = ChallengeAdapter(
            mutableListOf(),
            {},
            {}
        )

        binding.rvChallenge.layoutManager = LinearLayoutManager(requireContext())
        binding.rvChallenge.adapter = adapter

        viewModel.listChallenge.observe(viewLifecycleOwner){ lista ->
            adapter.updateList(lista)
        }

        viewModel.getListChallenge()

        binding.btnBack.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.fabAdd.setOnClickListener {
            showAddDialog()
        }


    }



    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

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

                if (enable){
                    btnSave.backgroundTintList=
                        ContextCompat.getColorStateList(
                            requireContext(),
                            R.color.orange
                        )
                }else {
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


}