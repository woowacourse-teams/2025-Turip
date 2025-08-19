package com.on.turip.ui.main.favorite

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.on.turip.domain.favorite.FavoriteContent

class FavoriteItemAdapter(
    private val onFavoriteItemListener: FavoriteItemViewHolder.FavoriteItemListener,
) : ListAdapter<FavoriteContent, FavoriteItemViewHolder>(FavoriteItemDiffUtil) {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): FavoriteItemViewHolder = FavoriteItemViewHolder.of(parent, onFavoriteItemListener)

    override fun onBindViewHolder(
        holder: FavoriteItemViewHolder,
        position: Int,
    ) {
        holder.bind(getItem(position))
    }

    private object FavoriteItemDiffUtil : DiffUtil.ItemCallback<FavoriteContent>() {
        override fun areItemsTheSame(
            oldItem: FavoriteContent,
            newItem: FavoriteContent,
        ): Boolean = oldItem.content.id == newItem.content.id

        override fun areContentsTheSame(
            oldItem: FavoriteContent,
            newItem: FavoriteContent,
        ): Boolean = oldItem == newItem
    }
}
