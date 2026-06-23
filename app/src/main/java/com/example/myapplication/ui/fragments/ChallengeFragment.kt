package com.example.myapplication.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myapplication.adapter.ChallengeAdapter
import com.example.myapplication.databinding.FragmentChallengeBinding
import com.example.myapplication.model.Challenge

class ChallengeFragment : Fragment() {

    private var _binding: FragmentChallengeBinding? = null
    private val binding get() = _binding!!

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
        Toast.makeText(
            requireContext(),
            "ESTOY EN CHALLENGE",
            Toast.LENGTH_LONG
        ).show()
        super.onViewCreated(view, savedInstanceState)

        val retosPrueba = listOf(
            Challenge(
                id = 1,
                description = "Baila durante 30 segundos"
            ),
            Challenge(
                id = 2,
                description = "Canta una canción"
            ),
            Challenge(
                id = 3,
                description = "Haz 10 sentadillas"
            )
        )

        binding.rvChallenge.layoutManager =
            LinearLayoutManager(requireContext())

        binding.rvChallenge.adapter =
            ChallengeAdapter(retosPrueba)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}