package com.example.amover.ui.taskdetail

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.amover.R
import com.example.amover.databinding.FragmentTaskDetailBinding
import com.example.amover.databinding.FragmentTasksBinding
import com.example.amover.ui.database.DBHelper
import com.example.amover.ui.detail.DetailViewModel
import com.example.amover.ui.tasks.TaskAdapter


class TaskDetailFragment : Fragment() {
    private var _binding: FragmentTaskDetailBinding? = null

    private val binding get() = _binding!!

    private lateinit var detailViewModel: DetailViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        detailViewModel = ViewModelProvider(this).get(DetailViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val taskDetailViewModel = ViewModelProvider(this).get(TaskDetailViewModel::class.java)

        _binding = FragmentTaskDetailBinding.inflate(inflater, container, false)
        val root: View = binding.root

        binding.btnStart.setOnClickListener {
            findNavController().navigate(R.id.action_taskDetailFragment_to_navigation_map)
        }

        binding.btnComplete.setOnClickListener {
            findNavController().navigate(R.id.action_taskDetailFragment_to_confirmationFragment)
        }


        return root
        // Inflate the layout for this fragment
        //return inflater.inflate(R.layout.fragment_task_detail, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


       val taskId = arguments?.getInt("taskId")?:0

        val db = DBHelper(requireContext())
        val taskcheckList = db.getTasks()

        val task = taskcheckList.find{it.id == taskId}?: return
        if(task == null){
            Toast.makeText(requireContext(), "Task não encontrada", Toast.LENGTH_SHORT).show()
            return
        }else {
            Toast.makeText(requireContext(), "Task encontrada", Toast.LENGTH_SHORT).show()
            binding.taskNameText.text = task.name
            binding.taskAddressText.text = task.address
            binding.taskTimewindowText.text = task.timewindow
            binding.taskNoteText.text = task.note
            binding.taskTypeText.text = task.type
            binding.taskStatusText.text = task.status
            binding.taskLatitudeText.text = task.latitude.toString()
            binding.taskLongitudeText.text = task.longitude.toString()
        }



    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null

    }
}