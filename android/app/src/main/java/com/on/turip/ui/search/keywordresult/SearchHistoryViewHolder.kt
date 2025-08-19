package com.on.turip.ui.search.keywordresult

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.on.turip.databinding.ItemSearchHistoryBinding
import com.on.turip.domain.searchhistory.SearchHistory

class SearchHistoryViewHolder(
    private val binding: ItemSearchHistoryBinding,
    private val searchHistoryListener: SearchHistoryListener,
) : RecyclerView.ViewHolder(binding.root) {
    private var searchHistory: SearchHistory? = null

    init {
        itemView.setOnClickListener {
            searchHistory?.let {
                searchHistoryListener.onSearchHistoryItemClick(it.keyword)
            }
        }
        binding.ivSearchHistoryCancel.setOnClickListener {
            searchHistory?.let {
                searchHistoryListener.onSearchHistoryDeleteClick(it.keyword)
            }
        }
    }

    fun bind(searchHistory: SearchHistory) {
        binding.tvSearchHistoryKeyword.text = searchHistory.keyword
        this.searchHistory = searchHistory
    }

    companion object {
        fun of(
            parent: ViewGroup,
            searchHistoryListener: SearchHistoryListener,
        ): SearchHistoryViewHolder {
            val inflater: LayoutInflater = LayoutInflater.from(parent.context)
            val binding: ItemSearchHistoryBinding =
                ItemSearchHistoryBinding.inflate(inflater, parent, false)
            return SearchHistoryViewHolder(binding, searchHistoryListener)
        }
    }

    interface SearchHistoryListener {
        fun onSearchHistoryDeleteClick(keyword: String)

        fun onSearchHistoryItemClick(keyword: String)
    }
}
