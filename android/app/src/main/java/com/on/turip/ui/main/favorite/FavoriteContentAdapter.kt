package com.on.turip.ui.main.favorite

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.on.turip.domain.favorite.FavoriteContent

class FavoriteContentAdapter(
    private val onFavoriteContentListener: FavoriteContentViewHolder.FavoriteContentListener,
) : ListAdapter<FavoriteContent, FavoriteContentViewHolder>(FavoriteContentDiffUtil) {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): FavoriteContentViewHolder = FavoriteContentViewHolder.of(parent, onFavoriteContentListener)

    override fun onBindViewHolder(
        holder: FavoriteContentViewHolder,
        position: Int,
    ) {
        holder.bind(getItem(position))
    }

    private object FavoriteContentDiffUtil : DiffUtil.ItemCallback<FavoriteContent>() {
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
