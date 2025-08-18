package com.on.turip.ui.main.favorite

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.on.turip.databinding.ItemPlaceFolderNameBinding
import com.on.turip.ui.main.favorite.model.FavoritePlaceFolderModel

class FavoritePlaceFolderNameViewHolder(
    private val binding: ItemPlaceFolderNameBinding,
    onPlaceFolderListener: OnPlaceFolderNameListener,
) : RecyclerView.ViewHolder(binding.root) {
    private var favoritePlaceFolderModel: FavoritePlaceFolderModel? = null

    init {
        itemView.setOnClickListener {
            favoritePlaceFolderModel?.let { onPlaceFolderListener.onPlaceFolderNameClick(it.id) }
        }
    }

    fun bind(favoritePlaceFolderModel: FavoritePlaceFolderModel) {
        this.favoritePlaceFolderModel = favoritePlaceFolderModel
        binding.tvPlaceFolderName.text = favoritePlaceFolderModel.name
        itemView.isSelected = favoritePlaceFolderModel.isSelected
    }

    companion object {
        fun of(
            parent: ViewGroup,
            onPlaceFolderListener: OnPlaceFolderNameListener,
        ): FavoritePlaceFolderNameViewHolder {
            val inflater: LayoutInflater = LayoutInflater.from(parent.context)
            val binding: ItemPlaceFolderNameBinding =
                ItemPlaceFolderNameBinding.inflate(inflater, parent, false)
            return FavoritePlaceFolderNameViewHolder(binding, onPlaceFolderListener)
        }
    }

    fun interface OnPlaceFolderNameListener {
        fun onPlaceFolderNameClick(folderId: Long)
    }
}
