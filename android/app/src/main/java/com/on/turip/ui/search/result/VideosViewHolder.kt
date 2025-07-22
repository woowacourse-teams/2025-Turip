package com.on.turip.ui.search.result

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.on.turip.R
import com.on.turip.databinding.ItemSearchResultBinding
import com.on.turip.ui.common.model.SearchResultModel

class VideosViewHolder(
    private val binding: ItemSearchResultBinding,
    private val onSearchResultListener: OnSearchResultListener,
) : RecyclerView.ViewHolder(binding.root) {
    init {
        itemView.setOnClickListener {
            onSearchResultListener.onSearchResultClick()
        }
    }

    fun bind(searchResultModel: SearchResultModel) {
        binding.tvSearchResultTitle.text = searchResultModel.content.title
        binding.tvSearchResultDay.text =
            itemView.context.getString(
                R.string.search_result_trip_duration,
                searchResultModel.tripDuration.nights,
                searchResultModel.tripDuration.days,
            )
        binding.tvSearchResultLocation.text =
            itemView.context.getString(
                R.string.search_result_location_number,
                searchResultModel.tripPlaceCount,
            )
        binding.tvSearchResultDescription.text =
            itemView.context.getString(
                R.string.search_result_video_description,
                searchResultModel.content.creator.channelName,
                searchResultModel.content.uploadedDate,
            )
        binding.ivSearchResultThumbnail
    }

    companion object {
        fun of(
            parent: ViewGroup,
            onSearchResultListener: OnSearchResultListener,
        ): VideosViewHolder {
            val inflater: LayoutInflater = LayoutInflater.from(parent.context)
            val binding: ItemSearchResultBinding =
                ItemSearchResultBinding.inflate(inflater, parent, false)
            return VideosViewHolder(binding, onSearchResultListener)
        }
    }

    fun interface OnSearchResultListener {
        fun onSearchResultClick()
    }
}
