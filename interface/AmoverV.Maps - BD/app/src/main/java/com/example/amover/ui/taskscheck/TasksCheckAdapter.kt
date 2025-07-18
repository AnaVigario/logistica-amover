package com.example.amover.ui.taskscheck

import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.amover.R
import com.example.amover.databinding.FragmentDetailBinding
import com.example.amover.model.TaskModel
import java.time.LocalDate.parse
import java.time.format.DateTimeFormatter.ofPattern

class TasksCheckAdapter( private var checklist: List<TaskModel>) : RecyclerView.Adapter<TasksCheckAdapter.TaskViewHolder>() {
    class TaskViewHolder(view: View) : RecyclerView.ViewHolder(view){
        val binding = FragmentDetailBinding.bind(view)

        fun bind(taskcheck: TaskModel){
            binding.taskIdText.text = taskcheck.id.toString()
            binding.taskAddressText.text = taskcheck.address
            binding.taskTypeText.text = taskcheck.type
            binding.taskNameText.text = taskcheck.name
            binding.taskTimewindowText.text = taskcheck.timewindow
            binding.circleTask.setImageResource(R.drawable.circle)
            binding.arrowImage.setImageResource(R.drawable.baseline_arrow)
            binding.ruleImage.setImageResource(R.drawable.rule)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.fragment_detail, parent, false)
        return TaskViewHolder(view)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        val taskcheckList = checklist[position]
        holder.bind(taskcheckList)
    }

    override fun getItemCount(): Int = checklist.size

    fun updateList(newlist: List<TaskModel>) {
        val formatter = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            ofPattern("yyyy-MM-dd")
        } else {
            TODO("VERSION.SDK_INT < O")
        }
        checklist = newlist.sortedBy {
            parse(it.dateTask, formatter)
        }
        notifyDataSetChanged()

    }

}