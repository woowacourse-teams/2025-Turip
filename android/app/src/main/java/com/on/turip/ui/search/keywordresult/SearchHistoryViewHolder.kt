package com.on.turip.ui.search.keywordresult

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.on.turip.databinding.ItemSearchHistoryBinding
import com.on.turip.domain.searchhistory.SearchHistory

class SearchHistoryViewHolder(
    private val binding: ItemSearchHistoryBinding,
    private val onSearchHistoryListener: OnSearchHistoryListener,
) : RecyclerView.ViewHolder(binding.root) {
    fun bind(searchHistory: SearchHistory) {
        binding.tvSearchHistoryKeyword.text = searchHistory.keyword
        binding.ivSearchHistoryCancel.setOnClickListener {
            onSearchHistoryListener.onSearchHistoryListener(searchHistory.keyword)
        }
    }

    companion object {
        fun of(
            parent: ViewGroup,
            onSearchHistoryListener: OnSearchHistoryListener,
        ): SearchHistoryViewHolder {
            val inflater: LayoutInflater = LayoutInflater.from(parent.context)
            val binding: ItemSearchHistoryBinding =
                ItemSearchHistoryBinding.inflate(inflater, parent, false)
            return SearchHistoryViewHolder(binding, onSearchHistoryListener)
        }
    }

    fun interface OnSearchHistoryListener {
        fun onSearchHistoryListener(keyword: String)
    }
}
