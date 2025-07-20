package com.on.turip.ui.search.detail

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.webkit.WebView
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import androidx.core.net.toUri
import com.on.turip.databinding.ActivitySearchDetailBinding
import com.on.turip.ui.common.base.BaseActivity
import com.on.turip.ui.search.detail.webview.WebViewVideoBridge
import com.on.turip.ui.search.detail.webview.applyVideoSettings

class SearchDetailActivity : BaseActivity<SearchDetailViewModel, ActivitySearchDetailBinding>() {
    override val viewModel: SearchDetailViewModel by viewModels()
    override val binding: ActivitySearchDetailBinding by lazy {
        ActivitySearchDetailBinding.inflate(layoutInflater)
    }
    private lateinit var webView: WebView

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
                    finish()
                }
            },
        )
    }

    private fun setupBindings() {
        webView = binding.wvSearchDetailVideo
    }

    private fun setupWebView() {
        webView.applyVideoSettings()
        webView.addJavascriptInterface(
            WebViewVideoBridge("") { showWebViewErrorView() }, // TODO 생성자에 url의 일부 추출 필요, WebViewUtils에 구현한 String.extractVideoId() 사용 예정
            BRIDGE_NAME_IN_JS_FILE,
        )
        webView.loadUrl(LOAD_URL_FILE_PATH)
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
                Intent(Intent.ACTION_VIEW, "".toUri()) // TODO "" 에는 유튜브 영상 uri가 들어가야 한다.
            startActivity(intent)
        }
    }

    companion object {
        private const val BRIDGE_NAME_IN_JS_FILE = "videoBridge"
        private const val LOAD_URL_FILE_PATH = "file:///android_asset/iframe.html"
    }
}
