package com.example.amover.ui.detailtask

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.amover.R
import com.example.amover.databinding.FragmentDetailTaskBinding
import com.example.amover.model.TaskModel

class DetailTaskAdapter(
    private var list: List<TaskModel>,
    private val onItemClicked: (TaskModel) -> Unit
) : RecyclerView.Adapter<DetailTaskAdapter.DetailTaskViewHolder>() {

    class DetailTaskViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val binding = FragmentDetailTaskBinding.bind(view)

        fun bind(task: TaskModel) {
            //binding.taskIdText.text = task.id.toString()
            binding.taskNameText.text = task.name
            binding.taskAddressText.text = task.address
            //binding.taskContactoText.text = task.contacto
            binding.taskTimewindowText.text = task.timewindow
            binding.taskNoteText.text = task.note
            binding.taskTypeText.text = task.type
            binding.taskStatusText.text = task.status
            binding.taskLocalizaOText.text =
                task.latitude.toString() + ", " + task.longitude.toString()

            //binding.ruleImage.setImageResource(R.drawable.rule)

        }

    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DetailTaskViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.fragment_detail_task, parent, false)
        return DetailTaskViewHolder(view)

    }

    override fun onBindViewHolder(holder: DetailTaskViewHolder, position: Int) {
       val taskList = list[position]
        holder.bind(taskList)
    }

    override fun getItemCount(): Int = list.size

    fun updateList(newlist: List<TaskModel>) {
        list = newlist
        notifyDataSetChanged()
    }
}