package com.example.amover.ui.detailtask

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.amover.R
import com.example.amover.databinding.FragmentDetailTaskBinding
import com.example.amover.ui.database.DBHelper

class DetailTaskFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var _binding: FragmentDetailTaskBinding? = null
    private val binding get() = _binding!!

    //private lateinit var taskViewModel: DetailTaskViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //taskViewModel = ViewModelProvider(this).get(DetailTaskViewModel::class.java)

    }

    override fun onCreateView(
        inflater: LayoutInflater, 
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_detail_task, container, false)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null

    }
}

