package com.on.turip.ui.search.result

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.on.turip.domain.content.video.VideoInformation

class SearchResultAdapter(
    private val onSearchResultListener: SearchResultViewHolder.OnSearchResultListener,
) : ListAdapter<VideoInformation, SearchResultViewHolder>(VideoInformationDiffUtil) {
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

    private object VideoInformationDiffUtil : DiffUtil.ItemCallback<VideoInformation>() {
        override fun areItemsTheSame(
            oldItem: VideoInformation,
            newItem: VideoInformation,
        ): Boolean = oldItem.content.id == newItem.content.id

        override fun areContentsTheSame(
            oldItem: VideoInformation,
            newItem: VideoInformation,
        ): Boolean = oldItem == newItem
    }
}
