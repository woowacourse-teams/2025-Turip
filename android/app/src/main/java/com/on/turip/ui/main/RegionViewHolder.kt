package com.on.turip.ui.main

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.on.turip.databinding.ItemSearchingRegionBinding
import com.on.turip.ui.common.model.RegionModel

class RegionViewHolder(
    private val binding: ItemSearchingRegionBinding,
    onRegionListener: OnRegionListener,
) : RecyclerView.ViewHolder(binding.root) {
    private var region: RegionModel? = null

    init {
        binding.root.setOnClickListener {
            region?.let { onRegionListener.onRegionClick(it) }
        }
    }

    fun bind(region: RegionModel) {
        this.region = region
        binding.tvSearchingRegion.text = region.korean
    }

    companion object {
        fun of(
            parent: ViewGroup,
            onRegionListener: OnRegionListener,
        ): RegionViewHolder {
            val inflater: LayoutInflater = LayoutInflater.from(parent.context)
            val binding: ItemSearchingRegionBinding =
                ItemSearchingRegionBinding.inflate(inflater, parent, false)
            return RegionViewHolder(binding, onRegionListener)
        }
    }

    fun interface OnRegionListener {
        fun onRegionClick(region: RegionModel)
    }
}
