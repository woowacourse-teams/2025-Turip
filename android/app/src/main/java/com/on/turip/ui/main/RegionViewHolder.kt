package com.on.turip.ui.main

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

    fun interface OnRegionListener {
        fun onRegionClick(region: RegionModel)
    }
}
