package com.on.turip.ui.main.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.on.turip.R
import com.on.turip.databinding.ItemUsersLikeContentBinding
import com.on.turip.domain.content.Content
import com.on.turip.domain.content.PopularFavoriteContent
import com.on.turip.ui.common.loadRoundedCornerImage

class UsersLikeContentViewHolder(
    private val binding: ItemUsersLikeContentBinding,
    onContentListener: OnContentListener,
) : RecyclerView.ViewHolder(binding.root) {
    private var content: Content? = null

    init {
        itemView.setOnClickListener {
            content?.let { onContentListener.onContentClick(it) }
        }
    }

    fun bind(popularFavoriteContent: PopularFavoriteContent) {
        this.content = popularFavoriteContent.content
        binding.apply {
            tvUsersLikeContentTitle.text = popularFavoriteContent.content.videoData.title
            tvUsersLikeContentRegion.text = popularFavoriteContent.city
            tvUsersLikeContentChannelName.text = popularFavoriteContent.content.creator.channelName
            ivUsersLikeContentThumbnail.loadRoundedCornerImage(
                imageUrl = popularFavoriteContent.content.videoData.url,
                radius = 12,
            )
            tvUsersLikeContentDescription.text =
                itemView.context.getString(
                    R.string.all_video_description,
                    popularFavoriteContent.content.videoData.uploadedDate,
                    itemView.context.getString(
                        R.string.all_travel_duration,
                        popularFavoriteContent.trip.tripDuration.nights,
                        popularFavoriteContent.trip.tripDuration.days,
                    ),
                )
        }
    }

    companion object {
        fun of(
            parent: ViewGroup,
            onContentListener: OnContentListener,
        ): UsersLikeContentViewHolder {
            val binding: ItemUsersLikeContentBinding =
                ItemUsersLikeContentBinding.inflate(
                    LayoutInflater.from(parent.context),
                )
            return UsersLikeContentViewHolder(binding, onContentListener)
        }
    }

    fun interface OnContentListener {
        fun onContentClick(content: Content)
    }
}
