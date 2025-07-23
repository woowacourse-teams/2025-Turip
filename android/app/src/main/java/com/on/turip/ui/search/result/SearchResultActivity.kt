package com.on.turip.ui.search.result

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import com.on.turip.R
import com.on.turip.databinding.ActivitySearchResultBinding
import com.on.turip.ui.common.base.BaseActivity
import com.on.turip.ui.common.model.RegionModel

class SearchResultActivity : BaseActivity<SearchResultViewModel, ActivitySearchResultBinding>() {
    override val viewModel: SearchResultViewModel by viewModels()
    override val binding: ActivitySearchResultBinding by lazy {
        ActivitySearchResultBinding.inflate(layoutInflater)
    }
    private val videosAdapter: VideosAdapter =
        VideosAdapter(
            object : VideosViewHolder.OnSearchResultListener {
                override fun onSearchResultClick() {
                    TODO("영상 상세 페이지로 이동")
                }
            },
        )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val region: String = intent.getStringExtra(REGION_ID) ?: return
        viewModel.setRegionFromIntent(region)

        setupToolbar()
        setupAdapters()
        setupBindings(region)
        setupObservers()
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

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    private fun setupAdapters() {
        binding.rvSearchResult.adapter = videosAdapter
    }

    private fun setupBindings(region: String) {
        val regionOrigin = RegionModel.findByEnglish(region)
        binding.tbSearchResult.title = regionOrigin.korean
    }

    private fun setupObservers() {
        viewModel.searchResultState.observe(this) { searchResultState: SearchResultViewModel.SearchResultState? ->
            binding.tvSearchResultCount.text =
                getString(
                    R.string.search_result_exist_result,
                    searchResultState?.searchResultCount ?: 0,
                )

            videosAdapter.submitList(searchResultState?.videos)
        }
    }

    companion object {
        private const val REGION_ID: String = "com.on.turip.REGION_ID"

        fun newIntent(
            context: Context,
            region: String,
        ): Intent =
            Intent(context, SearchResultActivity::class.java)
                .putExtra(REGION_ID, region)
    }
}
