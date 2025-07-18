package com.example.amover.ui.taskscheck

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.amover.R
import com.example.amover.databinding.FragmentTasksCheckBinding
import com.example.amover.ui.database.DBHelper


class TasksCheckFragment : Fragment() {


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_tasks_check, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = FragmentTasksCheckBinding.bind(view)

        binding.recyclerviewCheckTasks.layoutManager = LinearLayoutManager(requireContext())
        val db = DBHelper(requireContext())
        val taskcheckList = db.getCheckTasks()
        binding.recyclerviewCheckTasks.adapter = TasksCheckAdapter(taskcheckList)
    }
}