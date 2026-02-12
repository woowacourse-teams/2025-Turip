package com.on.turip.ui.main.bookmarks

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.on.turip.R
import com.on.turip.databinding.ItemFavoriteContentBinding
import com.on.turip.domain.bookmark.BookmarkContent
import com.on.turip.domain.content.Content
import com.on.turip.domain.content.video.VideoData
import com.on.turip.domain.creator.Creator
import com.on.turip.ui.common.TuripUrlConverter
import com.on.turip.ui.common.extensions.loadRoundedCornerImage
import com.on.turip.ui.common.mapper.toUiModel

class FavoriteContentViewHolder(
    private val binding: ItemFavoriteContentBinding,
    private val onFavoriteContentListener: FavoriteContentListener,
) : RecyclerView.ViewHolder(binding.root) {
    private var bookmarkContent: BookmarkContent? = null

    init {
        itemView.setOnClickListener {
            bookmarkContent?.content?.let { content: Content ->
                onFavoriteContentListener.onFavoriteItemClick(
                    content.id,
                )
            }
        }
        binding.ivFavoriteContentFavorite.setOnClickListener {
            bookmarkContent?.let {
                onFavoriteContentListener.onFavoriteClick(
                    it.content.id,
                    it.content.isBookmarked,
                )
            }
        }
    }

    fun bind(bookmarkContent: BookmarkContent) {
        this.bookmarkContent = bookmarkContent
        val content: Content = bookmarkContent.content
        val videoData: VideoData = content.videoData
        val creator: Creator = content.creator

        binding.apply {
            tvFavoriteContentTitle.text = videoData.title

            tvFavoriteContentCreatorNameAndUploadDate.text =
                itemView.context.getString(
                    R.string.region_result_video_description,
                    creator.channelName,
                    videoData.uploadedDate,
                )

            tvFavoriteContentRegion.text = content.city.name

            tvFavoriteContentTotalPlaceCount.text =
                itemView.context.getString(
                    R.string.all_total_place_count,
                    bookmarkContent.tripPlaceCount,
                )

            tvFavoriteContentTripDuration.text =
                bookmarkContent.tripDuration.toUiModel().toDisplayText(itemView.context)

            ivFavoriteContentVideoThumbnail.loadRoundedCornerImage(
                imageUrl = TuripUrlConverter.convertVideoThumbnailUrl(videoData.url),
                radius = 10,
            )

            ivFavoriteContentFavorite.isSelected = content.isBookmarked
        }
    }

    companion object {
        fun of(
            parent: ViewGroup,
            favoriteContentListener: FavoriteContentListener,
        ): FavoriteContentViewHolder {
            val inflater: LayoutInflater = LayoutInflater.from(parent.context)
            val binding: ItemFavoriteContentBinding =
                ItemFavoriteContentBinding.inflate(inflater, parent, false)
            return FavoriteContentViewHolder(binding, favoriteContentListener)
        }
    }

    interface FavoriteContentListener {
        fun onFavoriteClick(
            contentId: Long,
            isFavorite: Boolean,
        )

        fun onFavoriteItemClick(contentId: Long)
    }
}
