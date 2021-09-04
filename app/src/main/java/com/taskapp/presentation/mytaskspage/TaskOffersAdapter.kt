package com.taskapp.presentation.mytaskspage

import android.graphics.Bitmap
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.taskapp.databinding.ItemOfferUserBinding
import com.taskapp.domain.User


class TaskOffersAdapter(
    private var userList: ArrayList<User>,
    private var userPicList: ArrayList<Bitmap>,
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
        if (position >= userPicList.size) {
            holder.bind(
                userList[position], null
            )
        } else {
            holder.bind(
                userList[position], userPicList[position]
            )
        }

    }

    override fun getItemCount(): Int = userList.size

    inner class ViewHolder(private var item: ItemOfferUserBinding) :
        RecyclerView.ViewHolder(item.root) {
        fun bind(user: User, bitmap: Bitmap?) {
            item.employerNameTextView.text = user.fullName
            item.userConstraintLayout.setOnClickListener {
                listener.onUserClick(layoutPosition)
            }
            item.chooseButton.setOnClickListener {
                listener.onAcceptOfferClick(layoutPosition)
            }
            bitmap?.let { item.profilePictureImageView.setImageBitmap(it) }
        }
    }

    interface TaskOfferClickInterface {
        fun onUserClick(index: Int)
        fun onAcceptOfferClick(index: Int)
    }
}