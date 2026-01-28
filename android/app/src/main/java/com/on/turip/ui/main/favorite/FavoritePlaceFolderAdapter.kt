package com.on.turip.ui.main.favorite

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.on.turip.ui.main.favorite.model.TuripModel

class FavoritePlaceFolderAdapter(
    private val favoritePlaceFolderListener: FavoritePlaceFolderViewHolder.FavoritePlaceFolderListener,
) : ListAdapter<TuripModel, FavoritePlaceFolderViewHolder>(FavoritePlaceFolderDiffUtil) {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): FavoritePlaceFolderViewHolder = FavoritePlaceFolderViewHolder.of(parent, favoritePlaceFolderListener)

    override fun onBindViewHolder(
        holder: FavoritePlaceFolderViewHolder,
        position: Int,
    ) {
        holder.bind(getItem(position))
    }

    private object FavoritePlaceFolderDiffUtil : DiffUtil.ItemCallback<TuripModel>() {
        override fun areItemsTheSame(
            oldItem: TuripModel,
            newItem: TuripModel,
        ): Boolean = oldItem.id == newItem.id

        override fun areContentsTheSame(
            oldItem: TuripModel,
            newItem: TuripModel,
        ): Boolean = oldItem == newItem
    }
}
