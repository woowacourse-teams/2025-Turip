package com.on.turip.ui.search.regionresult

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.on.turip.databinding.ActivityRegionResultBinding
import com.on.turip.ui.common.base.BaseActivity
import com.on.turip.ui.compose.designsystem.theme.TuripTheme
import com.on.turip.ui.compose.search.regionresult.RegionResultScreen
import com.on.turip.ui.login.LoginActivity
import com.on.turip.ui.trip.detail.TripDetailActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RegionResultActivity : BaseActivity<ActivityRegionResultBinding>() {
    private val viewModel: RegionResultViewModel by viewModels()

    override val binding: ActivityRegionResultBinding by lazy {
        ActivityRegionResultBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initComposeView()
    }

    private fun initComposeView() {
        binding.cvRegionResult.apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                val uiState by viewModel.uiState.collectAsStateWithLifecycle()
                val uiEffect by viewModel.uiEffect.collectAsStateWithLifecycle(initialValue = null)

                TuripTheme {
                    RegionResultScreen(
                        regionName = viewModel.regionCategoryName,
                        uiState = uiState,
                        uiEffect = uiEffect,
                        onBackClick = { finish() },
                        onItemClick = { contentId ->
                            startActivity(
                                TripDetailActivity.newIntent(
                                    this@RegionResultActivity,
                                    contentId,
                                ),
                            )
                        },
                        onNavigateToLogin = { navigateToLoginScreen() },
                        onRetryClick = { viewModel.loadContentsFromRegion() },
                    )
                }
            }
        }
    }

    private fun navigateToLoginScreen() {
        val intent =
            LoginActivity
                .newIntent(this)
                .apply { flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK }
        startActivity(intent)
        finish()
    }

    companion object {
        const val REGION_RESULT_REGION_CATEGORY_NAME_KEY =
            "com.on.turip.REGION_RESULT_REGION_CATEGORY_NAME_KEY"

        fun newIntent(
            context: Context,
            regionCategoryName: String,
        ): Intent =
            Intent(context, RegionResultActivity::class.java)
                .putExtra(REGION_RESULT_REGION_CATEGORY_NAME_KEY, regionCategoryName)
    }
}
