package com.on.turip.ui.main.favorite

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.on.turip.ui.main.favorite.model.FavoritePlaceFolderModel

class FavoritePlaceFolderNameAdapter(
    private val onPlaceFolderNameListener: FavoritePlaceFolderNameViewHolder.OnPlaceFolderNameListener,
) : ListAdapter<FavoritePlaceFolderModel, FavoritePlaceFolderNameViewHolder>(
        FavoritePlaceFolderDiffUtil,
    ) {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): FavoritePlaceFolderNameViewHolder = FavoritePlaceFolderNameViewHolder.of(parent, onPlaceFolderNameListener)

    override fun onBindViewHolder(
        holder: FavoritePlaceFolderNameViewHolder,
        position: Int,
    ) {
        holder.bind(getItem(position))
    }

    private object FavoritePlaceFolderDiffUtil : DiffUtil.ItemCallback<FavoritePlaceFolderModel>() {
        override fun areItemsTheSame(
            oldItem: FavoritePlaceFolderModel,
            newItem: FavoritePlaceFolderModel,
        ): Boolean = oldItem.name == newItem.name

        override fun areContentsTheSame(
            oldItem: FavoritePlaceFolderModel,
            newItem: FavoritePlaceFolderModel,
        ): Boolean = oldItem == newItem
    }
}
