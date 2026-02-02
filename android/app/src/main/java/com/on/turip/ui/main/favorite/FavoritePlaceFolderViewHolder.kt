package com.on.turip.ui.main.favorite

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.on.turip.databinding.ItemFavoritePlaceFolderBinding
import com.on.turip.ui.main.favorite.model.TuripModel

class FavoritePlaceFolderViewHolder(
    private val binding: ItemFavoritePlaceFolderBinding,
    favoritePlaceFolderListener: FavoritePlaceFolderListener,
) : RecyclerView.ViewHolder(binding.root) {
    private var turipModel: TuripModel? = null

    init {
        binding.ivFavoritePlaceFolderFavorite.setOnClickListener {
            turipModel?.let { placeFolder: TuripModel ->
                favoritePlaceFolderListener.onFavoriteFolderFavoriteClick(placeFolder)
            }
        }
        binding.ivFavoritePlaceFolderIcon.setOnClickListener {
            turipModel?.let {
                favoritePlaceFolderListener.onFavoriteFolderClick(
                    favoriteFolderId = it.id,
                    favoriteFolderName = it.name,
                )
            }
        }
        binding.tvFavoritePlaceFolderName.setOnClickListener {
            turipModel?.let {
                favoritePlaceFolderListener.onFavoriteFolderClick(
                    favoriteFolderId = it.id,
                    favoriteFolderName = it.name,
                )
            }
        }
    }

    fun bind(turipModel: TuripModel) {
        this.turipModel = turipModel
        binding.tvFavoritePlaceFolderName.text = turipModel.name
        binding.ivFavoritePlaceFolderFavorite.isSelected = turipModel.isSelected
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
        fun onFavoriteFolderFavoriteClick(turipModel: TuripModel)

        fun onFavoriteFolderClick(
            favoriteFolderId: Long,
            favoriteFolderName: String,
        )
    }
}
