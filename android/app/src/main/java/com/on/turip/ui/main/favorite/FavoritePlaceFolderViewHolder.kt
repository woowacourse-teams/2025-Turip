package com.on.turip.ui.main.favorite

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.on.turip.databinding.ItemFavoritePlaceFolderBinding
import com.on.turip.ui.main.favorite.model.FavoriteFolderModel

class FavoritePlaceFolderViewHolder(
    private val binding: ItemFavoritePlaceFolderBinding,
    onFavoritePlaceFolderListener: OnFavoritePlaceFolderListener,
) : RecyclerView.ViewHolder(binding.root) {
    private var favoriteFolderModel: FavoriteFolderModel? = null

    init {
        binding.ivFavoritePlaceFolderFavorite.setOnClickListener {
            favoriteFolderModel?.let { onFavoritePlaceFolderListener.onFavoriteClick(it) }
        }
    }

    fun bind(favoriteFolderModel: FavoriteFolderModel) {
        this.favoriteFolderModel = favoriteFolderModel

        binding.tvFavoritePlaceFolderName.text = favoriteFolderModel.name
        binding.ivFavoritePlaceFolderFavorite.isSelected = favoriteFolderModel.isFavorite
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
        fun onFavoriteClick(favoriteFolderModel: FavoriteFolderModel)
    }
}
