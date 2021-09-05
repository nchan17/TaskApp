package com.taskapp.presentation.mytaskspage

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.taskapp.R
import com.taskapp.databinding.ItemOfferUserBinding
import com.taskapp.domain.TaskOfferPageData


class TaskOffersAdapter(
    private var taskOfferPageDataLs: ArrayList<TaskOfferPageData>,
    private var listener: TaskOfferClickInterface
) :
    RecyclerView.Adapter<TaskOffersAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        return ViewHolder(
            ItemOfferUserBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(
            taskOfferPageDataLs[position]
        )

    }

    override fun getItemCount(): Int = taskOfferPageDataLs.size

    inner class ViewHolder(private var item: ItemOfferUserBinding) :
        RecyclerView.ViewHolder(item.root) {
        fun bind(taskOfferPageData: TaskOfferPageData) {
            item.employerNameTextView.text = taskOfferPageData.userName
            item.userConstraintLayout.setOnClickListener {
                listener.onUserClick(layoutPosition)
            }
            item.chooseButton.setOnClickListener {
                listener.onAcceptOfferClick(layoutPosition)
            }
            if (taskOfferPageData.photo == null) {
                item.profilePictureImageView.setImageResource(R.drawable.profile_photo)
            } else {
                item.profilePictureImageView.setImageBitmap(taskOfferPageData.photo!!)
            }
            item.ratingBar.rating = taskOfferPageData.rating
        }
    }

    interface TaskOfferClickInterface {
        fun onUserClick(index: Int)
        fun onAcceptOfferClick(index: Int)
    }
}