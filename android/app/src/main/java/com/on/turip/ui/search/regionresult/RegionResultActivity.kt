package com.on.turip.ui.search.regionresult

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.on.turip.R
import com.on.turip.databinding.ActivityRegionResultBinding
import com.on.turip.ui.common.base.BaseActivity
import com.on.turip.ui.common.model.RegionModel
import com.on.turip.ui.trip.detail.TripDetailActivity

class RegionResultActivity : BaseActivity<ActivityRegionResultBinding>() {
    val viewModel: RegionResultViewModel by viewModels {
        RegionResultViewModel.provideFactory(
            intent.getStringExtra(REGION_KEY) ?: "",
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

            supportActionBar?.title = setupTitle(searchResultState.region)

            regionResultAdapter.submitList(searchResultState.videoInformations)

            binding.rvRegionResult.isVisible = searchResultState.isExist == true
            binding.groupRegionResultEmpty.isVisible = searchResultState.isExist == false
            binding.tvRegionResultLoading.isVisible = searchResultState.loading == true
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
            Intent(context, RegionResultActivity::class.java)
                .putExtra(REGION_KEY, region)
    }
}
