package com.on.turip.ui.search.result

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.load
import coil.transform.CircleCropTransformation
import com.on.turip.R
import com.on.turip.databinding.ItemSearchResultBinding
import com.on.turip.domain.contents.Video

class VideosViewHolder(
    private val binding: ItemSearchResultBinding,
    private val onSearchResultListener: OnSearchResultListener,
) : RecyclerView.ViewHolder(binding.root) {
    init {
        itemView.setOnClickListener {
            onSearchResultListener.onSearchResultClick()
        }
    }

    fun bind(video: Video) {
        binding.tvSearchResultTitle.text = video.content.title
        binding.tvSearchResultDay.text =
            itemView.context.getString(
                R.string.search_result_trip_duration,
                video.tripDuration.nights,
                video.tripDuration.days,
            )
        binding.tvSearchResultLocation.text =
            itemView.context.getString(
                R.string.search_result_location_number,
                video.tripPlaceCount,
            )
        binding.tvSearchResultDescription.text =
            itemView.context.getString(
                R.string.search_result_video_description,
                video.content.creator.channelName,
                video.content.uploadedDate,
            )
        binding.ivSearchResultThumbnail.load(video.content.creator.profileImage) {
            crossfade(true)
            transformations(CircleCropTransformation())
        }
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
