package com.on.turip.ui.main.favorite

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.on.turip.R
import com.on.turip.databinding.ItemFavoriteBinding
import com.on.turip.domain.content.Content
import com.on.turip.ui.common.TuripUrlConverter
import com.on.turip.ui.common.loadCircularImage
import com.on.turip.ui.common.loadRoundedCornerImage
import com.on.turip.ui.common.model.trip.toDisplayText
import com.on.turip.ui.search.model.VideoInformationModel

class FavoriteItemViewHolder(
    private val binding: ItemFavoriteBinding,
    private val onFavoriteItemListener: OnFavoriteItemListener,
) : RecyclerView.ViewHolder(binding.root) {
    private var videoInformationModel: VideoInformationModel? = null

    init {
        itemView.setOnClickListener {
            videoInformationModel?.content?.let { content: Content ->
                onFavoriteItemListener.onFavoriteItemClick(
                    content.id,
                    content.creator.id,
                )
            }
        }
        binding.ivFavoriteFavorite.setOnClickListener {
            videoInformationModel?.let { onFavoriteItemListener.onFavoriteClick(it.content.id) }
        }
    }

    fun bind(videoInformationModel: VideoInformationModel) {
        val content = videoInformationModel.content
        val videoData = content.videoData
        val creator = content.creator
        val tripModel = videoInformationModel.tripModel

        binding.apply {
            tvFavoriteTitle.text = videoData.title

            ivFavoriteCreatorThumbnail.loadCircularImage(creator.profileImage)

            tvFavoriteCreatorNameAndUploadDate.text =
                itemView.context.getString(
                    R.string.region_result_video_description,
                    creator.channelName,
                    videoData.uploadedDate,
                )

            tvFavoriteCity.text = content.city.name

            tvFavoriteTotalPlaceCount.text =
                itemView.context.getString(
                    R.string.all_total_place_count,
                    tripModel.tripPlaceCount,
                )

            tvFavoriteTravelDuration.text =
                tripModel.tripDurationModel.toDisplayText(itemView.context)

            ivFavoriteVideoThumbnail.loadRoundedCornerImage(
                TuripUrlConverter.convertVideoThumbnailUrl(videoData.url),
                10,
            )

            ivFavoriteFavorite.isSelected = content.isFavorite
        }
    }

    companion object {
        fun of(
            parent: ViewGroup,
            onFavoriteItemListener: OnFavoriteItemListener,
        ): FavoriteItemViewHolder {
            val inflater: LayoutInflater = LayoutInflater.from(parent.context)
            val binding: ItemFavoriteBinding = ItemFavoriteBinding.inflate(inflater, parent, false)
            return FavoriteItemViewHolder(binding, onFavoriteItemListener)
        }
    }

    interface OnFavoriteItemListener {
        fun onFavoriteClick(contentId: Long)

        fun onFavoriteItemClick(
            contentId: Long,
            creatorId: Long,
        )
    }
}
