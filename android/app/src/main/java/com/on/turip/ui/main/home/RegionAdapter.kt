package com.on.turip.ui.main.home

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.on.turip.domain.region.RegionCategory

class RegionAdapter(
    private val onRegionListener: RegionViewHolder.OnRegionListener,
) : ListAdapter<RegionCategory, RegionViewHolder>(
        RegionDiffUtil,
    ) {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): RegionViewHolder = RegionViewHolder.of(parent, onRegionListener)

    override fun onBindViewHolder(
        holder: RegionViewHolder,
        position: Int,
    ): Unit = holder.bind(getItem(position))

    private object RegionDiffUtil : DiffUtil.ItemCallback<RegionCategory>() {
        override fun areItemsTheSame(
            oldItem: RegionCategory,
            newItem: RegionCategory,
        ): Boolean = oldItem.name == newItem.name

        override fun areContentsTheSame(
            oldItem: RegionCategory,
            newItem: RegionCategory,
        ): Boolean = oldItem == newItem
    }
}
