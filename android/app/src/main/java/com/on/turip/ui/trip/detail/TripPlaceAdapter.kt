package com.on.turip.ui.trip.detail

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter

class TripPlaceAdapter(
    private val onClickListener: TripPlaceViewHolder.PlaceListener,
) : ListAdapter<PlaceModel, TripPlaceViewHolder>(TravelPlaceDiffUtil) {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): TripPlaceViewHolder = TripPlaceViewHolder.of(parent, onClickListener)

    override fun onBindViewHolder(
        holder: TripPlaceViewHolder,
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
