package com.on.turip.ui.trip.detail

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter

class TripDayAdapter(
    private val onClickListener: TripDayViewHolder.OnDayListener,
) : ListAdapter<DayModel, TripDayViewHolder>(TravelDayDiffUtil) {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): TripDayViewHolder = TripDayViewHolder.of(parent, onClickListener)

    override fun onBindViewHolder(
        holder: TripDayViewHolder,
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
