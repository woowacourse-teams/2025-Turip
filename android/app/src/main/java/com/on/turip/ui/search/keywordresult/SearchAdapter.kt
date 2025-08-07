package com.on.turip.ui.search.keywordresult

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import com.on.turip.ui.search.model.VideoInformationModel

class SearchAdapter(
    private val onSearchResultListener: SearchViewHolder.OnSearchResultListener,
) : androidx.recyclerview.widget.ListAdapter<VideoInformationModel, SearchViewHolder>(
        VideoInformationDiffUtil,
    ) {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): SearchViewHolder = SearchViewHolder.of(parent, onSearchResultListener)

    override fun onBindViewHolder(
        holder: SearchViewHolder,
        position: Int,
    ) {
        holder.bind(getItem(position))
    }

    private object VideoInformationDiffUtil : DiffUtil.ItemCallback<VideoInformationModel>() {
        override fun areItemsTheSame(
            oldItem: VideoInformationModel,
            newItem: VideoInformationModel,
        ): Boolean = oldItem.content.id == newItem.content.id

        override fun areContentsTheSame(
            oldItem: VideoInformationModel,
            newItem: VideoInformationModel,
        ): Boolean = oldItem == newItem
    }
}
