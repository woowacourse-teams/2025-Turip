package com.on.turip.ui.main.favorite

import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.on.turip.databinding.ItemFavoritePlaceBinding
import com.on.turip.ui.main.favorite.model.FavoritePlaceUiModel
import com.on.turip.ui.main.favorite.model.Maps

class FavoritePlaceViewHolder(
    private val binding: ItemFavoritePlaceBinding,
    favoritePlaceListener: FavoritePlaceListener,
) : RecyclerView.ViewHolder(binding.root) {
    private var favoritePlaceUiModel: FavoritePlaceUiModel? = null

    init {
        binding.ivFavoritePlaceMapLink.setOnClickListener {
            favoritePlaceUiModel?.let {
                favoritePlaceListener.onMapClick(it.uri)
            }
        }
        binding.ivFavoritePlaceFavorite.setOnClickListener {
            favoritePlaceUiModel?.let {
                favoritePlaceListener.onFavoriteClick(it.placeId, it.isFavorite)
            }
        }
        binding.root.setOnClickListener {
            favoritePlaceUiModel?.let {
                favoritePlaceListener.onItemClick(it)
            }
        }
    }

    fun bind(favoritePlaceUiModel: FavoritePlaceUiModel) {
        this.favoritePlaceUiModel = favoritePlaceUiModel

        binding.tvFavoritePlaceCategory.text = favoritePlaceUiModel.turipCategory
        binding.tvFavoritePlaceName.text = favoritePlaceUiModel.name
        binding.ivFavoritePlaceFavorite.isSelected = favoritePlaceUiModel.isFavorite
        binding.ivFavoritePlaceMapLink.setImageResource(Maps.from(favoritePlaceUiModel.uri).iconRes)
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

        fun onItemClick(favoritePlaceUiModel: FavoritePlaceUiModel)
    }
}
