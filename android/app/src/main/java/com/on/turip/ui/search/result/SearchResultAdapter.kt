package com.on.turip.ui.search.result

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.on.turip.ui.search.model.VideoInformationModel

class SearchResultAdapter(
    private val onSearchResultListener: SearchResultViewHolder.OnSearchResultListener,
) : ListAdapter<VideoInformationModel, SearchResultViewHolder>(VideoInformationDiffUtil) {
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
