package com.on.turip.ui.main.favorite

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.on.turip.ui.search.model.VideoInformationModel

class FavoriteItemAdapter(
    private val onFavoriteItemListener: FavoriteItemViewHolder.FavoriteItemListener,
) : ListAdapter<VideoInformationModel, FavoriteItemViewHolder>(FavoriteItemDiffUtil) {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): FavoriteItemViewHolder = FavoriteItemViewHolder.of(parent, onFavoriteItemListener)

    override fun onBindViewHolder(
        holder: FavoriteItemViewHolder,
        position: Int,
    ) {
        val videoInformationModel: VideoInformationModel = getItem(position)
        holder.bind(videoInformationModel)
    }

    private object FavoriteItemDiffUtil : DiffUtil.ItemCallback<VideoInformationModel>() {
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
