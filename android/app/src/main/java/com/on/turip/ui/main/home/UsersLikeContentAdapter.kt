package com.on.turip.ui.main.home

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.on.turip.domain.content.Content

class UsersLikeContentAdapter(
    private val onContentListener: (content: Content) -> Unit,
) : ListAdapter<UsersLikeContentModel, UsersLikeContentViewHolder>(UsersLikeContentDiffUtil) {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): UsersLikeContentViewHolder = UsersLikeContentViewHolder.of(parent, onContentListener)

    override fun onBindViewHolder(
        holder: UsersLikeContentViewHolder,
        position: Int,
    ) {
        holder.bind(getItem(position))
    }

    private object UsersLikeContentDiffUtil : DiffUtil.ItemCallback<UsersLikeContentModel>() {
        override fun areItemsTheSame(
            oldItem: UsersLikeContentModel,
            newItem: UsersLikeContentModel,
        ): Boolean = oldItem.content.id == newItem.content.id

        override fun areContentsTheSame(
            oldItem: UsersLikeContentModel,
            newItem: UsersLikeContentModel,
        ): Boolean = oldItem == newItem
    }
}
