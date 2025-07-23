package com.on.turip.ui.search.result

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.on.turip.domain.contents.Video

class VideosAdapter(
    private val onSearchResultListener: VideosViewHolder.OnSearchResultListener,
) : ListAdapter<Video, VideosViewHolder>(VideosDiffUtil) {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): VideosViewHolder = VideosViewHolder.of(parent, onSearchResultListener)

    override fun onBindViewHolder(
        holder: VideosViewHolder,
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
