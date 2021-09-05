package com.taskapp.presentation.searchpage

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.taskapp.R
import com.taskapp.domain.Task
import com.taskapp.databinding.ItemSearchTaskCardBinding
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class SearchTaskRecyclerAdapter(
    private var taskList: ArrayList<Task>,
    private var listener: SearchTaskClickInterface
) :
    RecyclerView.Adapter<SearchTaskRecyclerAdapter.ViewHolder>() {

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
            taskList[position]
        )
    }

    fun setNewTaskList(taskList: ArrayList<Task>){
        this.taskList = taskList
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int = taskList.size

    inner class ViewHolder(private var item: ItemSearchTaskCardBinding) :
        RecyclerView.ViewHolder(item.root) {
        fun bind(task: Task) {
            Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
            task.creation_data?.let {
                val mCalendar = Calendar.getInstance()
                mCalendar.time = it
                val date =
                    SimpleDateFormat("d MMM yyyy", Locale.getDefault()).format(mCalendar.time)
                item.dateTextView.text = date
            }

            item.titleTextView.text = task.title
            item.descriptionTextView.text = task.description
            item.priceTextView.text =
                itemView.context.getString(
                    R.string.search_task_adapter_gel_text,
                    task.price.toString()
                )
            itemView.setOnClickListener {
                listener.onItemClick(layoutPosition)
            }
        }
    }

    interface SearchTaskClickInterface {
        fun onItemClick(index: Int)
    }
}