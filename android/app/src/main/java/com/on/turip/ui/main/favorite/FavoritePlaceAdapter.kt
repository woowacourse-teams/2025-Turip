package com.on.turip.ui.main.favorite

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.on.turip.ui.main.favorite.model.FavoritePlaceModel

class FavoritePlaceAdapter(
    private val favoritePlaceListener: FavoritePlaceViewHolder.FavoritePlaceListener,
    private val onChange: (favoritePlaces: List<FavoritePlaceModel>) -> Unit,
) : ListAdapter<FavoritePlaceModel, FavoritePlaceViewHolder>(FavoritePlaceDiffUtil) {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): FavoritePlaceViewHolder = FavoritePlaceViewHolder.of(parent, favoritePlaceListener)

    override fun onBindViewHolder(
        holder: FavoritePlaceViewHolder,
        position: Int,
    ) {
        holder.bind(getItem(position))
    }

    fun moveItem(
        from: Int,
        to: Int,
    ) {
        val updatedItems =
            buildList {
                addAll(currentList)
                val item = removeAt(from)
                add(to, item)
            }
        submitList(updatedItems) {
            onChange(updatedItems)
        }
    }

    private object FavoritePlaceDiffUtil : DiffUtil.ItemCallback<FavoritePlaceModel>() {
        override fun areItemsTheSame(
            oldItem: FavoritePlaceModel,
            newItem: FavoritePlaceModel,
        ): Boolean = oldItem.placeId == newItem.placeId

        override fun areContentsTheSame(
            oldItem: FavoritePlaceModel,
            newItem: FavoritePlaceModel,
        ): Boolean = oldItem == newItem
    }
}
