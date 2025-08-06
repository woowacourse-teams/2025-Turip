package com.on.turip.ui.main.home

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.on.turip.ui.common.model.RegionModel

class RegionAdapter(
    private val onRegionListener: RegionViewHolder.OnRegionListener,
) : ListAdapter<RegionModel, RegionViewHolder>(
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

    private object RegionDiffUtil : DiffUtil.ItemCallback<RegionModel>() {
        override fun areItemsTheSame(
            oldItem: RegionModel,
            newItem: RegionModel,
        ): Boolean = oldItem.english == newItem.english

        override fun areContentsTheSame(
            oldItem: RegionModel,
            newItem: RegionModel,
        ): Boolean = oldItem == newItem
    }
}
