package com.taskapp.presentation.searchpage

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.taskapp.core.domain.Task
import com.taskapp.databinding.ItemSearchTaskCardBinding
import java.text.SimpleDateFormat

class SearchTaskRecyclerAdapter(taskList: ArrayList<Task>, listener: SearchTaskClickInterface) :
    RecyclerView.Adapter<SearchTaskRecyclerAdapter.ViewHolder>() {
    private var taskLs: ArrayList<Task> = taskList
    private var listener: SearchTaskClickInterface = listener


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        return ViewHolder(
            ItemSearchTaskCardBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(
            taskLs[position]
        )
    }

    override fun getItemCount(): Int = taskLs.size

    inner class ViewHolder(private var item: ItemSearchTaskCardBinding) :
        RecyclerView.ViewHolder(item.root) {
        fun bind(task: Task) {
            val date =
                task.creation_data?.date.toString() + "-" + task.creation_data?.month.toString() + "-" + task.creation_data?.year?.plus(
                    1900
                )
                    .toString()
//            val date =
//            SimpleDateFormat("dd-mm-yyyy").format(task.creation_data?.time)
            item.dateTextView.text = date
            item.titleTextView.text = task.title
            item.descriptionTextView.text = task.description
            item.priceTextView.text = task.price.toString() + " ₾"
            itemView.setOnClickListener {
                listener.onItemClick(layoutPosition)
            }
        }
    }

    interface SearchTaskClickInterface {
        fun onItemClick(index: Int)
    }
}