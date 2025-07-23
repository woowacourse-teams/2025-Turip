package com.on.turip.ui.search.result

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.on.turip.domain.contents.Video

class SearchResultAdapter(
    private val onSearchResultListener: SearchResultViewHolder.OnSearchResultListener,
) : ListAdapter<Video, SearchResultViewHolder>(VideosDiffUtil) {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): SearchResultViewHolder = SearchResultViewHolder.of(parent, onSearchResultListener)

    override fun onBindViewHolder(
        holder: SearchResultViewHolder,
        position: Int,
    ) {
        holder.bind(getItem(position))
    }
}

private object VideosDiffUtil : DiffUtil.ItemCallback<Video>() {
    override fun areItemsTheSame(
        oldItem: Video,
        newItem: Video,
    ): Boolean = oldItem.content.id == newItem.content.id

    override fun areContentsTheSame(
        oldItem: Video,
        newItem: Video,
    ): Boolean = oldItem == newItem
}
