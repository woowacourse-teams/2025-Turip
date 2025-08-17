package com.on.turip.ui.main.favorite

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.on.turip.ui.main.favorite.model.FavoriteFolderModel

class FavoritePlaceFolderAdapter(
    private val onFavoritePlaceFolderListener: FavoritePlaceFolderViewHolder.OnFavoritePlaceFolderListener,
) : ListAdapter<FavoriteFolderModel, FavoritePlaceFolderViewHolder>(FavoritePlaceFolderDiffUtil) {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): FavoritePlaceFolderViewHolder = FavoritePlaceFolderViewHolder.of(parent, onFavoritePlaceFolderListener)

    override fun onBindViewHolder(
        holder: FavoritePlaceFolderViewHolder,
        position: Int,
    ) {
        holder.bind(getItem(position))
    }

    private object FavoritePlaceFolderDiffUtil : DiffUtil.ItemCallback<FavoriteFolderModel>() {
        override fun areItemsTheSame(
            oldItem: FavoriteFolderModel,
            newItem: FavoriteFolderModel,
        ): Boolean = oldItem.id == newItem.id

        override fun areContentsTheSame(
            oldItem: FavoriteFolderModel,
            newItem: FavoriteFolderModel,
        ): Boolean = oldItem == newItem
    }
}
