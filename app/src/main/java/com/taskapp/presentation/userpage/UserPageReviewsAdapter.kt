package com.taskapp.presentation.userpage

import android.view.LayoutInflater
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.taskapp.R
import com.taskapp.databinding.ItemReviewBinding
import com.taskapp.domain.ReviewPageData

class UserPageReviewsAdapter(
    private var reviewPageDataLs: ArrayList<ReviewPageData>,
    private var listener: ReviewTaskClickInterface
) :
    RecyclerView.Adapter<UserPageReviewsAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        return ViewHolder(
            ItemReviewBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(
            reviewPageDataLs[position]
        )

    }

    override fun getItemCount(): Int = reviewPageDataLs.size

    inner class ViewHolder(private var item: ItemReviewBinding) :
        RecyclerView.ViewHolder(item.root) {
        fun bind(reviewPageData: ReviewPageData) {
            item.userNameTextView.text = reviewPageData.userName
            itemView.setOnClickListener {
                listener.onUserClick(layoutPosition)
            }
            if (reviewPageData.comment.isNullOrEmpty()) {
                item.commentTextView.visibility = GONE
            } else {
                item.commentTextView.text = reviewPageData.comment
                item.commentTextView.visibility = VISIBLE
            }
            item.ratingBar.rating = reviewPageData.rating
            if (reviewPageData.photo == null) {
                item.profilePictureImageView.setImageResource(R.drawable.profile_photo)
            } else {
                item.profilePictureImageView.setImageBitmap(reviewPageData.photo)
            }
        }
    }

    interface ReviewTaskClickInterface {
        fun onUserClick(index: Int)
    }
}