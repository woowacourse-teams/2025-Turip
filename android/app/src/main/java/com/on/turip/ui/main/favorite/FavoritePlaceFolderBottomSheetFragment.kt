package com.on.turip.ui.main.favorite

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.core.graphics.Insets
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.viewModels
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.snackbar.Snackbar
import com.on.turip.R
import com.on.turip.databinding.BottomSheetFragmentFavoritePlaceFolderBinding
import com.on.turip.ui.common.TuripSnackbar
import com.on.turip.ui.common.base.BaseBottomSheetFragment
import com.on.turip.ui.main.favorite.model.FavoritePlaceFolderModel

class FavoritePlaceFolderBottomSheetFragment : BaseBottomSheetFragment<BottomSheetFragmentFavoritePlaceFolderBinding>() {
    private val viewModel: FavoritePlaceFolderViewModel by viewModels {
        FavoritePlaceFolderViewModel.provideFactory(
            placeId = arguments?.getLong(ARGUMENTS_PLACE_ID) ?: 0L,
        )
    }
    private val favoritePlaceFolderAdapter: FavoritePlaceFolderAdapter by lazy {
        FavoritePlaceFolderAdapter { favoritePlaceFolderModel: FavoritePlaceFolderModel ->
            viewModel.updateFolder(folderId = favoritePlaceFolderModel.id)
            showSnackbar(favoritePlaceFolderModel)
        }
    }

    private fun showSnackbar(favoritePlaceFolderModel: FavoritePlaceFolderModel) {
        val updatedFavorites: Boolean = !favoritePlaceFolderModel.isSelected

        val messageResource: Int =
            if (updatedFavorites) R.string.bottom_sheet_favorite_place_folder_save_with_folder_name else R.string.bottom_sheet_favorite_place_folder_remove_with_folder_name
        val message: String = getString(messageResource, favoritePlaceFolderModel.name)
        val iconResource: Int =
            if (updatedFavorites) R.drawable.ic_heart_normal else R.drawable.ic_heart_empty

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

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog: Dialog = super.onCreateDialog(savedInstanceState)
        dialog.setOnShowListener {
            val bottomSheetView: View =
                dialog.findViewById<FrameLayout>(com.google.android.material.R.id.design_bottom_sheet)
            val behavior: BottomSheetBehavior<View> = BottomSheetBehavior.from(bottomSheetView)

            bottomSheetView.setBackgroundResource(R.drawable.bg_pure_white_top_radius_20dp)

            var isInitialized: Boolean = false
            ViewCompat.setOnApplyWindowInsetsListener(bottomSheetView) { view: View, insets: WindowInsetsCompat ->
                val systemBars: Insets = insets.getInsets(WindowInsetsCompat.Type.systemBars())
                val availableHeight: Int = view.rootView.height - systemBars.top - systemBars.bottom

                bottomSheetView.layoutParams.height = availableHeight
                behavior.expandedOffset = systemBars.top

                if (!isInitialized) {
                    behavior.apply {
                        isFitToContents = false
                        peekHeight = (availableHeight * BASIC_VIEW_PERCENT).toInt()
                        isDraggable = true
                        state = BottomSheetBehavior.STATE_COLLAPSED
                    }
                    isInitialized = true
                }
                insets
            }
        }
        return dialog
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)
        setupAdapters()
        setupObservers()
    }

    private fun setupAdapters() {
        binding.rvBottomSheetFavoritePlaceFolderFolder.adapter = favoritePlaceFolderAdapter
    }

    private fun setupObservers() {
        viewModel.favoritePlaceFolders.observe(viewLifecycleOwner) { folders: List<FavoritePlaceFolderModel> ->
            favoritePlaceFolderAdapter.submitList(folders)
        }
    }

    companion object {
        private const val BASIC_VIEW_PERCENT: Float = 0.5f
        private const val ARGUMENTS_PLACE_ID = "PLACE_ID"

        fun instance(placeId: Long): FavoritePlaceFolderBottomSheetFragment =
            FavoritePlaceFolderBottomSheetFragment().apply {
                arguments =
                    Bundle().apply {
                        putLong(ARGUMENTS_PLACE_ID, placeId)
                    }
            }
    }
}
