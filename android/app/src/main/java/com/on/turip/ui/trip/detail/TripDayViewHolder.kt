package com.on.turip.ui.trip.detail

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.on.turip.R
import com.on.turip.databinding.ItemTravelDayBinding

class TripDayViewHolder(
    private val binding: ItemTravelDayBinding,
    onClickListener: OnDayListener,
) : RecyclerView.ViewHolder(binding.root) {
    private var dayModel: DayModel? = null

    init {
        itemView.setOnClickListener {
            dayModel?.let {
                onClickListener.onDayClick(it)
            }
        }
    }

    fun bind(dayModel: DayModel) {
        this.dayModel = dayModel
        binding.tvTravelDayDay.text =
            if (dayModel.day == DayModel.ALL_PLACE) {
                itemView.context.getString(R.string.trip_detail_travel_all_place)
            } else {
                itemView.context.getString(R.string.trip_detail_travel_day, dayModel.day)
            }
        binding.divTravelDayUnderline.visibility =
            if (dayModel.isSelected) View.VISIBLE else View.GONE
    }

    companion object {
        fun of(
            parent: ViewGroup,
            onClickListener: OnDayListener,
        ): TripDayViewHolder {
            val inflater: LayoutInflater = LayoutInflater.from(parent.context)
            val binding: ItemTravelDayBinding =
                ItemTravelDayBinding.inflate(inflater, parent, false)
            return TripDayViewHolder(binding, onClickListener)
        }
    }

    fun interface OnDayListener {
        fun onDayClick(dayModel: DayModel)
    }
}
