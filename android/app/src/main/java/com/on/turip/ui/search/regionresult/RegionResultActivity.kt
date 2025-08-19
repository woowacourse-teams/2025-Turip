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
        RegionResultAdapter { contentId: Long?, creatorId: Long? ->
            val intent: Intent =
                TripDetailActivity.newIntent(
                    context = this,
                    contentId = contentId ?: 0,
                    creatorId = creatorId ?: 0,
                )
            startActivity(intent)
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setupToolbar()
        setupAdapters()
        setupObservers()
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

            setupVisible(searchResultState)
        }
    }

    private fun setupVisible(searchResultState: RegionResultViewModel.SearchResultState) {
        binding.rvRegionResult.visibility =
            if (searchResultState.isExist) View.VISIBLE else View.GONE

        binding.groupRegionResultEmpty.visibility =
            if (searchResultState.isExist) View.GONE else View.VISIBLE

        binding.tvRegionResultLoading.visibility =
            if (searchResultState.loading) View.VISIBLE else View.GONE
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
