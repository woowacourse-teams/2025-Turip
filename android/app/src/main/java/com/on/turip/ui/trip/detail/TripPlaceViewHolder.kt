package com.on.turip.ui.trip.detail

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.on.turip.R
import com.on.turip.databinding.ItemTravelPlaceBinding

class TripPlaceViewHolder(
    private val binding: ItemTravelPlaceBinding,
    onClickListener: PlaceListener,
) : RecyclerView.ViewHolder(binding.root) {
    private var placeModel: PlaceModel? = null

    init {
        itemView.setOnClickListener {
            placeModel?.let {
                onClickListener.onItemClick(it)
            }
        }

        binding.clTravelPlaceMap.setOnClickListener {
            placeModel?.let {
                onClickListener.onPlaceClick(it)
            }
        }
        binding.clTravelPlaceTimeLine.setOnClickListener {
            placeModel?.let {
                onClickListener.onTimeLineClick(it)
            }
        }
        binding.clTravelPlaceFavorite.setOnClickListener {
            placeModel?.let {
                onClickListener.onFavoriteClick(it)
            }
        }
    }

    fun bind(placeModel: PlaceModel) {
        this.placeModel = placeModel
        binding.tvTravelPlaceName.text = placeModel.name
        binding.tvTravelPlaceCategory.text = placeModel.turipCategory
        binding.tvTravelPlaceTimeLine.text =
            itemView.context.getString(
                R.string.travel_place_time_line,
                placeModel.timeLine,
            )
        binding.ivTravelPlaceFavorite.isSelected = placeModel.isFavorite
    }

    companion object {
        fun of(
            parent: ViewGroup,
            onClickListener: PlaceListener,
        ): TripPlaceViewHolder {
            val inflater: LayoutInflater = LayoutInflater.from(parent.context)
            val binding: ItemTravelPlaceBinding =
                ItemTravelPlaceBinding.inflate(inflater, parent, false)
            return TripPlaceViewHolder(binding, onClickListener)
        }
    }

    interface PlaceListener {
        fun onItemClick(placeModel: PlaceModel)

        fun onPlaceClick(placeModel: PlaceModel)

        fun onTimeLineClick(placeModel: PlaceModel)

        fun onFavoriteClick(placeModel: PlaceModel)
    }
}
