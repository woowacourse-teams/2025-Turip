package com.on.turip.ui.search.detail

import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import androidx.core.net.toUri
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.on.turip.databinding.ActivitySearchDetailBinding
import com.on.turip.ui.common.base.BaseActivity
import com.on.turip.ui.search.detail.webview.TuripWebChromeClient
import com.on.turip.ui.search.detail.webview.TuripWebViewClient
import com.on.turip.ui.search.detail.webview.WebViewVideoBridge
import com.on.turip.ui.search.detail.webview.applyVideoSettings

class SearchDetailActivity : BaseActivity<SearchDetailViewModel, ActivitySearchDetailBinding>() {
    override val viewModel: SearchDetailViewModel by viewModels()
    override val binding: ActivitySearchDetailBinding by lazy {
        ActivitySearchDetailBinding.inflate(layoutInflater)
    }
    private lateinit var turipWebChromeClient: TuripWebChromeClient
    private val travelDayAdapter: TravelDayAdapter =
        TravelDayAdapter { dayModel ->
            viewModel.selectDay(dayModel)
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setupToolbar()
        setupWebView()
        setupBindings()
        setupListeners()
        setupObservers()
    }

    override fun onDestroy() {
        binding.wvSearchDetailVideo.destroy()
        super.onDestroy()
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.tbSearchDetail)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = null

        onBackPressedDispatcher.addCallback(
            this,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    if (turipWebChromeClient.isFullScreen()) {
                        turipWebChromeClient.onHideCustomView()
                    } else if (binding.wvSearchDetailVideo.canGoBack()) {
                        binding.wvSearchDetailVideo.goBack()
                    } else {
                        finish()
                    }
                }
            },
        )
    }

    private fun setupWebView() {
        turipWebChromeClient =
            TuripWebChromeClient(
                fullScreenView = binding.flSearchDetailVideoFullscreen,
                onEnterFullScreen = ::enableFullscreen,
                onExitFullScreen = ::disableFullscreen,
            )

        binding.wvSearchDetailVideo.apply {
            applyVideoSettings()
            webChromeClient = turipWebChromeClient
            webViewClient = TuripWebViewClient(binding.pbSearchDetailVideo)
            addJavascriptInterface(
                // TODO 생성자에 url의 일부 추출 필요, WebViewUtils에 구현한 String.extractVideoId() 사용 예정
                WebViewVideoBridge("") { showWebViewErrorView() },
                BRIDGE_NAME_IN_JS_FILE,
            )
        }
        binding.wvSearchDetailVideo.loadUrl(LOAD_URL_FILE_PATH)
    }

    private fun enableFullscreen() {
        WindowCompat.setDecorFitsSystemWindows(this.window, false)
        WindowInsetsControllerCompat(window, window.decorView).apply {
            hide(WindowInsetsCompat.Type.systemBars())
            systemBarsBehavior =
                WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }

        binding.tbSearchDetail.visibility = View.GONE
        binding.nsvSearchDetail.visibility = View.GONE

        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
    }

    private fun disableFullscreen() {
        WindowCompat.setDecorFitsSystemWindows(this.window, true)
        WindowInsetsControllerCompat(window, window.decorView).apply {
            show(WindowInsetsCompat.Type.systemBars())
            systemBarsBehavior =
                WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }

        binding.tbSearchDetail.visibility = View.VISIBLE
        binding.nsvSearchDetail.visibility = View.VISIBLE

        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
    }

    private fun showWebViewErrorView() {
        runOnUiThread {
            binding.wvSearchDetailVideo.visibility = View.GONE
            binding.tvSearchDetailVideoError.visibility = View.VISIBLE
        }
    }

    private fun setupBindings() {
        binding.rvSearchDetailTravelDay.adapter = travelDayAdapter
    }

    private fun setupListeners() {
        binding.tvSearchDetailVideoError.setOnClickListener {
            val intent: Intent =
                Intent(
                    Intent.ACTION_VIEW,
                    "".toUri(),
                ) // TODO "" 에는 유튜브 영상 uri가 들어가야 한다.
            startActivity(intent)
        }
    }

    private fun setupObservers() {
        viewModel.days.observe(this) { dayModels ->
            travelDayAdapter.submitList(dayModels)
        }
    }

    companion object {
        private const val BRIDGE_NAME_IN_JS_FILE = "videoBridge"
        private const val LOAD_URL_FILE_PATH = "file:///android_asset/iframe.html"
    }
}
