package com.example.amover.ui.detail

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.amover.R
import com.example.amover.databinding.FragmentDetailBinding
import com.example.amover.model.TaskModel


class DetailAdapter (
    private var list: List<TaskModel>,
    private val onItemClicked: (TaskModel) -> Unit
) : RecyclerView.Adapter<DetailAdapter.TaskViewHolder>(){
    class TaskViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val binding = FragmentDetailBinding.bind(view)

        fun bind(task: TaskModel) {
            binding.taskIdText.text = task.id.toString()
            binding.taskAddressText.text = task.address
            binding.taskTypeText.text = task.type
            binding.taskNameText.text = task.name
            binding.taskTimewindowText.text = task.timewindow
            binding.circleTask.setImageResource(R.drawable.circle)
            binding.arrowImage.setImageResource(R.drawable.baseline_arrow)
            binding.ruleImage.setImageResource(R.drawable.rule)


        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DetailAdapter.TaskViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.fragment_detail, parent, false)
        return TaskViewHolder(view)
    }

    override fun onBindViewHolder(holder: DetailAdapter.TaskViewHolder, position: Int) {
        val taskList = list[position]
        holder.bind(taskList)
    }

    override fun getItemCount(): Int = list.size

    fun updateList(newlist: List<TaskModel>) {
        list = newlist
        notifyDataSetChanged()
    }
}


