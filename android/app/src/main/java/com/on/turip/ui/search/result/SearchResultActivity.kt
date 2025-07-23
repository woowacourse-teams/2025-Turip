package com.on.turip.ui.search.result

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import androidx.core.view.isVisible
import com.on.turip.R
import com.on.turip.databinding.ActivitySearchResultBinding
import com.on.turip.ui.common.base.BaseActivity
import com.on.turip.ui.common.model.RegionModel

class SearchResultActivity : BaseActivity<SearchResultViewModel, ActivitySearchResultBinding>() {
    override val viewModel: SearchResultViewModel by viewModels {
        SearchResultViewModel.provideFactory(
            intent.getStringExtra(REGION_KEY) ?: "",
        )
    }
    override val binding: ActivitySearchResultBinding by lazy {
        ActivitySearchResultBinding.inflate(layoutInflater)
    }
    private val searchResultAdapter: SearchResultAdapter =
        SearchResultAdapter {
            // TODO : 여행 상세 페이지로 이동
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setupToolbar()
        setupAdapters()
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
        binding.rvSearchResult.adapter = searchResultAdapter
    }

    private fun setupObservers() {
        viewModel.searchResultState.observe(this) { searchResultState: SearchResultViewModel.SearchResultState ->
            binding.tvSearchResultCount.text =
                getString(
                    R.string.search_result_exist_result,
                    searchResultState.searchResultCount,
                )

            supportActionBar?.title = setupTitle(searchResultState.region)

            searchResultAdapter.submitList(searchResultState.videos)

            binding.rvSearchResult.isVisible = searchResultState.isExist == true
            binding.groupEmptyResult.isVisible = searchResultState.isExist == false
        }
    }

    private fun setupTitle(region: String): String {
        val regionOrigin = RegionModel.findByEnglish(region)
        return regionOrigin.korean
    }

    companion object {
        private const val REGION_KEY: String = "com.on.turip.REGION_KEY"

        fun newIntent(
            context: Context,
            region: String,
        ): Intent =
            Intent(context, SearchResultActivity::class.java)
                .putExtra(REGION_KEY, region)
    }
}
