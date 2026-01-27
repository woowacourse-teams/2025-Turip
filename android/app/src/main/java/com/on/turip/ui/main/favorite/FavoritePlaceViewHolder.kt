package com.on.turip.ui.main.favorite

import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.on.turip.databinding.ItemFavoritePlaceBinding
import com.on.turip.ui.main.favorite.model.Maps
import com.on.turip.ui.main.favorite.model.TuripPlaceModel

class FavoritePlaceViewHolder(
    private val binding: ItemFavoritePlaceBinding,
    favoritePlaceListener: FavoritePlaceListener,
) : RecyclerView.ViewHolder(binding.root) {
    private var turipPlaceModel: TuripPlaceModel? = null

    init {
        binding.ivFavoritePlaceMapLink.setOnClickListener {
            turipPlaceModel?.let {
                favoritePlaceListener.onMapClick(it.uri)
            }
        }
        binding.ivFavoritePlaceFavorite.setOnClickListener {
            turipPlaceModel?.let {
                favoritePlaceListener.onFavoriteClick(it.placeId, it.isTuripPlace)
            }
        }
        binding.root.setOnClickListener {
            turipPlaceModel?.let {
                favoritePlaceListener.onItemClick(it)
            }
        }
    }

    fun bind(turipPlaceModel: TuripPlaceModel) {
        this.turipPlaceModel = turipPlaceModel

        binding.tvFavoritePlaceCategory.text = turipPlaceModel.turipCategory
        binding.tvFavoritePlaceName.text = turipPlaceModel.name
        binding.ivFavoritePlaceFavorite.isSelected = turipPlaceModel.isTuripPlace
        binding.ivFavoritePlaceMapLink.setImageResource(Maps.from(turipPlaceModel.uri).iconRes)
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

        fun onItemClick(turipPlaceModel: TuripPlaceModel)
    }
}
