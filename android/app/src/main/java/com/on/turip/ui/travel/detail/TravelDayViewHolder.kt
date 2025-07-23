package com.on.turip.ui.travel.detail

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.on.turip.R
import com.on.turip.databinding.ItemTravelDayBinding

class TravelDayViewHolder(
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
            itemView.context.getString(R.string.search_detail_travel_day, dayModel.day)
        binding.divTravelDayUnderline.visibility =
            if (dayModel.isSelected) View.VISIBLE else View.GONE
    }

    companion object {
        fun of(
            parent: ViewGroup,
            onClickListener: OnDayListener,
        ): TravelDayViewHolder {
            val inflater: LayoutInflater = LayoutInflater.from(parent.context)
            val binding: ItemTravelDayBinding =
                ItemTravelDayBinding.inflate(inflater, parent, false)
            return TravelDayViewHolder(binding, onClickListener)
        }
    }

    fun interface OnDayListener {
        fun onDayClick(dayModel: DayModel)
    }
}
