package com.on.turip.ui.search.regionresult

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.on.turip.ui.compose.designsystem.theme.TuripTheme
import com.on.turip.ui.compose.search.regionresult.RegionResultScreen
import com.on.turip.ui.compose.search.regionresult.RegionResultViewModel
import com.on.turip.ui.login.LoginActivity
import com.on.turip.ui.trip.TripDetailActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RegionResultActivity : AppCompatActivity() {
    private val viewModel: RegionResultViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TuripTheme {
                RegionResultScreen(
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
                    viewModel = viewModel,
                )
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
