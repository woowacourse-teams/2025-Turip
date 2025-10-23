package com.on.turip.ui.search.regionresult

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

class RegionResultViewHolder(
    private val binding: ItemResultBinding,
    private val onSearchResultListener: OnSearchResultListener,
) : RecyclerView.ViewHolder(binding.root) {
    private var content: Content? = null

    init {
        itemView.setOnClickListener {
            onSearchResultListener.onSearchResultClick(content?.id ?: 0)
        }
    }

    fun bind(videoInformationModel: VideoInformationModel) {
        content = videoInformationModel.content
        binding.apply {
            tvResultTitle.text = videoInformationModel.content.videoData.title
            tvResultDay.text =
                videoInformationModel.tripModel.tripDurationModel.toDisplayText(itemView.context)
            tvResultLocation.text =
                itemView.context.getString(
                    R.string.all_total_place_count,
                    videoInformationModel.tripModel.tripPlaceCount,
                )
            tvResultRegionName.text = videoInformationModel.content.city.name
            tvResultDescription.text =
                itemView.context.getString(
                    R.string.all_video_description,
                    videoInformationModel.content.creator.channelName,
                    videoInformationModel.content.videoData.uploadedDate,
                )
            ivResultCreatorThumbnail.loadCircularImage(videoInformationModel.content.creator.profileImage)
            ivResultVideoThumbnail.loadRoundedCornerImage(
                imageUrl = TuripUrlConverter.convertVideoThumbnailUrl(videoInformationModel.content.videoData.url),
                radius = 4,
            )
        }
    }

    companion object {
        fun of(
            parent: ViewGroup,
            onSearchResultListener: OnSearchResultListener,
        ): RegionResultViewHolder {
            val inflater: LayoutInflater = LayoutInflater.from(parent.context)
            val binding: ItemResultBinding =
                ItemResultBinding.inflate(inflater, parent, false)
            return RegionResultViewHolder(binding, onSearchResultListener)
        }
    }

    fun interface OnSearchResultListener {
        fun onSearchResultClick(contentId: Long)
    }
}
