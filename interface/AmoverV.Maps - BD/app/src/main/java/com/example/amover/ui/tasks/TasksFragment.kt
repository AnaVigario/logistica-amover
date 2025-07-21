package com.example.amover.ui.tasks

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.amover.R
import com.example.amover.databinding.FragmentTasksBinding
import com.example.amover.ui.database.DBHelper


class TasksFragment : Fragment() {
    private var _binding: FragmentTasksBinding? = null
    private val binding get() = _binding!!

    private lateinit var taskViewModel: TasksViewModel
    //private lateinit var taskAdapter: TaskAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        taskViewModel = ViewModelProvider(this).get(TasksViewModel::class.java)
        //taskAdapter = TaskAdapter(emptyList())
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_tasks, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //binding.recyclerviewTasks.layoutManager = LinearLayoutManager(requireContext())
        //binding.recyclerviewTasks.adapter = taskAdapter

        val binding = FragmentTasksBinding.bind(view)
        binding.recyclerviewTasks.layoutManager = LinearLayoutManager(requireContext())

        val db = DBHelper(requireContext())
        val taskcheckList = db.getTasks()

        binding.recyclerviewTasks.adapter = TaskAdapter(taskcheckList) { task ->
            val bundle = Bundle().apply {
                putInt("id", task.id)

            }
            findNavController().navigate(R.id.action_navigation_tasks_to_taskDetailFragment, bundle)
            Toast.makeText(requireContext(), "ID: ${task.name}", Toast.LENGTH_SHORT).show()

        }

    }
override fun onDestroyView() {
    super.onDestroyView()
    _binding = null
}
    }

