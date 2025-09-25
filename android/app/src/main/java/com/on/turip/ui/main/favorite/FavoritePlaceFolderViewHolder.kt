package com.on.turip.ui.main.favorite

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.on.turip.databinding.ItemFavoritePlaceFolderBinding
import com.on.turip.ui.main.favorite.model.FavoritePlaceFolderModel

class FavoritePlaceFolderViewHolder(
    private val binding: ItemFavoritePlaceFolderBinding,
    favoritePlaceFolderListener: FavoritePlaceFolderListener,
) : RecyclerView.ViewHolder(binding.root) {
    private var favoritePlaceFolderModel: FavoritePlaceFolderModel? = null

    init {
        binding.ivFavoritePlaceFolderFavorite.setOnClickListener {
            favoritePlaceFolderModel?.let { placeFolder: FavoritePlaceFolderModel ->
                favoritePlaceFolderListener.onFavoriteFolderFavoriteClick(placeFolder)
            }
        }
        binding.ivFavoritePlaceFolderIcon.setOnClickListener {
            favoritePlaceFolderModel?.let {
                favoritePlaceFolderListener.onFavoriteFolderClick(
                    favoriteFolderId = it.id,
                    favoriteFolderName = it.name,
                )
            }
        }
        binding.tvFavoritePlaceFolderName.setOnClickListener {
            favoritePlaceFolderModel?.let {
                favoritePlaceFolderListener.onFavoriteFolderClick(
                    favoriteFolderId = it.id,
                    favoriteFolderName = it.name,
                )
            }
        }
    }

    fun bind(favoritePlaceFolderModel: FavoritePlaceFolderModel) {
        this.favoritePlaceFolderModel = favoritePlaceFolderModel
        binding.tvFavoritePlaceFolderName.text = favoritePlaceFolderModel.name
        binding.ivFavoritePlaceFolderFavorite.isSelected = favoritePlaceFolderModel.isSelected
    }

    companion object {
        fun of(
            parent: ViewGroup,
            favoritePlaceFolderListener: FavoritePlaceFolderListener,
        ): FavoritePlaceFolderViewHolder {
            val inflater: LayoutInflater = LayoutInflater.from(parent.context)
            val binding: ItemFavoritePlaceFolderBinding =
                ItemFavoritePlaceFolderBinding.inflate(inflater, parent, false)
            return FavoritePlaceFolderViewHolder(binding, favoritePlaceFolderListener)
        }
    }

    interface FavoritePlaceFolderListener {
        fun onFavoriteFolderFavoriteClick(favoritePlaceFolderModel: FavoritePlaceFolderModel)

        fun onFavoriteFolderClick(
            favoriteFolderId: Long,
            favoriteFolderName: String,
        )
    }
}
