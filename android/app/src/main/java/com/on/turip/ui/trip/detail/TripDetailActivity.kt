package com.on.turip.ui.trip.detail

import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.google.android.material.snackbar.Snackbar
import com.on.turip.R
import com.on.turip.databinding.ActivityTripDetailBinding
import com.on.turip.ui.common.TuripSnackbar
import com.on.turip.ui.common.base.BaseActivity
import com.on.turip.ui.common.collectOnStarted
import com.on.turip.ui.common.error.ErrorUiModel
import com.on.turip.ui.common.error.ErrorUiState
import com.on.turip.ui.common.error.toUiModel
import com.on.turip.ui.common.loadCircularImage
import com.on.turip.ui.common.model.trip.toDisplayText
import com.on.turip.ui.login.LoginActivity
import com.on.turip.ui.main.favorite.FavoriteBottomSheetContainerFragment
import com.on.turip.ui.search.keywordresult.SearchActivity
import com.on.turip.ui.trip.detail.webview.TuripWebChromeClient
import com.on.turip.ui.trip.detail.webview.TuripWebViewClient
import com.on.turip.ui.trip.detail.webview.applyVideoSettings
import com.on.turip.ui.trip.detail.webview.navigateToTimeLine
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TripDetailActivity : BaseActivity<ActivityTripDetailBinding>() {
    override val binding: ActivityTripDetailBinding by lazy {
        ActivityTripDetailBinding.inflate(layoutInflater)
    }

    private val videoManager by lazy { VideoManager(binding.wvTripDetailVideo) }

    private val viewModel: TripDetailViewModel by viewModels()

    private val turipWebChromeClient: TuripWebChromeClient by lazy {
        TuripWebChromeClient(
            fullScreenView = binding.flTripDetailVideoFullscreen,
            onEnterFullScreen = ::enableFullscreen,
            onExitFullScreen = ::disableFullscreen,
        )
    }

    private val turipWebViewClient: TuripWebViewClient by lazy {
        TuripWebViewClient(progressBar = binding.pbTripDetailVideo)
    }

    private val tripDayAdapter by lazy {
        TripDayAdapter { dayModel -> viewModel.updateDay(dayModel) }
    }

    private val tripPlaceAdapter by lazy {
        TripPlaceAdapter(
            object : TripPlaceViewHolder.PlaceListener {
                override fun onItemClick(placeModel: PlaceModel) {
                    val intent: Intent =
                        SearchActivity.newIntent(this@TripDetailActivity, placeModel.name)
                    startActivity(intent)
                }

                override fun onPlaceClick(placeModel: PlaceModel) {
                    val intent: Intent = Intent(Intent.ACTION_VIEW, placeModel.placeUri)
                    startActivity(intent)
                }

                override fun onTimeLineClick(placeModel: PlaceModel) {
                    binding.wvTripDetailVideo.navigateToTimeLine(placeModel.contentTimeLine)
                }

                override fun onFavoriteClick(placeModel: PlaceModel) {
                    if (supportFragmentManager.findFragmentByTag("favorite_place_folder") == null) {
                        val bottomSheet: FavoriteBottomSheetContainerFragment =
                            FavoriteBottomSheetContainerFragment.instance(placeModel.id)
                        bottomSheet.show(supportFragmentManager, "favorite_place_folder")
                    }
                }
            },
        )
    }

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
            ContextCompat.getColor(this, R.color.gray_300_5b5b5b),
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
            webViewClient = turipWebViewClient
        }
    }

    private fun showWebViewErrorView() {
        runOnUiThread {
            binding.wvTripDetailVideo.visibility = View.GONE
            binding.clTripDetailVideoError.visibility = View.VISIBLE
            binding.pbTripDetailVideo.visibility = View.GONE
        }
    }

    private fun setupAdapters() {
        binding.rvTripDetailTripDay.adapter = tripDayAdapter
        binding.rvTripDetailTripPlace.apply {
            adapter = tripPlaceAdapter
            itemAnimator = null
        }
    }

    private fun setupListeners() {
        binding.clTripDetailVideoError.setOnClickListener {
            val intent: Intent =
                Intent(
                    Intent.ACTION_VIEW,
                    viewModel.uiState.value.tripDetailInfo
                        ?.videoLink
                        ?.toUri(),
                )
            startActivity(intent)
        }

        binding.ivTripDetailFavorite.setOnClickListener {
            viewModel.updateFavorite()
        }

        binding.ivTripDetailContentToggle.setOnClickListener {
            viewModel.updateExpandTextToggle()
            binding.tvTripDetailContentTitle.maxLines = viewModel.uiState.value.titleMaxLines
        }
    }

    private fun setupObservers() {
        collectOnStarted(viewModel.uiState) { uiState: TripDetailUiState ->
            when {
                // 로딩
                uiState.isLoading -> showLoading()

                // 에러
                uiState.errorUiState != ErrorUiState.None -> showErrorView(uiState.errorUiState)

                // 정상
                else -> showContents(uiState)
            }
        }

        collectOnStarted(viewModel.uiEffect) { uiEffect: TripDetailUiEffect ->
            when (uiEffect) {
                is TripDetailUiEffect.ShowFavoriteStatus -> showFavoriteStatusSnackbar(uiEffect.isFavorite)
                TripDetailUiEffect.NavigateToLogin -> navigateToLoginScreen()
                is TripDetailUiEffect.ShowError -> {
                    val uiModel: ErrorUiModel =
                        uiEffect.errorUiState.toUiModel() ?: return@collectOnStarted
                    Snackbar
                        .make(binding.root, uiModel.titleRes, Snackbar.LENGTH_INDEFINITE)
                        .apply {
                            setAction(uiModel.retryTextRes) {
                                viewModel.onErrorRetryRequested(uiEffect.action)
                            }
                        }.show()
                }
            }
        }
    }

    private fun showLoading() {
        binding.pbTripDetailScreenLoading.visibility = View.VISIBLE
        binding.cvTripDetailVideoContainer.visibility = View.GONE
        binding.ivTripDetailFavorite.visibility = View.GONE
        binding.nsvTripDetail.visibility = View.GONE
        binding.customErrorView.visibility = View.GONE
    }

    private fun showErrorView(errorUiState: ErrorUiState) {
        binding.customErrorView.visibility = View.VISIBLE
        binding.pbTripDetailScreenLoading.visibility = View.GONE
        binding.cvTripDetailVideoContainer.visibility = View.GONE
        binding.ivTripDetailFavorite.visibility = View.GONE
        binding.nsvTripDetail.visibility = View.GONE

        binding.customErrorView.apply {
            visibility = View.VISIBLE
            showErrorView(errorUiState)
            setOnRetryClickListener { viewModel.loadTripDetails() }
        }
    }

    private fun showContents(uiState: TripDetailUiState) {
        tripDayAdapter.submitList(uiState.days)
        tripPlaceAdapter.submitList(uiState.places)

        binding.ivTripDetailContentToggle.isSelected =
            uiState.isExpandTextToggleSelected
        binding.ivTripDetailFavorite.isSelected = uiState.isFavorite
        binding.tvTripDetailDayPlaceCount.text =
            getString(R.string.trip_detail_day_place_count, uiState.places.size)
        binding.tvTripDetailContentTitle.maxLines = uiState.titleMaxLines

        uiState.tripDetailInfo?.let { tripDetail: TripDetailInfoModel ->
            binding.ivTripDetailCreatorThumbnail.loadCircularImage(tripDetail.creatorThumbnail)
            binding.tvTripDetailCreatorName.text = tripDetail.creatorName
            binding.tvTripDetailContentTitle.text = tripDetail.contentTitle
            binding.tvTripDetailInfo.text =
                getString(
                    R.string.trip_detail_info,
                    tripDetail.uploadedDate,
                    tripDetail.placeTotalCount,
                    tripDetail.duration.toDisplayText(this),
                )
            if (!viewModel.videoLoaded) {
                videoManager.loadVideo(tripDetail.videoLink) { showWebViewErrorView() }
                viewModel.updateVideoLoadStatus(isLoaded = true)
            }
        }

        binding.cvTripDetailVideoContainer.visibility = View.VISIBLE
        binding.ivTripDetailFavorite.visibility = View.VISIBLE
        binding.nsvTripDetail.visibility = View.VISIBLE
        binding.customErrorView.visibility = View.GONE
        binding.pbTripDetailScreenLoading.visibility = View.GONE
        binding.ivTripDetailContentToggle.visibility =
            if (uiState.isExpandTextToggleVisible == true) View.VISIBLE else View.GONE

        judgeExpandTextToggleVisibility(uiState.isExpandTextToggleVisible)
    }

    /**
     * isVisible == null일 때만 Visibility 판단
     */
    private fun judgeExpandTextToggleVisibility(isVisible: Boolean?) {
        if (isVisible == null) {
            val bodyTextView: TextView = binding.tvTripDetailContentTitle
            bodyTextView.post {
                val lineCount: Int = bodyTextView.layout.lineCount
                val ellipsisCount: Int = bodyTextView.layout.getEllipsisCount(lineCount - 1)
                viewModel.updateExpandTextToggleVisibility(lineCount, ellipsisCount)
            }
        }
    }

    private fun showFavoriteStatusSnackbar(isFavorite: Boolean) {
        val messageResource: Int =
            if (isFavorite) R.string.trip_detail_snackbar_favorite_save else R.string.trip_detail_snackbar_favorite_remove
        val iconResource: Int =
            if (isFavorite) R.drawable.ic_heart_pressed else R.drawable.ic_heart_empty

        TuripSnackbar
            .make(
                rootView = binding.root,
                message = getString(messageResource),
                duration = Snackbar.LENGTH_LONG,
                layoutInflater = layoutInflater,
            ).topMarginInCoordinatorLayout(binding.tbTripDetail.height)
            .icon(iconResource)
            .action(R.string.all_snackbar_close)
            .show()
    }

    private fun navigateToLoginScreen() {
        val intent: Intent =
            LoginActivity
                .newIntent(this)
                .apply { flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK }
        startActivity(intent)
        finish()
    }

    override fun onDestroy() {
        binding.wvTripDetailVideo.destroy()
        videoManager.clear()
        super.onDestroy()
    }

    companion object {
        const val TRIP_DETAIL_CONTENT_KEY: String = "com.on.turip.TRIP_DETAIL_CONTENT_KEY"

        fun newIntent(
            context: Context,
            contentId: Long,
        ): Intent =
            Intent(context, TripDetailActivity::class.java)
                .putExtra(TRIP_DETAIL_CONTENT_KEY, contentId)
    }
}
