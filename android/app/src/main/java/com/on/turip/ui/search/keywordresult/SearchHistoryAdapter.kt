package com.on.turip.ui.search.keywordresult

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import com.on.turip.domain.searchhistory.SearchHistory

class SearchHistoryAdapter(
    private val onSearchHistoryListener: SearchHistoryViewHolder.OnSearchHistoryListener,
) : androidx.recyclerview.widget.ListAdapter<SearchHistory, SearchHistoryViewHolder>(
        SearchHistoryDiffUtil,
    ) {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): SearchHistoryViewHolder = SearchHistoryViewHolder.of(parent, onSearchHistoryListener)

    override fun onBindViewHolder(
        holder: SearchHistoryViewHolder,
        position: Int,
    ) {
        holder.bind(getItem(position))
    }

    private object SearchHistoryDiffUtil : DiffUtil.ItemCallback<SearchHistory>() {
        override fun areItemsTheSame(
            oldItem: SearchHistory,
            newItem: SearchHistory,
        ): Boolean = oldItem.keyword == newItem.keyword

        override fun areContentsTheSame(
            oldItem: SearchHistory,
            newItem: SearchHistory,
        ): Boolean = oldItem == newItem
    }
}
