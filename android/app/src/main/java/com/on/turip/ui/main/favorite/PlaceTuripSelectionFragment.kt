package com.on.turip.ui.main.favorite

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import com.google.android.material.snackbar.Snackbar
import com.on.turip.R
import com.on.turip.databinding.BottomSheetFragmentFavoritePlaceFolderBinding
import com.on.turip.ui.common.TuripSnackbar
import com.on.turip.ui.common.base.BaseFragment
import com.on.turip.ui.common.collectOnStarted
import com.on.turip.ui.common.error.ErrorUiModel
import com.on.turip.ui.common.error.toUiModel
import com.on.turip.ui.compose.trip.TripDetailViewModel
import com.on.turip.ui.folder.TuripActivity
import com.on.turip.ui.login.LoginActivity
import com.on.turip.ui.main.favorite.FavoritePlaceFolderViewHolder.FavoritePlaceFolderListener
import com.on.turip.ui.main.favorite.model.PlaceTuripSelectionUiEffect
import com.on.turip.ui.main.favorite.model.PlaceTuripSelectionUiState
import com.on.turip.ui.main.favorite.model.TuripModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PlaceTuripSelectionFragment : BaseFragment<BottomSheetFragmentFavoritePlaceFolderBinding>() {
    private val viewModel: PlaceTuripSelectionViewModel by viewModels()
    private val sharedViewModel: TripDetailViewModel by activityViewModels()

    private val favoritePlaceFolderAdapter: FavoritePlaceFolderAdapter by lazy {
        FavoritePlaceFolderAdapter(
            object : FavoritePlaceFolderListener {
                override fun onFavoriteFolderFavoriteClick(turipModel: TuripModel) {
                    viewModel.updateTurip(turipModel)
                }

                override fun onFavoriteFolderClick(
                    favoriteFolderId: Long,
                    favoriteFolderName: String,
                ) {
                    parentFragmentManager
                        .beginTransaction()
                        .replace(
                            R.id.fcv_bottom_sheet_folder_favorite_place_folder_catalog,
                            FavoritePlaceFolderCatalogFragment.newInstance(
                                favoriteFolderId,
                                favoriteFolderName,
                            ),
                        ).addToBackStack(null)
                        .commit()
                }
            },
        )
    }

    override fun inflateBinding(
        inflater: LayoutInflater,
        container: ViewGroup?,
    ): BottomSheetFragmentFavoritePlaceFolderBinding = BottomSheetFragmentFavoritePlaceFolderBinding.inflate(inflater, container, false)

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)
        setupAdapters()
        setupListeners()
        setupObservers()
    }

    private fun setupAdapters() {
        binding.rvBottomSheetFavoritePlaceFolderFolder.adapter = favoritePlaceFolderAdapter
    }

    private fun setupListeners() {
        binding.ivBottomSheetFolderFavoritePlaceAddFolder.setOnClickListener {
            val intent: Intent = TuripActivity.newIntent(requireContext())
            startActivity(intent)
        }
    }

    private fun setupObservers() {
        collectOnStarted(viewModel.uiState) { uiState: PlaceTuripSelectionUiState ->
            favoritePlaceFolderAdapter.submitList(uiState.turips)
            sharedViewModel.updateHasFavoriteFolderInPlace(
                hasFavoriteFolder = uiState.isIncludedInAnyTurip,
                placeId = uiState.placeId,
            )
        }

        collectOnStarted(viewModel.uiEffect) { uiEffect: PlaceTuripSelectionUiEffect ->
            when (uiEffect) {
                is PlaceTuripSelectionUiEffect.ShowUpdateTurip -> {
                    showFavoriteStatus(uiEffect.turip)
                }

                PlaceTuripSelectionUiEffect.NavigateToLogin -> {
                    navigateToLoginScreen()
                }

                is PlaceTuripSelectionUiEffect.ShowError -> {
                    val uiModel: ErrorUiModel =
                        uiEffect.errorUiState.toUiModel() ?: return@collectOnStarted
                    view?.let { view: View ->
                        Snackbar
                            .make(view, uiModel.titleRes, Snackbar.LENGTH_INDEFINITE)
                            .apply {
                                setAction(uiModel.retryTextRes) {
                                    viewModel.handleErrorRetryRequest(
                                        uiEffect.retryAction,
                                    )
                                }
                            }.show()
                    }
                }
            }
        }
    }

    private fun showFavoriteStatus(turipModel: TuripModel) {
        val updatedFavorites = !turipModel.isSelected

        val messageResource =
            if (updatedFavorites) {
                R.string.bottom_sheet_turip_place_save_in_turip
            } else {
                R.string.bottom_sheet_turip_place_remove_in_turip
            }
        val message = getString(messageResource, turipModel.name)
        val iconResource =
            if (updatedFavorites) R.drawable.ic_heart_pressed else R.drawable.ic_heart_empty

        TuripSnackbar
            .make(
                rootView = binding.root,
                message = message,
                duration = Snackbar.LENGTH_LONG,
                layoutInflater = layoutInflater,
            ).icon(iconResource)
            .action(R.string.all_snackbar_close)
            .show()
    }

    private fun navigateToLoginScreen() {
        val intent: Intent =
            LoginActivity
                .newIntent(requireActivity())
                .apply { flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK }
        startActivity(intent)
        requireActivity().finish()
    }

    override fun onResume() {
        super.onResume()
        viewModel.loadTuripsByPlace()
    }

    companion object {
        const val FAVORITE_PLACE_FOLDER_ARGUMENTS_PLACE_ID =
            "com.on.turip.FAVORITE_PLACE_FOLDER_ARGUMENTS_PLACE_ID"

        fun newInstance(placeId: Long): PlaceTuripSelectionFragment =
            PlaceTuripSelectionFragment().apply {
                arguments =
                    Bundle().apply {
                        putLong(FAVORITE_PLACE_FOLDER_ARGUMENTS_PLACE_ID, placeId)
                    }
            }
    }
}
