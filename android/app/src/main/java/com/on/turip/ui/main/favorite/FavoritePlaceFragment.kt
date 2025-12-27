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
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.on.turip.R
import com.on.turip.data.common.UiError
import com.on.turip.databinding.FragmentFavoritePlaceBinding
import com.on.turip.ui.common.TuripDialogFragment
import com.on.turip.ui.common.base.BaseFragment
import com.on.turip.ui.common.collectOnStarted
import com.on.turip.ui.common.event.CommonUiEffect
import com.on.turip.ui.folder.FolderActivity
import com.on.turip.ui.login.LoginActivity
import com.on.turip.ui.main.favorite.model.FavoriteFolderShareModel
import com.on.turip.ui.main.favorite.model.FavoritePlaceLatLngUiModel
import com.on.turip.ui.main.favorite.model.FavoritePlaceModel
import com.on.turip.ui.main.favorite.model.FavoritePlaceUiEffect
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class FavoritePlaceFragment :
    BaseFragment<FragmentFavoritePlaceBinding>(),
    OnMapReadyCallback {
    private val viewModel: FavoritePlaceViewModel by viewModels()
    private val folderNameAdapter: FavoritePlaceFolderNameAdapter by lazy {
        FavoritePlaceFolderNameAdapter { folderId: Long ->
            viewModel.updateFolderWithPlaces(folderId)
        }
    }
    private val placeAdapter: FavoritePlaceAdapter by lazy {
        FavoritePlaceAdapter(
            object : FavoritePlaceViewHolder.FavoritePlaceListener {
                override fun onFavoriteClick(
                    placeId: Long,
                    isFavorite: Boolean,
                ) {
                    viewModel.updateFavoritePlace(placeId, isFavorite)
                }

                override fun onMapClick(uri: Uri) {
                    val intent: Intent = Intent(Intent.ACTION_VIEW, uri)
                    startActivity(intent)
                }

                override fun onItemClick(favoritePlaceModel: FavoritePlaceModel) {
                    map.animateCamera(
                        CameraUpdateFactory.newCameraPosition(
                            CameraPosition(
                                favoritePlaceModel.latLng,
                                15f,
                                0f,
                                0f,
                            ),
                        ),
                        1000,
                        null,
                    )
                    markerMap[favoritePlaceModel.placeId]?.showInfoWindow()
                }
            },
            onCommit = { viewModel.updateFavoritePlacesOrder(it) },
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
        showNetworkError()
        setupMapFragment(savedInstanceState)
        setupLoginSuggestDialog()
    }

    private fun showNetworkError() {
        binding.customErrorView.apply {
            visibility = View.VISIBLE
            render(UiError.Global.Network)
            setOnRetryClickListener {
                viewModel.loadFoldersAndPlaces()
            }
        }
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
            val intent: Intent = FolderActivity.newIntent(requireContext())
            startActivity(intent)
        }
        binding.ivFavoritePlaceShare.setOnClickListener {
            viewModel.shareFolder()
        }

        binding.ivFavoritePlaceMapToggle.setOnClickListener {
            val isMapVisible = binding.mvFavoritePlace.isVisible
            binding.mvFavoritePlace.visibility = if (isMapVisible) View.GONE else View.VISIBLE
            it.isSelected = isMapVisible
        }
    }

    private fun setupObservers() {
        viewModel.favoritePlaceUiState.observe(viewLifecycleOwner) { state ->
            folderNameAdapter.submitList(state.folders)
            placeAdapter.submitList(state.places)

            binding.apply {
                if (state.isLoading) {
                    pbSearchRegionResult.visibility = View.VISIBLE
                    clFavoritePlaceEmpty.visibility = View.GONE
                    groupFavoritePlaceNotError.visibility = View.GONE
                    groupFavoritePlaceNotEmpty.visibility = View.GONE
                    tvFavoritePlacePlaceCount.visibility = View.GONE
                } else {
                    pbSearchRegionResult.visibility = View.GONE
                    groupFavoritePlaceNotError.visibility = View.VISIBLE
                }

                if (state.isNetWorkError || state.isServerError) {
                    mvFavoritePlace.visibility = View.GONE
                    customErrorView.visibility = View.VISIBLE
                    clFavoritePlaceEmpty.visibility = View.GONE
                    groupFavoritePlaceNotError.visibility = View.GONE
                    groupFavoritePlaceNotEmpty.visibility = View.GONE
                    tvFavoritePlacePlaceCount.visibility = View.GONE
                } else {
                    customErrorView.visibility = View.GONE
                    groupFavoritePlaceNotError.visibility = View.VISIBLE

                    if (!state.isLoading) {
                        handlePlaceState(state)
                    }
                }
            }
        }

        collectOnStarted(viewModel.commonUiEffect) { commonUiEffect: CommonUiEffect ->
            when (commonUiEffect) {
                CommonUiEffect.NavigateToLogin -> navigateToLoginScreen()
            }
        }

        collectOnStarted(viewModel.uiEffect) { uiEffect: FavoritePlaceUiEffect ->
            when (uiEffect) {
                FavoritePlaceUiEffect.ShowFolderShareNotAllowed -> {
                    showSuggestLoginMessage()
                }

                is FavoritePlaceUiEffect.ShareFolder -> {
                    shareFolder(uiEffect.favoriteFolderShareModel)
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
            LoginActivity.newIntent(requireActivity()).apply {
                flags =
                    Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
        startActivity(intent)
        requireActivity().finish()
    }

    private fun FragmentFavoritePlaceBinding.handlePlaceState(state: FavoritePlaceViewModel.FavoritePlaceUiState) {
        if (state.places.isEmpty()) {
            clFavoritePlaceEmpty.visibility = View.VISIBLE
            groupFavoritePlaceNotEmpty.visibility = View.GONE
            tvFavoritePlacePlaceCount.visibility = View.GONE
            ivFavoritePlaceShare.visibility = View.GONE
        } else {
            clFavoritePlaceEmpty.visibility = View.GONE
            groupFavoritePlaceNotEmpty.visibility = View.VISIBLE
            tvFavoritePlacePlaceCount.apply {
                visibility = View.VISIBLE
                text = getString(R.string.all_total_place_count, state.places.size)
            }
            ivFavoritePlaceShare.visibility = View.VISIBLE
        }
    }

    private fun shareFolder(folderShareModel: FavoriteFolderShareModel) {
        val sharedContents: String = folderShareModel.toShareFormat()

        val intent =
            Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_TEXT, sharedContents)
                putExtra(Intent.EXTRA_TITLE, folderShareModel.name)
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
            Intent.createChooser(intent, folderShareModel.name).apply {
                putExtra(Intent.EXTRA_INITIAL_INTENTS, initialIntents)
                putExtra(Intent.EXTRA_TITLE, folderShareModel.name)
            }

        startActivity(chooserIntent)
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
        viewModel.loadFoldersAndPlaces()
    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        if (!hidden) {
            viewModel.loadFoldersAndPlaces()
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

        viewModel.favoriteLatLng.observe(viewLifecycleOwner) { favoriteLatLngList ->
            when {
                favoriteLatLngList.isEmpty() -> handleEmptyFavorites()
                favoriteLatLngList.size == 1 -> handleSingleFavorite(favoriteLatLngList.first())
                else -> handleMultipleFavorites(favoriteLatLngList)
            }
        }
    }

    private fun handleEmptyFavorites() {
        binding.mvFavoritePlace.visibility = View.GONE
        binding.ivFavoritePlaceMapToggle.visibility = View.GONE
        markerMap.clear()
    }

    private fun handleSingleFavorite(favoriteLatLng: FavoritePlaceLatLngUiModel) {
        showMap()
        clearMapMarkers()

        addMarkerToMap(favoriteLatLng)
        map.setOnMapLoadedCallback {
            map.moveCamera(
                CameraUpdateFactory.newLatLngZoom(favoriteLatLng.favoriteLatLng, 15f),
            )
        }
    }

    private fun handleMultipleFavorites(favoriteLatLngList: List<FavoritePlaceLatLngUiModel>) {
        showMap()
        clearMapMarkers()

        val boundsBuilder = LatLngBounds.Builder()

        favoriteLatLngList.forEach { favoriteLatLng ->
            addMarkerToMap(favoriteLatLng)
            boundsBuilder.include(favoriteLatLng.favoriteLatLng)
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

    private fun addMarkerToMap(favoriteLatLng: FavoritePlaceLatLngUiModel) {
        val marker =
            map.addMarker(
                MarkerOptions()
                    .position(favoriteLatLng.favoriteLatLng)
                    .title(favoriteLatLng.name),
            )
        marker?.let {
            markerMap[favoriteLatLng.placeId] = it
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

        fun instance(): FavoritePlaceFragment = FavoritePlaceFragment()
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
