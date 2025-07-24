package com.on.turip.ui.search.result

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.on.turip.R
import com.on.turip.databinding.ItemSearchResultBinding
import com.on.turip.domain.videoinfo.contents.Content
import com.on.turip.domain.videoinfo.contents.creator.Creator
import com.on.turip.domain.videoinfo.contents.video.VideoInformation
import com.on.turip.ui.common.loadCircularImage

class SearchResultViewHolder(
    private val binding: ItemSearchResultBinding,
    private val onSearchResultListener: OnSearchResultListener,
) : RecyclerView.ViewHolder(binding.root) {
    private var creator: Creator? = null
    private var content: Content? = null

    init {
        itemView.setOnClickListener {
            onSearchResultListener.onSearchResultClick(
                content?.id ?: 0,
                creator?.id ?: 0,
            )
        }
    }

    fun bind(videoInformation: VideoInformation) {
        creator = videoInformation.content.creator
        content = videoInformation.content
        binding.tvSearchResultTitle.text = videoInformation.content.videoData.title
        binding.tvSearchResultDay.text =
            itemView.context.getString(
                R.string.all_travel_duration,
                videoInformation.trip.tripDuration.nights,
                videoInformation.trip.tripDuration.days,
            )
        binding.tvSearchResultLocation.text =
            itemView.context.getString(
                R.string.all_total_place_count,
                videoInformation.trip.tripPlaceCount,
            )
        binding.tvSearchResultDescription.text =
            itemView.context.getString(
                R.string.search_result_video_description,
                videoInformation.content.creator.channelName,
                videoInformation.content.videoData.uploadedDate,
            )
        binding.ivSearchResultThumbnail.loadCircularImage(videoInformation.content.creator.profileImage)
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
        fun onSearchResultClick(
            contentId: Long,
            creatorId: Long,
        )
    }
}
