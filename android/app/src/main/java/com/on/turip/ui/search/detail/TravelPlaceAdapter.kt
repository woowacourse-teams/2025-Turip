package com.on.turip.ui.search.detail

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.on.turip.ui.common.model.PlaceModel

class TravelPlaceAdapter(
    private val onClickListener: TravelPlaceViewHolder.OnPlaceListener,
) : ListAdapter<PlaceModel, TravelPlaceViewHolder>(TravelPlaceDiffUtil) {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): TravelPlaceViewHolder = TravelPlaceViewHolder.of(parent, onClickListener)

    override fun onBindViewHolder(
        holder: TravelPlaceViewHolder,
        position: Int,
    ) {
        val placeModel: PlaceModel = getItem(position)
        holder.bind(placeModel)
    }

    private object TravelPlaceDiffUtil : DiffUtil.ItemCallback<PlaceModel>() {
        override fun areItemsTheSame(
            oldItem: PlaceModel,
            newItem: PlaceModel,
        ): Boolean = oldItem.name == newItem.name

        override fun areContentsTheSame(
            oldItem: PlaceModel,
            newItem: PlaceModel,
        ): Boolean = oldItem == newItem
    }
}
