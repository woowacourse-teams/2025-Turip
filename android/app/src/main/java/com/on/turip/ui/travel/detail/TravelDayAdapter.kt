package com.on.turip.ui.travel.detail

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter

class TravelDayAdapter(
    private val onClickListener: TravelDayViewHolder.OnDayListener,
) : ListAdapter<DayModel, TravelDayViewHolder>(TravelDayDiffUtil) {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): TravelDayViewHolder = TravelDayViewHolder.of(parent, onClickListener)

    override fun onBindViewHolder(
        holder: TravelDayViewHolder,
        position: Int,
    ) {
        val dayModel: DayModel = getItem(position)
        holder.bind(dayModel)
    }

    private object TravelDayDiffUtil : DiffUtil.ItemCallback<DayModel>() {
        override fun areItemsTheSame(
            oldItem: DayModel,
            newItem: DayModel,
        ): Boolean = oldItem.day == newItem.day

        override fun areContentsTheSame(
            oldItem: DayModel,
            newItem: DayModel,
        ): Boolean = oldItem == newItem
    }
}
