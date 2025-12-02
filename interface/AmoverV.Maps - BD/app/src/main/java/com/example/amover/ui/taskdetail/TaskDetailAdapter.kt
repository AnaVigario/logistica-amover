package com.example.amover.ui.taskdetail

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.recyclerview.widget.RecyclerView
import com.example.amover.R
import com.example.amover.databinding.FragmentDetailBinding
import com.example.amover.databinding.FragmentTaskDetailBinding
import com.example.amover.model.TaskModel
import com.example.amover.ui.detail.DetailAdapter
import com.example.amover.ui.detail.DetailAdapter.TaskViewHolder

class TaskDetailAdapter(
    private var list: List<TaskModel>,
    private val onItemClicked: (TaskModel) -> Unit
) : RecyclerView.Adapter<TaskDetailAdapter.TaskViewHolder>() {

    inner class TaskViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val binding = FragmentTaskDetailBinding.bind(view)

        fun bind(task: TaskModel) {
            //binding.taskIdText.text = task.id.toString()
            binding.taskNameText.text = task.name
            binding.taskAddressText.text = task.address
            binding.taskTimewindowText.text = task.timewindow
            binding.taskNoteText.text = task.note
            binding.taskTypeText.text = task.type
            binding.taskStatusText.text = task.status
            binding.taskLatitudeText.text = task.latitude.toString()
            binding.taskLongitudeText.text = task.longitude.toString()
            //binding.ruleImage.setImageResource(R.drawable.rule)

            binding.btnStart.setOnClickListener {
                Toast.makeText(binding.root.context, "Iniciar", Toast.LENGTH_SHORT).show()
                //findNavController().navigate(R.id.action_taskDetailFragment_to_navigation_map)
            }
        }

    }

    override fun getItemCount(): Int = list.size

    fun updateList(newlist: List<TaskModel>) {
        list = newlist
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): TaskDetailAdapter.TaskViewHolder {
        val view =
            LayoutInflater.from(parent.context)
                .inflate(R.layout.fragment_task_detail , parent, false)
        return TaskViewHolder(view)

    }

    override fun onBindViewHolder(holder: TaskDetailAdapter.TaskViewHolder, position: Int) {
        val taskList = list[position]
        holder.bind(taskList)
    }
}