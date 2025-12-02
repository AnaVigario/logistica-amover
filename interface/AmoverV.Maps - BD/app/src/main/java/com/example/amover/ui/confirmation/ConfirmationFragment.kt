package com.example.amover.ui.confirmation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.amover.databinding.FragmentConfirmationBinding


class ConfirmationFragment : Fragment() {

    private var _binding: FragmentConfirmationBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val confirmationViewModel =
            ViewModelProvider(this).get(ConfirmationViewModel::class.java)

        _binding = FragmentConfirmationBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val textView: TextView = binding.textConfirmation
        confirmationViewModel.text.observe(viewLifecycleOwner) {
            textView.text = it
        }
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}