package com.on.turip.ui.search.result

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.on.turip.R
import com.on.turip.databinding.ItemSearchResultBinding
import com.on.turip.domain.content.Content
import com.on.turip.domain.creator.Creator
import com.on.turip.ui.common.loadCircularImage
import com.on.turip.ui.common.model.trip.toDisplayText
import com.on.turip.ui.search.model.VideoInformationModel

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

    fun bind(videoInformationModel: VideoInformationModel) {
        creator = videoInformationModel.content.creator
        content = videoInformationModel.content
        binding.tvSearchResultTitle.text = videoInformationModel.content.videoData.title
        binding.tvSearchResultDay.text =
            videoInformationModel.tripModel.tripDurationModel.toDisplayText(itemView.context)
        binding.tvSearchResultLocation.text =
            itemView.context.getString(
                R.string.all_total_place_count,
                videoInformationModel.tripModel.tripPlaceCount,
            )
        binding.tvSearchResultDescription.text =
            itemView.context.getString(
                R.string.search_result_video_description,
                videoInformationModel.content.creator.channelName,
                videoInformationModel.content.videoData.uploadedDate,
            )
        binding.ivSearchResultThumbnail.loadCircularImage(videoInformationModel.content.creator.profileImage)
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
