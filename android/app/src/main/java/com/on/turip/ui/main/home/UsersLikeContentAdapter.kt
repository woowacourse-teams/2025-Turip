package com.on.turip.ui.main.home

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.on.turip.domain.content.Content
import com.on.turip.domain.content.PopularFavoriteContent

class UsersLikeContentAdapter(
    private val onContentListener: (content: Content) -> Unit,
) : ListAdapter<PopularFavoriteContent, UsersLikeContentViewHolder>(UsersLikeContentDiffUtil) {
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

    private object UsersLikeContentDiffUtil : DiffUtil.ItemCallback<PopularFavoriteContent>() {
        override fun areItemsTheSame(
            oldItem: PopularFavoriteContent,
            newItem: PopularFavoriteContent,
        ): Boolean = oldItem.content.id == newItem.content.id

        override fun areContentsTheSame(
            oldItem: PopularFavoriteContent,
            newItem: PopularFavoriteContent,
        ): Boolean = oldItem == newItem
    }
}
