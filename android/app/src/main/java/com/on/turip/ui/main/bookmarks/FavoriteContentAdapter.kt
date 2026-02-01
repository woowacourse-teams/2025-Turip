package com.on.turip.ui.main.bookmarks

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.on.turip.domain.bookmark.BookmarkContent

class FavoriteContentAdapter(
    private val onFavoriteContentListener: FavoriteContentViewHolder.FavoriteContentListener,
) : ListAdapter<BookmarkContent, FavoriteContentViewHolder>(FavoriteContentDiffUtil) {
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

    private object FavoriteContentDiffUtil : DiffUtil.ItemCallback<BookmarkContent>() {
        override fun areItemsTheSame(
            oldItem: BookmarkContent,
            newItem: BookmarkContent,
        ): Boolean = oldItem.content.id == newItem.content.id

        override fun areContentsTheSame(
            oldItem: BookmarkContent,
            newItem: BookmarkContent,
        ): Boolean = oldItem == newItem
    }
}
