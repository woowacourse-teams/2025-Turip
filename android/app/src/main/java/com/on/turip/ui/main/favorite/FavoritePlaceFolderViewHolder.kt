package com.on.turip.ui.main.favorite

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.on.turip.databinding.ItemFavoritePlaceFolderBinding
import com.on.turip.ui.main.favorite.model.FavoritePlaceFolderModel

class FavoritePlaceFolderViewHolder(
    private val binding: ItemFavoritePlaceFolderBinding,
    onFavoritePlaceFolderListener: OnFavoritePlaceFolderListener,
) : RecyclerView.ViewHolder(binding.root) {
    private var favoritePlaceFolderModel: FavoritePlaceFolderModel? = null

    init {

        binding.ivFavoritePlaceFolderFavorite.setOnClickListener {
            favoritePlaceFolderModel?.let { placeFolder: FavoritePlaceFolderModel ->
                onFavoritePlaceFolderListener.onFavoriteFolderClick(placeFolder)
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
            onFavoritePlaceFolderListener: OnFavoritePlaceFolderListener,
        ): FavoritePlaceFolderViewHolder {
            val inflater: LayoutInflater = LayoutInflater.from(parent.context)
            val binding: ItemFavoritePlaceFolderBinding =
                ItemFavoritePlaceFolderBinding.inflate(inflater, parent, false)
            return FavoritePlaceFolderViewHolder(binding, onFavoritePlaceFolderListener)
        }
    }

    fun interface OnFavoritePlaceFolderListener {
        fun onFavoriteFolderClick(favoritePlaceFolderModel: FavoritePlaceFolderModel)
    }
}
