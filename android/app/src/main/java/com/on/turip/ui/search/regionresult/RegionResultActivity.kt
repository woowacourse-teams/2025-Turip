package com.on.turip.ui.search.regionresult

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import com.on.turip.R
import com.on.turip.data.common.ErrorUiState
import com.on.turip.databinding.ActivityRegionResultBinding
import com.on.turip.ui.common.base.BaseActivity
import com.on.turip.ui.common.collectOnStarted
import com.on.turip.ui.common.event.CommonUiEffect
import com.on.turip.ui.login.LoginActivity
import com.on.turip.ui.trip.detail.TripDetailActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RegionResultActivity : BaseActivity<ActivityRegionResultBinding>() {
    private val viewModel: RegionResultViewModel by viewModels()
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
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.tbRegionResult)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        binding.tbRegionResult.navigationIcon?.setTint(
            ContextCompat.getColor(this, R.color.gray_300_5b5b5b),
        )
        supportActionBar?.title = viewModel.regionCategoryName
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
        collectOnStarted(viewModel.uiState) { uiState: RegionResultUiState ->
            when (uiState) {
                RegionResultUiState.Loading -> renderLoading()
                RegionResultUiState.Empty -> renderEmptyView()
                is RegionResultUiState.Success -> renderContents(uiState)
                is RegionResultUiState.Error -> renderErrorView(uiState.errorUiState)
            }
        }

        collectOnStarted(viewModel.commonUiEffect) { uiEffect: CommonUiEffect ->
            when (uiEffect) {
                CommonUiEffect.NavigateToLogin -> navigateToLoginScreen()
            }
        }
    }

    private fun renderLoading() {
        binding.pbRegionResult.visibility = View.VISIBLE
        binding.groupRegionResultEmpty.visibility = View.GONE
        binding.groupRegionResultNotEmpty.visibility = View.GONE
        binding.customErrorView.visibility = View.GONE
    }

    private fun renderEmptyView() {
        binding.groupRegionResultEmpty.visibility = View.VISIBLE
        binding.groupRegionResultNotEmpty.visibility = View.GONE
        binding.pbRegionResult.visibility = View.GONE
        binding.customErrorView.visibility = View.GONE
    }

    private fun renderContents(uiState: RegionResultUiState.Success) {
        regionResultAdapter.submitList(uiState.videos)

        binding.groupRegionResultNotEmpty.visibility = View.VISIBLE
        binding.groupRegionResultEmpty.visibility = View.GONE
        binding.pbRegionResult.visibility = View.GONE
        binding.customErrorView.visibility = View.GONE

        binding.tvRegionResultCount.text =
            getString(R.string.region_result_exist_result, uiState.totalCount)
    }

    private fun renderErrorView(errorUiState: ErrorUiState) {
        binding.groupRegionResultEmpty.visibility = View.GONE
        binding.groupRegionResultNotEmpty.visibility = View.GONE
        binding.pbRegionResult.visibility = View.GONE

        binding.customErrorView.apply {
            visibility = View.VISIBLE
            render(errorUiState)
            setOnRetryClickListener { viewModel.reload() }
        }
    }

    private fun navigateToLoginScreen() {
        val intent: Intent =
            LoginActivity
                .newIntent(this)
                .apply { flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK }
        startActivity(intent)
        finish()
    }

    companion object {
        const val REGION_RESULT_REGION_CATEGORY_NAME_KEY: String =
            "com.on.turip.REGION_RESULT_REGION_CATEGORY_NAME_KEY"

        fun newIntent(
            context: Context,
            regionCategoryName: String,
        ): Intent =
            Intent(context, RegionResultActivity::class.java)
                .putExtra(REGION_RESULT_REGION_CATEGORY_NAME_KEY, regionCategoryName)
    }
}
