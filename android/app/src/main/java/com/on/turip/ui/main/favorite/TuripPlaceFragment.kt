package com.on.turip.ui.main.favorite

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.snackbar.Snackbar
import com.on.turip.R
import com.on.turip.databinding.FragmentFavoritePlaceBinding
import com.on.turip.ui.common.TuripDialogFragment
import com.on.turip.ui.common.base.BaseFragment
import com.on.turip.ui.common.error.ErrorUiModel
import com.on.turip.ui.common.error.ErrorUiState
import com.on.turip.ui.common.error.toUiModel
import com.on.turip.ui.common.extensions.collectOnStarted
import com.on.turip.ui.common.extensions.safeStartActivityWithToast
import com.on.turip.ui.folder.TuripActivity
import com.on.turip.ui.login.LoginActivity
import com.on.turip.ui.main.favorite.model.PlaceLatLngUiModel
import com.on.turip.ui.main.favorite.model.TuripModel
import com.on.turip.ui.main.favorite.model.TuripPlaceUiEffect
import com.on.turip.ui.main.favorite.model.TuripPlaceUiModel
import com.on.turip.ui.main.favorite.model.TuripPlaceUiState
import com.on.turip.ui.main.favorite.model.TuripShareModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TuripPlaceFragment :
    BaseFragment<FragmentFavoritePlaceBinding>(),
    OnMapReadyCallback {
    private val viewModel: TuripPlaceViewModel by viewModels()
    private val folderNameAdapter: FavoritePlaceFolderNameAdapter by lazy {
        FavoritePlaceFolderNameAdapter { folderId: Long ->
            viewModel.updateTuripWithPlaces(folderId)
        }
    }
    private val placeAdapter: FavoritePlaceAdapter by lazy {
        FavoritePlaceAdapter(
            object : FavoritePlaceViewHolder.FavoritePlaceListener {
                override fun onFavoriteClick(
                    placeId: Long,
                    isFavorite: Boolean,
                ) {
                    viewModel.updateTuripPlace(placeId, isFavorite)
                }

                override fun onMapClick(uri: Uri) {
                    val intent = Intent(Intent.ACTION_VIEW, uri)
                    requireContext().safeStartActivityWithToast(
                        intent = intent,
                        errorToastMessage = getString(R.string.all_snackbar_not_found_map_url),
                    )
                }

                override fun onItemClick(turipPlaceUiModel: TuripPlaceUiModel) {
                    map.animateCamera(
                        CameraUpdateFactory.newCameraPosition(
                            CameraPosition(turipPlaceUiModel.latLng, 15f, 0f, 0f),
                        ),
                        1000,
                        null,
                    )
                    markerMap[turipPlaceUiModel.placeId]?.showInfoWindow()
                }
            },
            onCommit = { viewModel.updateTuripPlacesOrder(it) },
        )
    }

    private lateinit var map: GoogleMap
    private val markerMap = mutableMapOf<Long, Marker>()

    override fun inflateBinding(
        inflater: LayoutInflater,
        container: ViewGroup?,
    ): FragmentFavoritePlaceBinding = FragmentFavoritePlaceBinding.inflate(inflater, container, false)

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)

        setupAdapters()
        setupListeners()
        setupObservers()
        setupMapFragment(savedInstanceState)
        setupLoginSuggestDialog()
    }

    private fun setupAdapters() {
        binding.rvFavoritePlaceFolderName.apply {
            adapter = folderNameAdapter
            itemAnimator = null
            addOnItemTouchListener(RecyclerViewTouchInterceptor)
        }

        binding.rvFavoritePlacePlace.adapter = placeAdapter

        val itemTouchHelper =
            ItemTouchHelper(
                object : ItemTouchHelper.SimpleCallback(
                    ItemTouchHelper.UP or ItemTouchHelper.DOWN,
                    0,
                ) {
                    override fun onMove(
                        recyclerView: RecyclerView,
                        viewHolder: RecyclerView.ViewHolder,
                        target: RecyclerView.ViewHolder,
                    ): Boolean {
                        val from = viewHolder.bindingAdapterPosition
                        val to = target.bindingAdapterPosition
                        placeAdapter.moveItem(from, to)
                        return true
                    }

                    override fun onSwiped(
                        viewHolder: RecyclerView.ViewHolder,
                        direction: Int,
                    ) = Unit

                    override fun isLongPressDragEnabled(): Boolean = true

                    override fun interpolateOutOfBoundsScroll(
                        recyclerView: RecyclerView,
                        viewSize: Int,
                        viewSizeOutOfBounds: Int,
                        totalSize: Int,
                        msSinceStartScroll: Long,
                    ): Int = viewSizeOutOfBounds / 10

                    override fun clearView(
                        recyclerView: RecyclerView,
                        viewHolder: RecyclerView.ViewHolder,
                    ) {
                        super.clearView(recyclerView, viewHolder)
                        placeAdapter.commitMove()
                    }
                },
            )

        itemTouchHelper.attachToRecyclerView(binding.rvFavoritePlacePlace)
    }

    private fun setupListeners() {
        binding.ivFavoritePlaceFolder.setOnClickListener {
            val intent: Intent = TuripActivity.newIntent(requireContext())
            startActivity(intent)
        }
        binding.ivFavoritePlaceShare.setOnClickListener {
            viewModel.shareTurip()
        }

        binding.ivFavoritePlaceMapToggle.setOnClickListener {
            val isMapVisible = binding.mvFavoritePlace.isVisible
            binding.mvFavoritePlace.visibility = if (isMapVisible) View.GONE else View.VISIBLE
            it.isSelected = isMapVisible
        }
    }

    private fun setupObservers() {
        collectOnStarted(viewModel.uiState) { uiState: TuripPlaceUiState ->
            if (uiState.isLoading) showLoading()
            when {
                uiState.errorUiState != ErrorUiState.None -> showErrorView(uiState.errorUiState)
                uiState.isEmpty -> showEmptyView(uiState.turips)
                else -> showContents(uiState)
            }
        }

        collectOnStarted(viewModel.uiEffect) { uiEffect: TuripPlaceUiEffect ->
            when (uiEffect) {
                TuripPlaceUiEffect.ShowTuripShareNotAllowed -> {
                    showSuggestLoginMessage()
                }

                is TuripPlaceUiEffect.ShareTurip -> {
                    shareFolder(uiEffect.turipShareModel)
                }

                TuripPlaceUiEffect.NavigateToLogin -> {
                    navigateToLoginScreen()
                }

                is TuripPlaceUiEffect.ShowError -> {
                    val uiModel: ErrorUiModel =
                        uiEffect.errorUiState.toUiModel() ?: return@collectOnStarted
                    view?.let { view: View ->
                        Snackbar
                            .make(view, uiModel.titleRes, Snackbar.LENGTH_INDEFINITE)
                            .apply {
                                setAction(uiModel.retryTextRes) {
                                    viewModel.handleErrorRetryRequest(uiEffect.retryAction)
                                }
                            }.show()
                    }
                }
            }
        }
    }

    private fun showSuggestLoginMessage() {
        TuripDialogFragment
            .newInstance(
                title = getString(R.string.turip_dialog_login_suggest_title),
                description = getString(R.string.turip_dialog_login_suggest_description),
                confirmText = getString(R.string.turip_dialog_login_suggest_confirm),
                dismissText = getString(R.string.turip_dialog_login_suggest_dismiss),
            ).show(parentFragmentManager, TuripDialogFragment::class.java.simpleName)
    }

    private fun navigateToLoginScreen() {
        val intent: Intent =
            LoginActivity
                .newIntent(requireActivity())
                .apply { flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK }
        startActivity(intent)
        requireActivity().finish()
    }

    private fun shareFolder(turipShareModel: TuripShareModel) {
        val sharedContents: String = turipShareModel.toShareFormat()

        val intent =
            Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_TEXT, sharedContents)
                putExtra(Intent.EXTRA_TITLE, turipShareModel.name)
            }
        val kakaoIntent: Intent =
            Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                `package` = KAKAO_PACKAGE
                putExtra(Intent.EXTRA_TEXT, sharedContents)
            }
        val instagramIntent: Intent =
            Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                `package` = INSTAGRAM_PACKAGE
                putExtra(Intent.EXTRA_TEXT, sharedContents)
            }
        val initialIntents = arrayOf(kakaoIntent, instagramIntent)

        val chooserIntent =
            Intent.createChooser(intent, turipShareModel.name).apply {
                putExtra(Intent.EXTRA_INITIAL_INTENTS, initialIntents)
                putExtra(Intent.EXTRA_TITLE, turipShareModel.name)
            }

        startActivity(chooserIntent)
    }

    private fun showLoading() {
        binding.pbFavoritePlaceLoading.visibility = View.VISIBLE
        binding.groupFavoritePlaceNotEmpty.visibility = View.GONE
        binding.groupFavoritePlaceNotError.visibility = View.GONE
        binding.clFavoritePlaceEmpty.visibility = View.GONE
        binding.customErrorView.visibility = View.GONE
    }

    private fun showEmptyView(folders: List<TuripModel>) {
        folderNameAdapter.submitList(folders)

        binding.pbFavoritePlaceLoading.visibility = View.GONE
        binding.groupFavoritePlaceNotError.visibility = View.VISIBLE
        binding.customErrorView.visibility = View.GONE

        binding.clFavoritePlaceEmpty.visibility = View.VISIBLE
        binding.groupFavoritePlaceNotEmpty.visibility = View.GONE

        binding.ivFavoritePlaceMapToggle.visibility = View.GONE
        binding.mvFavoritePlace.visibility = View.GONE
    }

    private fun showContents(uiState: TuripPlaceUiState) {
        folderNameAdapter.submitList(uiState.turips)
        placeAdapter.submitList(uiState.places)

        binding.pbFavoritePlaceLoading.visibility = View.GONE
        binding.groupFavoritePlaceNotError.visibility = View.VISIBLE
        binding.customErrorView.visibility = View.GONE

        binding.clFavoritePlaceEmpty.visibility = View.GONE
        binding.groupFavoritePlaceNotEmpty.visibility = View.VISIBLE
        binding.tvFavoritePlacePlaceCount.text =
            getString(R.string.all_total_place_count, uiState.places.size)

        when (uiState.placesLatLng.size) {
            0 -> handleEmptyFavorites()
            1 -> handleSingleFavorite(uiState.placesLatLng.first())
            else -> handleMultipleFavorites(uiState.placesLatLng)
        }
    }

    private fun showErrorView(errorUiState: ErrorUiState) {
        binding.groupFavoritePlaceNotError.visibility = View.GONE
        binding.groupFavoritePlaceNotEmpty.visibility = View.GONE
        binding.clFavoritePlaceEmpty.visibility = View.GONE
        binding.pbFavoritePlaceLoading.visibility = View.GONE

        binding.customErrorView.apply {
            visibility = View.VISIBLE
            showErrorView(errorUiState)
            setOnRetryClickListener { viewModel.loadTuripsAndPlaces() }
        }
    }

    override fun onStart() {
        super.onStart()
        binding.mvFavoritePlace.onStart()
    }

    override fun onPause() {
        binding.mvFavoritePlace.onPause()
        super.onPause()
    }

    override fun onStop() {
        binding.mvFavoritePlace.onStop()
        super.onStop()
    }

    override fun onDestroyView() {
        binding.mvFavoritePlace.onDestroy()
        super.onDestroyView()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        binding.mvFavoritePlace.onLowMemory()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        binding.mvFavoritePlace.onSaveInstanceState(outState)
    }

    override fun onResume() {
        super.onResume()
        binding.mvFavoritePlace.onResume()
        viewModel.loadTuripsAndPlaces()
    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        if (!hidden) {
            viewModel.loadTuripsAndPlaces()
        }
    }

    private fun setupMapFragment(savedInstanceState: Bundle?) {
        binding.mvFavoritePlace.onCreate(savedInstanceState)
        binding.mvFavoritePlace.getMapAsync { googleMap ->
            googleMap.setOnCameraMoveStartedListener { reason ->
                if (reason == GoogleMap.OnCameraMoveStartedListener.REASON_GESTURE) {
                    binding.root.parent?.requestDisallowInterceptTouchEvent(true)
                }
            }

            googleMap.setOnCameraIdleListener {
                binding.root.parent?.requestDisallowInterceptTouchEvent(false)
            }

            onMapReady(googleMap)
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        map.uiSettings.isZoomControlsEnabled = true
    }

    private fun handleEmptyFavorites() {
        binding.mvFavoritePlace.visibility = View.GONE
        binding.ivFavoritePlaceMapToggle.visibility = View.GONE
        markerMap.clear()
    }

    private fun handleSingleFavorite(place: PlaceLatLngUiModel) {
        showMap()
        clearMapMarkers()

        addMarkerToMap(place)
        map.setOnMapLoadedCallback {
            map.moveCamera(
                CameraUpdateFactory.newLatLngZoom(place.latLng, 15f),
            )
        }
    }

    private fun handleMultipleFavorites(places: List<PlaceLatLngUiModel>) {
        showMap()
        clearMapMarkers()

        val boundsBuilder = LatLngBounds.Builder()

        places.forEach { place ->
            addMarkerToMap(place)
            boundsBuilder.include(place.latLng)
        }

        val bounds = boundsBuilder.build()

        map.setOnMapLoadedCallback {
            map.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 100))
        }
    }

    private fun showMap() {
        binding.ivFavoritePlaceMapToggle.visibility = View.VISIBLE
        binding.mvFavoritePlace.visibility = View.VISIBLE
        binding.ivFavoritePlaceMapToggle.isSelected = false
    }

    private fun clearMapMarkers() {
        map.clear()
        markerMap.clear()
    }

    private fun addMarkerToMap(place: PlaceLatLngUiModel) {
        val marker =
            map.addMarker(
                MarkerOptions()
                    .position(place.latLng)
                    .title(place.name),
            )
        marker?.let {
            markerMap[place.placeId] = it
        }
    }

    private fun setupLoginSuggestDialog() {
        parentFragmentManager.setFragmentResultListener(
            TuripDialogFragment.REQUEST_KEY,
            viewLifecycleOwner,
        ) { _, bundle ->
            when (bundle.getString(TuripDialogFragment.TURIP_DIALOG_RESULT)) {
                TuripDialogFragment.RESULT_CONFIRM -> {
                    navigateToLoginScreen()
                }
            }
        }
    }

    companion object {
        private const val KAKAO_PACKAGE = "com.kakao.talk"
        private const val INSTAGRAM_PACKAGE = "com.instagram.android"

        fun instance(): TuripPlaceFragment = TuripPlaceFragment()
    }
}

private object RecyclerViewTouchInterceptor : RecyclerView.OnItemTouchListener {
    override fun onInterceptTouchEvent(
        recyclerView: RecyclerView,
        motionEvent: MotionEvent,
    ): Boolean {
        when (motionEvent.action) {
            MotionEvent.ACTION_DOWN -> {
                recyclerView.parent.requestDisallowInterceptTouchEvent(true)
            }

            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                recyclerView.parent.requestDisallowInterceptTouchEvent(false)
            }
        }
        return false
    }

    override fun onTouchEvent(
        recyclerView: RecyclerView,
        motionEvent: MotionEvent,
    ) {
    }

    override fun onRequestDisallowInterceptTouchEvent(disallowIntercept: Boolean) {}
}
