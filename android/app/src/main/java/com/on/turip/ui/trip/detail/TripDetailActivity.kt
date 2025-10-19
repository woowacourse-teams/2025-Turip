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
import com.on.turip.domain.ErrorEvent
import com.on.turip.domain.content.Content
import com.on.turip.ui.common.TuripSnackbar
import com.on.turip.ui.common.base.BaseActivity
import com.on.turip.ui.common.loadCircularImage
import com.on.turip.ui.common.model.trip.toDisplayText
import com.on.turip.ui.main.favorite.FavoriteBottomSheetContainerFragment
import com.on.turip.ui.search.keywordresult.SearchActivity
import com.on.turip.ui.trip.detail.webview.TuripWebChromeClient
import com.on.turip.ui.trip.detail.webview.TuripWebViewClient
import com.on.turip.ui.trip.detail.webview.applyVideoSettings
import com.on.turip.ui.trip.detail.webview.navigateToTimeLine

class TripDetailActivity : BaseActivity<ActivityTripDetailBinding>() {
    override val binding: ActivityTripDetailBinding by lazy {
        ActivityTripDetailBinding.inflate(layoutInflater)
    }

    private val videoManager by lazy {
        VideoManager(binding.wvTripDetailVideo)
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

    private val turipWebViewClient: TuripWebViewClient by lazy {
        TuripWebViewClient(progressBar = binding.pbTripDetailVideo)
    }

    private val tripDayAdapter by lazy {
        TripDayAdapter { dayModel ->
            viewModel.updateDay(dayModel)
        }
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
        showNetworkError()
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
                message = getString(messageResource),
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
            updateExpandTextToggleVisibility()
        }
        viewModel.tripDetailInfo.observe(this) { tripDetailInfo ->
            binding.tvTripDetailInfo.text =
                getString(
                    R.string.trip_detail_info,
                    tripDetailInfo.uploadedDate,
                    tripDetailInfo.placeCount,
                    tripDetailInfo.duration.toDisplayText(this),
                )
        }
        viewModel.videoUri.observe(this) { url: String ->
            videoManager.loadVideo(url) {
                showWebViewErrorView()
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
        viewModel.networkError.observe(this) { networkError: Boolean ->
            binding.nsvTripDetail.visibility =
                if (networkError) View.GONE else View.VISIBLE
            binding.tbTripDetail.visibility =
                if (networkError) View.GONE else View.VISIBLE
            binding.customErrorView.visibility =
                if (networkError) View.VISIBLE else View.GONE
        }
        viewModel.serverError.observe(this) { serverError: Boolean ->
            binding.nsvTripDetail.visibility =
                if (serverError) View.GONE else View.VISIBLE
            binding.tbTripDetail.visibility =
                if (serverError) View.GONE else View.VISIBLE
            binding.customErrorView.visibility =
                if (serverError) View.VISIBLE else View.GONE
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

    private fun showNetworkError() {
        binding.customErrorView.apply {
            visibility = View.VISIBLE
            setupError(ErrorEvent.NETWORK_ERROR)
            setOnRetryClickListener {
                viewModel.reload()
            }
        }
    }

    override fun onDestroy() {
        binding.wvTripDetailVideo.destroy()
        videoManager.clear()
        super.onDestroy()
    }

    companion object {
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
