package com.on.turip.ui.main

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.on.turip.databinding.ItemSearchingRegionBinding
import com.on.turip.ui.common.model.RegionModel

class RegionAdapter(
    private val onRegionListener: RegionViewHolder.OnRegionListener,
) : ListAdapter<RegionModel, RegionViewHolder>(
        RegionDiffUtil,
    ) {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): RegionViewHolder {
        val inflater: LayoutInflater = LayoutInflater.from(parent.context)
        val binding: ItemSearchingRegionBinding =
            ItemSearchingRegionBinding.inflate(inflater, parent, false)
        return RegionViewHolder(binding, onRegionListener)
    }

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
