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
import com.on.turip.ui.folder.FolderActivity
import com.on.turip.ui.main.favorite.FavoritePlaceFolderViewHolder.FavoritePlaceFolderListener
import com.on.turip.ui.main.favorite.model.FavoritePlaceFolderModel
import com.on.turip.ui.trip.detail.TripDetailViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FavoritePlaceFolderFragment : BaseFragment<BottomSheetFragmentFavoritePlaceFolderBinding>() {
    private val viewModel: FavoritePlaceFolderViewModel by viewModels()
    private val sharedViewModel: TripDetailViewModel by activityViewModels()

    private val favoritePlaceFolderAdapter: FavoritePlaceFolderAdapter by lazy {
        FavoritePlaceFolderAdapter(
            object : FavoritePlaceFolderListener {
                override fun onFavoriteFolderFavoriteClick(favoritePlaceFolderModel: FavoritePlaceFolderModel) {
                    viewModel.updateFolder(
                        folderId = favoritePlaceFolderModel.id,
                        isFavorite = favoritePlaceFolderModel.isSelected,
                    )
                    showSnackbar(favoritePlaceFolderModel)
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

    private fun showSnackbar(favoritePlaceFolderModel: FavoritePlaceFolderModel) {
        val updatedFavorites = !favoritePlaceFolderModel.isSelected

        val messageResource =
            if (updatedFavorites) {
                R.string.bottom_sheet_favorite_place_folder_save_with_folder_name
            } else {
                R.string.bottom_sheet_favorite_place_folder_remove_with_folder_name
            }
        val message = getString(messageResource, favoritePlaceFolderModel.name)
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
            val intent: Intent = FolderActivity.newIntent(requireContext())
            startActivity(intent)
        }
    }

    private fun setupObservers() {
        viewModel.favoritePlaceFolders.observe(viewLifecycleOwner) { folders ->
            favoritePlaceFolderAdapter.submitList(folders)
        }
        viewModel.hasFavoriteFolderWithPlaceId.observe(viewLifecycleOwner) { hasFavoriteFolder ->
            sharedViewModel.updateHasFavoriteFolderInPlace(
                hasFavoriteFolder,
                arguments?.getLong(FAVORITE_PLACE_FOLDER_ARGUMENTS_PLACE_ID) ?: 0L,
            )
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.loadFavoriteFoldersByPlaceId()
    }

    companion object {
        const val FAVORITE_PLACE_FOLDER_ARGUMENTS_PLACE_ID =
            "com.on.turip.FAVORITE_PLACE_FOLDER_ARGUMENTS_PLACE_ID"

        fun newInstance(placeId: Long): FavoritePlaceFolderFragment =
            FavoritePlaceFolderFragment().apply {
                arguments =
                    Bundle().apply {
                        putLong(FAVORITE_PLACE_FOLDER_ARGUMENTS_PLACE_ID, placeId)
                    }
            }
    }
}
