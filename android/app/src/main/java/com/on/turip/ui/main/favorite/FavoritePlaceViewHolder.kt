package com.on.turip.ui.main.favorite

import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.on.turip.databinding.ItemFavoritePlaceBinding
import com.on.turip.ui.compose.turipdetail.model.turip.TuripPlaceUiModel
import com.on.turip.ui.main.favorite.model.Maps

class FavoritePlaceViewHolder(
    private val binding: ItemFavoritePlaceBinding,
    favoritePlaceListener: FavoritePlaceListener,
) : RecyclerView.ViewHolder(binding.root) {
    private var turipPlaceUiModel: TuripPlaceUiModel? = null

    init {
        binding.ivFavoritePlaceMapLink.setOnClickListener {
            turipPlaceUiModel?.let {
                favoritePlaceListener.onMapClick(it.uri)
            }
        }
        binding.ivFavoritePlaceFavorite.setOnClickListener {
            turipPlaceUiModel?.let {
                favoritePlaceListener.onFavoriteClick(it.placeId, it.isTuripPlace)
            }
        }
        binding.root.setOnClickListener {
            turipPlaceUiModel?.let {
                favoritePlaceListener.onItemClick(it)
            }
        }
    }

    fun bind(turipPlaceUiModel: TuripPlaceUiModel) {
        this.turipPlaceUiModel = turipPlaceUiModel

        binding.tvFavoritePlaceCategory.text = turipPlaceUiModel.turipCategory
        binding.tvFavoritePlaceName.text = turipPlaceUiModel.name
        binding.ivFavoritePlaceFavorite.isSelected = turipPlaceUiModel.isTuripPlace
        binding.ivFavoritePlaceMapLink.setImageResource(Maps.from(turipPlaceUiModel.uri).iconRes)
    }

    companion object {
        fun of(
            parent: ViewGroup,
            favoritePlaceListener: FavoritePlaceListener,
        ): FavoritePlaceViewHolder {
            val inflater: LayoutInflater = LayoutInflater.from(parent.context)
            val binding: ItemFavoritePlaceBinding =
                ItemFavoritePlaceBinding.inflate(inflater, parent, false)
            return FavoritePlaceViewHolder(binding, favoritePlaceListener)
        }
    }

    interface FavoritePlaceListener {
        fun onFavoriteClick(
            placeId: Long,
            isFavorite: Boolean,
        )

        fun onMapClick(uri: Uri)

        fun onItemClick(turipPlaceUiModel: TuripPlaceUiModel)
    }
}
