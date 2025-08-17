package com.on.turip.ui.trip.detail

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.on.turip.databinding.ItemTravelPlaceBinding

class TripPlaceViewHolder(
    private val binding: ItemTravelPlaceBinding,
    onClickListener: OnPlaceListener,
) : RecyclerView.ViewHolder(binding.root) {
    private var placeModel: PlaceModel? = null

    init {
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
    }

    companion object {
        fun of(
            parent: ViewGroup,
            onClickListener: OnPlaceListener,
        ): TripPlaceViewHolder {
            val inflater: LayoutInflater = LayoutInflater.from(parent.context)
            val binding: ItemTravelPlaceBinding =
                ItemTravelPlaceBinding.inflate(inflater, parent, false)
            return TripPlaceViewHolder(binding, onClickListener)
        }
    }

    interface OnPlaceListener {
        fun onPlaceClick(placeModel: PlaceModel)

        fun onTimeLineClick(placeModel: PlaceModel)

        fun onFavoriteClick(placeModel: PlaceModel)
    }
}
