package com.on.turip.ui.search.regionresult

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import com.on.turip.R
import com.on.turip.databinding.ActivityRegionResultBinding
import com.on.turip.domain.ErrorEvent
import com.on.turip.ui.common.base.BaseActivity
import com.on.turip.ui.trip.detail.TripDetailActivity
import timber.log.Timber

class RegionResultActivity : BaseActivity<ActivityRegionResultBinding>() {
    val viewModel: RegionResultViewModel by viewModels {
        val regionCategoryName: String = intent.getStringExtra(REGION_CATEGORY_NAME_KEY) ?: ""
        Timber.d("선택한 지역 카테고리: $regionCategoryName")
        RegionResultViewModel.provideFactory(
            regionCategoryName = regionCategoryName,
        )
    }
    override val binding: ActivityRegionResultBinding by lazy {
        ActivityRegionResultBinding.inflate(layoutInflater)
    }
    private val regionResultAdapter: RegionResultAdapter =
        RegionResultAdapter { contentId: Long? ->
            val intent: Intent =
                TripDetailActivity.newIntent(
                    context = this,
                    contentId = contentId ?: 0,
                )
            startActivity(intent)
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setupToolbar()
        setupAdapters()
        setupObservers()
        showNetworkError()
    }

    private fun showNetworkError() {
        binding.customErrorView.apply {
            visibility = View.VISIBLE
            setupError(ErrorEvent.NETWORK_ERROR)
            setOnRetryClickListener {
                viewModel.reload()
            }
        }
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.tbRegionResult)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        binding.tbRegionResult.navigationIcon?.setTint(
            ContextCompat.getColor(
                this,
                R.color.gray_300_5b5b5b,
            ),
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
        binding.rvRegionResult.adapter = regionResultAdapter
    }

    private fun setupObservers() {
        viewModel.searchResultState.observe(this) { searchResultState: RegionResultViewModel.SearchResultState ->
            binding.tvRegionResultCount.text =
                getString(
                    R.string.region_result_exist_result,
                    searchResultState.searchResultCount,
                )
            supportActionBar?.title = searchResultState.region
            regionResultAdapter.submitList(searchResultState.videoInformations)
            updateUIVisibility()
        }

        viewModel.networkError.observe(this) {
            updateUIVisibility()
        }

        viewModel.serverError.observe(this) {
            updateUIVisibility()
        }
    }

    private fun updateUIVisibility() {
        val searchResultState = viewModel.searchResultState.value
        val hasNetworkError = viewModel.networkError.value == true
        val hasServerError = viewModel.serverError.value == true

        when {
            hasNetworkError || hasServerError -> {
                binding.customErrorView.visibility = View.VISIBLE
                binding.pbSearchRegionResult.visibility = View.GONE
                binding.groupRegionResultEmpty.visibility = View.GONE
                binding.tvRegionResultCount.visibility = View.GONE
                binding.rvRegionResult.visibility = View.GONE
            }

            searchResultState?.loading == true -> {
                binding.customErrorView.visibility = View.GONE
                binding.pbSearchRegionResult.visibility = View.VISIBLE
                binding.groupRegionResultEmpty.visibility = View.GONE
                binding.tvRegionResultCount.visibility = View.GONE
                binding.rvRegionResult.visibility = View.GONE
            }

            searchResultState?.isExist == true -> {
                binding.customErrorView.visibility = View.GONE
                binding.pbSearchRegionResult.visibility = View.GONE
                binding.groupRegionResultEmpty.visibility = View.GONE
                binding.tvRegionResultCount.visibility = View.VISIBLE
                binding.rvRegionResult.visibility = View.VISIBLE
            }

            else -> {
                binding.customErrorView.visibility = View.GONE
                binding.pbSearchRegionResult.visibility = View.GONE
                binding.groupRegionResultEmpty.visibility = View.VISIBLE
                binding.tvRegionResultCount.visibility = View.GONE
                binding.rvRegionResult.visibility = View.GONE
            }
        }
    }

    companion object {
        private const val REGION_CATEGORY_NAME_KEY: String = "com.on.turip.REGION_CATEGORY_NAME_KEY"

        fun newIntent(
            context: Context,
            regionCategoryName: String,
        ): Intent =
            Intent(context, RegionResultActivity::class.java)
                .putExtra(REGION_CATEGORY_NAME_KEY, regionCategoryName)
    }
}
