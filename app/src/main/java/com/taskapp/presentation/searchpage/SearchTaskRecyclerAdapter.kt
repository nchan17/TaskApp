package com.taskapp.presentation.searchpage

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.taskapp.domain.Task
import com.taskapp.databinding.ItemSearchTaskCardBinding

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

    override fun getItemCount(): Int = taskList.size

    inner class ViewHolder(private var item: ItemSearchTaskCardBinding) :
        RecyclerView.ViewHolder(item.root) {
        fun bind(task: Task) {
            val date = task.creation_data?.date.toString() + "-" +
                    task.creation_data?.month.toString() + "-" +
                    task.creation_data?.year?.plus(1900).toString()

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