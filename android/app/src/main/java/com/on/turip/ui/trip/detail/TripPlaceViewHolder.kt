package com.on.turip.ui.trip.detail

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.on.turip.R
import com.on.turip.databinding.ItemTripPlaceBinding

class TripPlaceViewHolder(
    private val binding: ItemTripPlaceBinding,
    onClickListener: PlaceListener,
) : RecyclerView.ViewHolder(binding.root) {
    private var placeModel: PlaceModel? = null

    init {
        itemView.setOnClickListener {
            placeModel?.let {
                onClickListener.onItemClick(it)
            }
        }

        binding.clTripPlaceMap.setOnClickListener {
            placeModel?.let {
                onClickListener.onPlaceClick(it)
            }
        }
        binding.clTripPlaceTimeLine.setOnClickListener {
            placeModel?.let {
                onClickListener.onTimeLineClick(it)
            }
        }
        binding.clTripPlaceFavorite.setOnClickListener {
            placeModel?.let {
                onClickListener.onFavoriteClick(it)
            }
        }
    }

    fun bind(placeModel: PlaceModel) {
        this.placeModel = placeModel
        binding.tvTripPlaceName.text = placeModel.name
        binding.tvTripPlaceCategory.text = placeModel.turipCategory
        binding.tvTripPlaceTimeLine.text =
            itemView.context.getString(
                R.string.trip_place_time_line,
                placeModel.timeLine,
            )
        binding.ivTripPlaceFavorite.isSelected = placeModel.isFavorite
        binding.ivTripPlaceMap.setImageResource(Maps.from(placeModel.placeUri).iconRes)
    }

    companion object {
        fun of(
            parent: ViewGroup,
            onClickListener: PlaceListener,
        ): TripPlaceViewHolder {
            val inflater: LayoutInflater = LayoutInflater.from(parent.context)
            val binding: ItemTripPlaceBinding =
                ItemTripPlaceBinding.inflate(inflater, parent, false)
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
