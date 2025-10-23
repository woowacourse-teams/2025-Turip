package com.on.turip.ui.search.keywordresult

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.on.turip.R
import com.on.turip.databinding.ItemResultBinding
import com.on.turip.domain.content.Content
import com.on.turip.ui.common.TuripUrlConverter
import com.on.turip.ui.common.loadCircularImage
import com.on.turip.ui.common.loadRoundedCornerImage
import com.on.turip.ui.common.model.trip.toDisplayText
import com.on.turip.ui.search.model.VideoInformationModel

class SearchViewHolder(
    private val binding: ItemResultBinding,
    private val onSearchResultListener: OnSearchResultListener,
) : RecyclerView.ViewHolder(binding.root) {
    private var content: Content? = null

    init {
        itemView.setOnClickListener {
            content?.let { content ->
                onSearchResultListener.onSearchResultClick(
                    content.id,
                )
            }
        }
    }

    fun bind(videoInformationModel: VideoInformationModel) {
        content = videoInformationModel.content
        binding.tvResultTitle.text = videoInformationModel.content.videoData.title
        binding.tvResultDay.text =
            videoInformationModel.tripModel.tripDurationModel.toDisplayText(itemView.context)
        binding.tvResultLocation.text =
            itemView.context.getString(
                R.string.all_total_place_count,
                videoInformationModel.tripModel.tripPlaceCount,
            )
        binding.tvResultDescription.text =
            itemView.context.getString(
                R.string.region_result_video_description,
                videoInformationModel.content.creator.channelName,
                videoInformationModel.content.videoData.uploadedDate,
            )
        binding.ivResultCreatorThumbnail.loadCircularImage(videoInformationModel.content.creator.profileImage)
        binding.ivResultVideoThumbnail.loadRoundedCornerImage(
            TuripUrlConverter.convertVideoThumbnailUrl(
                videoInformationModel.content.videoData.url,
            ),
            12,
        )
        binding.tvResultRegionName.text = videoInformationModel.content.city.name
    }

    companion object {
        fun of(
            parent: ViewGroup,
            onSearchResultListener: OnSearchResultListener,
        ): SearchViewHolder {
            val inflater: LayoutInflater = LayoutInflater.from(parent.context)
            val binding: ItemResultBinding =
                ItemResultBinding.inflate(inflater, parent, false)
            return SearchViewHolder(binding, onSearchResultListener)
        }
    }

    fun interface OnSearchResultListener {
        fun onSearchResultClick(contentId: Long)
    }
}
