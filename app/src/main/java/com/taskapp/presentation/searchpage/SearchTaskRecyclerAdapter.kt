package com.taskapp.presentation.searchpage

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.taskapp.core.domain.Task
import com.taskapp.databinding.ItemSearchTaskCardBinding
import java.text.SimpleDateFormat

class SearchTaskRecyclerAdapter(taskList: ArrayList<Task>) :
    RecyclerView.Adapter<SearchTaskRecyclerAdapter.ViewHolder>() {
    private var list: ArrayList<Task> = taskList

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
            list[position]
        )
    }

    override fun getItemCount(): Int = list.size

    inner class ViewHolder(private var item: ItemSearchTaskCardBinding) :
        RecyclerView.ViewHolder(item.root) {
        fun bind(task: Task) {
            val date =
                SimpleDateFormat("dd-mm-yyyy").format(task.creation_data?.time) //araswor TariRebs aCvenebs
            item.dateTextView.text = date
            item.titleTextView.text = task.title
            item.descriptionTextView.text = task.description
            item.priceTextView.text = task.price.toString() + " ₾"
        }
    }
}