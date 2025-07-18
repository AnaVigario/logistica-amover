package com.example.amover.ui.home

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.amover.LoginActivity
import com.example.amover.R
import com.example.amover.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val homeViewModel =
            ViewModelProvider(this).get(HomeViewModel::class.java)

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        binding.buttonlogout.setOnClickListener {
            val intent = Intent(requireActivity(), LoginActivity::class.java)
            startActivity(intent)
        }

        binding.buttondelivery.setOnClickListener {
            findNavController().navigate(R.id.action_navigation_home_to_navigation_tasks)
        }

        binding.buttondeliverycheck.setOnClickListener {
            findNavController().navigate(R.id.action_navigation_home_to_tasksCheckFragment)
        }

        binding.buttonperfil.setOnClickListener {
            findNavController().navigate(R.id.action_navigation_home_to_perfilFragment)
        }
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}