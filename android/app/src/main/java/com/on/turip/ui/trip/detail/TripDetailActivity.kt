package com.on.turip.ui.trip.detail

import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.graphics.Bitmap
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.core.widget.NestedScrollView
import com.google.android.material.snackbar.Snackbar
import com.on.turip.R
import com.on.turip.databinding.ActivityTripDetailBinding
import com.on.turip.domain.content.Content
import com.on.turip.ui.common.TuripSnackbar
import com.on.turip.ui.common.TuripUrlConverter
import com.on.turip.ui.common.base.BaseActivity
import com.on.turip.ui.common.loadCircularImage
import com.on.turip.ui.common.model.trip.TripModel
import com.on.turip.ui.common.model.trip.toDisplayText
import com.on.turip.ui.trip.detail.webview.TuripWebChromeClient
import com.on.turip.ui.trip.detail.webview.WebViewVideoBridge
import com.on.turip.ui.trip.detail.webview.applyVideoSettings

class TripDetailActivity : BaseActivity<ActivityTripDetailBinding>() {
    override val binding: ActivityTripDetailBinding by lazy {
        ActivityTripDetailBinding.inflate(layoutInflater)
    }

    val viewModel: TripDetailViewModel by viewModels {
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
            val intent: Intent = Intent(Intent.ACTION_VIEW, placeModel.placeUri)
            startActivity(intent)
        }
    }

    private var originalVideoContainer: CardView? = null
    private var stickyVideoContainer: CardView? = null
    private var nestedScrollView: NestedScrollView? = null
    private var stickyThreshold = 0
    private var currentVideoUrl: String? = null
    private var isVideoLoaded = false
    private var isVideoInStickyMode = false

    private fun enableFullscreen() {
        WindowCompat.setDecorFitsSystemWindows(this.window, false)
        WindowInsetsControllerCompat(window, window.decorView).apply {
            hide(WindowInsetsCompat.Type.systemBars())
            systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }

        binding.tbTripDetail.visibility = View.GONE
        binding.nsvTripDetail.visibility = View.GONE

        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
    }

    private fun disableFullscreen() {
        WindowCompat.setDecorFitsSystemWindows(this.window, true)
        WindowInsetsControllerCompat(window, window.decorView).apply {
            show(WindowInsetsCompat.Type.systemBars())
            systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }

        binding.tbTripDetail.visibility = View.VISIBLE
        binding.nsvTripDetail.visibility = View.VISIBLE

        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setupToolbar()
        setupOnBackPressedDispatcher()
        setupWebView()
        setupStickyVideo()
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
        binding.tbTripDetail.navigationIcon?.setTint(
            ContextCompat.getColor(
                this,
                R.color.gray_300_5b5b5b,
            ),
        )
    }

    private fun setupOnBackPressedDispatcher() {
        onBackPressedDispatcher.addCallback(
            this,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    when {
                        turipWebChromeClient.isFullScreen() -> {
                            turipWebChromeClient.onHideCustomView()
                        }

                        binding.wvTripDetailVideo.canGoBack() -> {
                            binding.wvTripDetailVideo.goBack()
                        }

                        else -> {
                            finish()
                        }
                    }
                }
            },
        )
    }

    private fun setupWebView() {
        binding.wvTripDetailVideo.apply {
            applyVideoSettings()
            webChromeClient = turipWebChromeClient
            webViewClient = createWebViewClient()
        }
    }

    private fun createWebViewClient(): WebViewClient =
        object : WebViewClient() {
            override fun onPageStarted(
                view: WebView?,
                url: String?,
                favicon: Bitmap?,
            ) {
                super.onPageStarted(view, url, favicon)
                binding.pbTripDetailVideo.visibility = View.VISIBLE
            }

            override fun onPageFinished(
                view: WebView?,
                url: String?,
            ) {
                super.onPageFinished(view, url)
                binding.pbTripDetailVideo.visibility = View.GONE
                isVideoLoaded = true
            }

            override fun onReceivedError(
                view: WebView?,
                request: WebResourceRequest?,
                error: WebResourceError?,
            ) {
                super.onReceivedError(view, request, error)
                showWebViewErrorView()
            }
        }

    private fun setupStickyVideo() {
        originalVideoContainer = binding.cvTripDetailVideoContainer
        stickyVideoContainer = binding.cvTripDetailVideoContainerSticky
        nestedScrollView = binding.nsvTripDetail

        nestedScrollView?.viewTreeObserver?.addOnGlobalLayoutListener(
            object : ViewTreeObserver.OnGlobalLayoutListener {
                override fun onGlobalLayout() {
                    nestedScrollView?.viewTreeObserver?.removeOnGlobalLayoutListener(this)
                    calculateStickyThreshold()
                }
            },
        )

        nestedScrollView?.setOnScrollChangeListener { _, _, scrollY, _, _ ->
            handleStickyVideo(scrollY)
        }
    }

    private fun calculateStickyThreshold() {
        originalVideoContainer?.post {
            val location = IntArray(2)
            originalVideoContainer?.getLocationInWindow(location)

            val scrollViewLocation = IntArray(2)
            nestedScrollView?.getLocationInWindow(scrollViewLocation)

            stickyThreshold = location[1] - scrollViewLocation[1]
        }
    }

    private fun handleStickyVideo(scrollY: Int) {
        if (!isVideoLoaded || currentVideoUrl.isNullOrEmpty()) {
            return
        }

        if (scrollY >= stickyThreshold && !isVideoInStickyMode) {
            showStickyVideo()
            isVideoInStickyMode = true
        } else if (scrollY < stickyThreshold && isVideoInStickyMode) {
            hideStickyVideo()
            isVideoInStickyMode = false
        }
    }

    private fun showStickyVideo() {
        val webView = binding.wvTripDetailVideo
        val layoutParams = webView.layoutParams

        (webView.parent as? ViewGroup)?.removeView(webView)

        stickyVideoContainer?.addView(webView, layoutParams)

        originalVideoContainer?.visibility = View.INVISIBLE

        stickyVideoContainer?.visibility = View.VISIBLE
        stickyVideoContainer?.alpha = 0f
        stickyVideoContainer
            ?.animate()
            ?.alpha(1f)
            ?.setDuration(200)
            ?.start()
    }

    private fun hideStickyVideo() {
        val webView = binding.wvTripDetailVideo
        val layoutParams = webView.layoutParams

        stickyVideoContainer?.removeView(webView)

        originalVideoContainer?.addView(webView, layoutParams)
        originalVideoContainer?.visibility = View.VISIBLE

        stickyVideoContainer
            ?.animate()
            ?.alpha(0f)
            ?.setDuration(200)
            ?.withEndAction {
                stickyVideoContainer?.visibility = View.GONE
            }?.start()
    }

    private fun showWebViewErrorView() {
        runOnUiThread {
            binding.wvTripDetailVideo.visibility = View.GONE
            binding.clTripDetailVideoError.visibility = View.VISIBLE
            binding.pbTripDetailVideo.visibility = View.GONE
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
        binding.clTripDetailVideoError.setOnClickListener {
            val intent: Intent =
                Intent(
                    Intent.ACTION_VIEW,
                    viewModel.videoUri.value?.toUri(),
                )
            startActivity(intent)
        }

        binding.ivTripDetailFavorite.setOnClickListener {
            viewModel.updateFavorite()
            showFavoriteStatusSnackbar(viewModel.isFavorite.value == true)
        }

        binding.ivTripDetailContentToggle.setOnClickListener {
            viewModel.updateExpandTextToggle()
        }
    }

    private fun showFavoriteStatusSnackbar(isFavorite: Boolean) {
        val messageResource: Int =
            if (isFavorite) R.string.trip_detail_snackbar_favorite_save else R.string.trip_detail_snackbar_favorite_remove
        val iconResource: Int =
            if (isFavorite) R.drawable.ic_heart_normal else R.drawable.ic_heart_empty

        TuripSnackbar
            .make(
                rootView = binding.root,
                messageResource = messageResource,
                duration = Snackbar.LENGTH_LONG,
                layoutInflater = layoutInflater,
            ).topMarginInCoordinatorLayout(binding.tbTripDetail.height)
            .icon(iconResource)
            .action(R.string.all_snackbar_close)
            .show()
    }

    private fun setupObservers() {
        viewModel.days.observe(this) { days: List<DayModel> ->
            tripDayAdapter.submitList(days)
        }
        viewModel.places.observe(this) { places: List<PlaceModel> ->
            tripPlaceAdapter.submitList(places)
            binding.tvTripDetailDayPlaceCount.text =
                getString(R.string.trip_detail_day_place_count, places.size)
        }
        viewModel.content.observe(this) { content: Content ->
            binding.ivTripDetailCreatorThumbnail.loadCircularImage(
                content.creator.profileImage,
            )
            binding.tvTripDetailCreatorName.text = content.creator.channelName
            binding.tvTripDetailContentTitle.text = content.videoData.title
            binding.tvTripDetailUploadDate.text = content.videoData.uploadedDate
            updateExpandTextToggleVisibility()
        }
        viewModel.tripModel.observe(this) { tripModel: TripModel ->
            binding.tvTripDetailTotalPlaceCount.text =
                getString(R.string.all_total_place_count, tripModel.tripPlaceCount)
            binding.tvTripDetailTravelDuration.text =
                tripModel.tripDurationModel.toDisplayText(this)
        }
        viewModel.videoUri.observe(this) { url: String ->
            currentVideoUrl = url

            binding.wvTripDetailVideo.apply {
                addJavascriptInterface(
                    WebViewVideoBridge(
                        TuripUrlConverter.extractVideoId(url),
                    ) { showWebViewErrorView() },
                    BRIDGE_NAME_IN_JS_FILE,
                )
                loadUrl(LOAD_URL_FILE_PATH)
            }
        }
        viewModel.isFavorite.observe(this) { isFavorite: Boolean ->
            binding.ivTripDetailFavorite.isSelected = isFavorite
        }
        viewModel.isExpandTextToggleVisible.observe(this) { isVisible ->
            binding.ivTripDetailContentToggle.visibility =
                if (isVisible) View.VISIBLE else View.GONE
        }

        viewModel.isExpandTextToggleSelected.observe(this) { isSelected ->
            binding.ivTripDetailContentToggle.isSelected = isSelected
        }

        viewModel.bodyMaxLines.observe(this) { maxLines ->
            binding.tvTripDetailContentTitle.maxLines = maxLines
        }
    }

    private fun updateExpandTextToggleVisibility() {
        if (viewModel.isExpandTextToggleVisible.value == true) return

        val bodyTextView: TextView = binding.tvTripDetailContentTitle
        bodyTextView.post {
            val lineCount: Int = bodyTextView.layout.lineCount
            val ellipsisCount: Int = bodyTextView.layout.getEllipsisCount(lineCount - 1)
            viewModel.updateExpandTextToggleVisibility(lineCount, ellipsisCount)
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
