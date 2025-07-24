package com.on.turip.ui.trip.detail

import android.content.Context
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
import com.on.turip.databinding.ActivityTripDetailBinding
import com.on.turip.ui.common.base.BaseActivity
import com.on.turip.ui.trip.detail.TravelDetailViewModel.TripDetailViewModel
import com.on.turip.ui.trip.detail.webview.TuripWebChromeClient
import com.on.turip.ui.trip.detail.webview.TuripWebViewClient
import com.on.turip.ui.trip.detail.webview.WebViewVideoBridge
import com.on.turip.ui.trip.detail.webview.applyVideoSettings

class TripDetailActivity : BaseActivity<TripDetailViewModel, ActivityTripDetailBinding>() {
    override val binding: ActivityTripDetailBinding by lazy {
        ActivityTripDetailBinding.inflate(layoutInflater)
    }
    override val viewModel: TripDetailViewModel by viewModels {
        TripDetailViewModel.provideFactory(
            intent.getLongExtra(CONTENT_KEY, 0),
            intent.getLongExtra(CREATOR_KEY, 0),
        )
    }
    private val turipWebChromeClient: TuripWebChromeClient by lazy {
        TuripWebChromeClient(
            fullScreenView = binding.flTripDetailVideoFullscreen,
            onEnterFullScreen = ::enableFullscreen,
            onExitFullScreen = ::disableFullscreen,
        )
    }

    private val tripDayAdapter by lazy {
        TripDayAdapter { dayModel ->
            viewModel.updateDay(dayModel)
        }
    }

    private val tripPlaceAdapter by lazy {
        TripPlaceAdapter { placeModel ->
            val intent = Intent(Intent.ACTION_VIEW, placeModel.placeUri)
            startActivity(intent)
        }
    }

    private fun enableFullscreen() {
        WindowCompat.setDecorFitsSystemWindows(this.window, false)
        WindowInsetsControllerCompat(window, window.decorView).apply {
            hide(WindowInsetsCompat.Type.systemBars())
            systemBarsBehavior =
                WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }

        binding.tbTripDetail.visibility = View.GONE
        binding.nsvTripDetail.visibility = View.GONE

        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
    }

    private fun disableFullscreen() {
        WindowCompat.setDecorFitsSystemWindows(this.window, true)
        WindowInsetsControllerCompat(window, window.decorView).apply {
            show(WindowInsetsCompat.Type.systemBars())
            systemBarsBehavior =
                WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }

        binding.tbTripDetail.visibility = View.VISIBLE
        binding.nsvTripDetail.visibility = View.VISIBLE

        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setupToolbar()
        setupOnBackPressedDispatcher()
        setupWebView()
        setupAdapters()
        setupListeners()
        setupObservers()
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.tbTripDetail)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            title = null
        }
    }

    private fun setupOnBackPressedDispatcher() {
        onBackPressedDispatcher.addCallback(
            this,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    if (turipWebChromeClient.isFullScreen()) {
                        turipWebChromeClient.onHideCustomView()
                    } else if (binding.wvTripDetailVideo.canGoBack()) {
                        binding.wvTripDetailVideo.goBack()
                    } else {
                        finish()
                    }
                }
            },
        )
    }

    private fun setupWebView() {
        binding.wvTripDetailVideo.apply {
            applyVideoSettings()
            webChromeClient = turipWebChromeClient
            webViewClient = TuripWebViewClient(binding.pbTripDetailVideo)
            addJavascriptInterface(
                // TODO 생성자에 url의 일부 추출 필요, WebViewUtils에 구현한 String.extractVideoId() 사용 예정
                WebViewVideoBridge("") { showWebViewErrorView() },
                BRIDGE_NAME_IN_JS_FILE,
            )
        }
        binding.wvTripDetailVideo.loadUrl(LOAD_URL_FILE_PATH)
    }

    private fun showWebViewErrorView() {
        runOnUiThread {
            binding.wvTripDetailVideo.visibility = View.GONE
            binding.tvTripDetailVideoError.visibility = View.VISIBLE
        }
    }

    private fun setupAdapters() {
        binding.rvTripDetailTravelDay.adapter = tripDayAdapter
        binding.rvTripDetailTravelPlace.apply {
            adapter = tripPlaceAdapter
            itemAnimator = null
        }
    }

    private fun setupListeners() {
        binding.tvTripDetailVideoError.setOnClickListener {
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
            tripDayAdapter.submitList(dayModels)
        }
        viewModel.places.observe(this) { placeModels ->
            tripPlaceAdapter.submitList(placeModels)
        }
    }

    override fun onDestroy() {
        binding.wvTripDetailVideo.destroy()
        super.onDestroy()
    }

    companion object {
        private const val BRIDGE_NAME_IN_JS_FILE = "videoBridge"
        private const val LOAD_URL_FILE_PATH = "file:///android_asset/iframe.html"
        private const val CREATOR_KEY: String = "com.on.turip.CREATOR_KEY"
        private const val CONTENT_KEY: String = "com.on.turip.CONTENT_KEY"

        fun newIntent(
            context: Context,
            contentId: Long,
            creatorId: Long,
        ): Intent =
            Intent(context, TripDetailActivity::class.java)
                .putExtra(CONTENT_KEY, contentId)
                .putExtra(CREATOR_KEY, creatorId)
    }
}
