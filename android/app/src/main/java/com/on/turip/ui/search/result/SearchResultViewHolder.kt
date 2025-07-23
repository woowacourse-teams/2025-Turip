package com.on.turip.ui.search.result

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.load
import coil.transform.CircleCropTransformation
import com.on.turip.R
import com.on.turip.databinding.ItemSearchResultBinding
import com.on.turip.domain.contents.VideoInformation

class SearchResultViewHolder(
    private val binding: ItemSearchResultBinding,
    private val onSearchResultListener: OnSearchResultListener,
) : RecyclerView.ViewHolder(binding.root) {
    init {
        itemView.setOnClickListener {
            onSearchResultListener.onSearchResultClick()
        }
    }

    fun bind(videoInformation: VideoInformation) {
        binding.tvSearchResultTitle.text = videoInformation.content.title
        binding.tvSearchResultDay.text =
            itemView.context.getString(
                R.string.search_result_trip_duration,
                videoInformation.tripDuration.nights,
                videoInformation.tripDuration.days,
            )
        binding.tvSearchResultLocation.text =
            itemView.context.getString(
                R.string.search_result_location_number,
                videoInformation.tripPlaceCount,
            )
        binding.tvSearchResultDescription.text =
            itemView.context.getString(
                R.string.search_result_video_description,
                videoInformation.content.creator.channelName,
                videoInformation.content.uploadedDate,
            )
        binding.ivSearchResultThumbnail.load(videoInformation.content.creator.profileImage) {
            crossfade(true)
            transformations(CircleCropTransformation())
        }
    }

    companion object {
        fun of(
            parent: ViewGroup,
            onSearchResultListener: OnSearchResultListener,
        ): SearchResultViewHolder {
            val inflater: LayoutInflater = LayoutInflater.from(parent.context)
            val binding: ItemSearchResultBinding =
                ItemSearchResultBinding.inflate(inflater, parent, false)
            return SearchResultViewHolder(binding, onSearchResultListener)
        }
    }

    fun interface OnSearchResultListener {
        fun onSearchResultClick()
    }
}
