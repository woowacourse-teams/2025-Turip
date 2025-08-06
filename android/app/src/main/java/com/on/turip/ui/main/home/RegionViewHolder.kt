package com.on.turip.ui.main.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.on.turip.databinding.ItemRegionBinding
import com.on.turip.domain.region.RegionCategory
import com.on.turip.ui.common.loadCircularImage

class RegionViewHolder(
    private val binding: ItemRegionBinding,
    onRegionListener: OnRegionListener,
) : RecyclerView.ViewHolder(binding.root) {
    private var regionCategory: RegionCategory? = null

    init {
        binding.root.setOnClickListener {
            regionCategory?.let { onRegionListener.onRegionClick(it.name) }
        }
    }

    fun bind(regionCategory: RegionCategory) {
        this.regionCategory = regionCategory
        binding.tvRegionName.text = regionCategory.name
        binding.ivRegion.loadCircularImage(regionCategory.imageUrl)
    }

    companion object {
        fun of(
            parent: ViewGroup,
            onRegionListener: OnRegionListener,
        ): RegionViewHolder {
            val inflater: LayoutInflater = LayoutInflater.from(parent.context)
            val binding: ItemRegionBinding =
                ItemRegionBinding.inflate(inflater, parent, false)
            return RegionViewHolder(binding, onRegionListener)
        }
    }

    fun interface OnRegionListener {
        fun onRegionClick(regionCategoryName: String)
    }
}
