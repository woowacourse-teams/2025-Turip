package com.on.turip.ui.search.keywordresult

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.compose.ui.platform.ViewCompositionStrategy
import com.on.turip.databinding.ActivitySearchBinding
import com.on.turip.ui.common.base.BaseActivity
import com.on.turip.ui.compose.designsystem.theme.TuripTheme
import com.on.turip.ui.compose.search.SearchScreen
import com.on.turip.ui.login.LoginActivity
import com.on.turip.ui.trip.detail.TripDetailActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SearchActivity : BaseActivity<ActivitySearchBinding>() {
    private val viewModel: SearchViewModel by viewModels()

    override val binding: ActivitySearchBinding by lazy {
        ActivitySearchBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initComposeView()
    }

    private fun initComposeView() {
        binding.cvSearch.apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)

            setContent {
                TuripTheme {
                    SearchScreen(
                        viewModel = viewModel,
                        onNavigateBack = { finish() },
                        onNavigateToDetail = { contentId ->
                            startActivity(
                                TripDetailActivity.newIntent(
                                    this@SearchActivity,
                                    contentId,
                                ),
                            )
                        },
                        onNavigateToLogin = {
                            navigateToLoginScreen()
                        },
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
        const val SEARCH_KEYWORD_KEY: String = "com.on.turip.SEARCH_KEYWORD_KEY"

        fun newIntent(
            context: Context,
            searchKeyword: String,
        ): Intent =
            Intent(context, SearchActivity::class.java).apply {
                putExtra(SEARCH_KEYWORD_KEY, searchKeyword)
            }
    }
}
