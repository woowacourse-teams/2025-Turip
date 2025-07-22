package com.on.turip.ui.search.detail

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.on.turip.databinding.ItemTravelPlaceBinding
import com.on.turip.ui.common.model.PlaceModel

class TravelPlaceViewHolder(
    private val binding: ItemTravelPlaceBinding,
    onClickListener: OnPlaceListener,
) : RecyclerView.ViewHolder(binding.root) {
    private var placeModel: PlaceModel? = null

    init {
        binding.ivTravelPlaceLink.setOnClickListener {
            placeModel?.let {
                onClickListener.onPlaceClick(it)
            }
        }
    }

    fun bind(placeModel: PlaceModel) {
        this.placeModel = placeModel
        binding.tvTravelPlaceName.text = placeModel.name
        binding.tvTravelPlaceCategory.text = placeModel.category
    }

    companion object {
        fun of(
            parent: ViewGroup,
            onClickListener: OnPlaceListener,
        ): TravelPlaceViewHolder {
            val inflater: LayoutInflater = LayoutInflater.from(parent.context)
            val binding: ItemTravelPlaceBinding =
                ItemTravelPlaceBinding.inflate(inflater, parent, false)
            return TravelPlaceViewHolder(binding, onClickListener)
        }
    }

    fun interface OnPlaceListener {
        fun onPlaceClick(placeModel: PlaceModel)
    }
}
