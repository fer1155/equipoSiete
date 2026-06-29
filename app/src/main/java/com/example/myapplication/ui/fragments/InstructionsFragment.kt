package com.example.myapplication.ui.fragments

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import com.example.myapplication.databinding.FragmentInstructionsBinding
import com.example.myapplication.utils.AnimationUtils
import com.example.myapplication.R
import androidx.fragment.app.activityViewModels
import com.example.myapplication.model.Challenge
import com.example.myapplication.ui.viewmodels.MainViewModel
import androidx.fragment.app.viewModels
import com.example.myapplication.ui.viewmodels.ChallengeViewModel

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [InstructionsFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class InstructionsFragment : Fragment() {

    private var _binding: FragmentInstructionsBinding? = null
    private val binding get() = _binding!!

    private var audioWasOn = false
    private val MainViewModel: MainViewModel by activityViewModels()

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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        audioWasOn = arguments?.getBoolean("audioWasOn") ?: false
    }
}