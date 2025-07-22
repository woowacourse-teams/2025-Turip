package com.on.turip.ui.search.result

import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import com.on.turip.databinding.ActivitySearchResultBinding
import com.on.turip.ui.common.base.BaseActivity

class SearchResultActivity : BaseActivity<SearchResultViewModel, ActivitySearchResultBinding>() {
    override val viewModel: SearchResultViewModel by viewModels()
    override val binding: ActivitySearchResultBinding by lazy {
        ActivitySearchResultBinding.inflate(layoutInflater)
    }
    private val videosAdapter: VideosAdapter by lazy {
        VideosAdapter(
            object : VideosViewHolder.OnSearchResultListener {
                override fun onSearchResultClick() {
                    TODO("영상 상세 페이지로 이동")
                }
            },
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupToolbar()
        setupAdapters()
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.tbSearchResult)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        onBackPressedDispatcher.addCallback(
            this,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    finish()
                }
            },
        )
    }

    private fun setupAdapters() {
        binding.rvSearchResult.adapter = videosAdapter
    }
}
