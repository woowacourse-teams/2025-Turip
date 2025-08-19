package com.on.turip.ui.search.regionresult

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.on.turip.ui.search.model.VideoInformationModel

class RegionResultAdapter(
    private val onSearchResultListener: RegionResultViewHolder.OnSearchResultListener,
) : ListAdapter<VideoInformationModel, RegionResultViewHolder>(VideoInformationDiffUtil) {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): RegionResultViewHolder = RegionResultViewHolder.of(parent, onSearchResultListener)

    override fun onBindViewHolder(
        holder: RegionResultViewHolder,
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
