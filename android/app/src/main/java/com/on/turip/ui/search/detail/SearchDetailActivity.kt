package com.on.turip.ui.search.detail

import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.View
import android.webkit.WebView
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
    private lateinit var webView: WebView
    private lateinit var turipWebChromeClient: TuripWebChromeClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setupToolbar()
        setupBindings()
        setupWebView()
        setupListeners()
    }

    override fun onDestroy() {
        webView.destroy()
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
                    } else if (webView.canGoBack()) {
                        webView.goBack()
                    } else {
                        finish()
                    }
                }
            },
        )
    }

    private fun setupBindings() {
        webView = binding.wvSearchDetailVideo
    }

    private fun setupWebView() {
        turipWebChromeClient =
            TuripWebChromeClient(
                fullScreenView = binding.flSearchDetailVideoFullscreen,
                onEnterFullScreen = ::enableFullscreen,
                onExitFullScreen = ::disableFullscreen,
            )

        webView.applyVideoSettings()
        webView.webChromeClient = turipWebChromeClient
        webView.webViewClient = TuripWebViewClient(binding.pbSearchDetailVideo)
        webView.addJavascriptInterface(
            // TODO 생성자에 url의 일부 추출 필요, WebViewUtils에 구현한 String.extractVideoId() 사용 예정
            WebViewVideoBridge("") { showWebViewErrorView() },
            BRIDGE_NAME_IN_JS_FILE,
        )
        webView.loadUrl(LOAD_URL_FILE_PATH)
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

    private fun setupListeners() {
        binding.tvSearchDetailVideoError.setOnClickListener {
            val intent =
                Intent(
                    Intent.ACTION_VIEW,
                    "".toUri(),
                ) // TODO "" 에는 유튜브 영상 uri가 들어가야 한다.
            startActivity(intent)
        }
    }

    companion object {
        private const val BRIDGE_NAME_IN_JS_FILE = "videoBridge"
        private const val LOAD_URL_FILE_PATH = "file:///android_asset/iframe.html"
    }
}
