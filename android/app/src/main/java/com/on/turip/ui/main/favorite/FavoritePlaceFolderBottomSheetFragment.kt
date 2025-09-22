package com.on.turip.ui.main.favorite

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.on.turip.R
import com.on.turip.databinding.BottomSheetFragmentFavoritePlaceFolderContainerBinding
import com.on.turip.ui.common.base.BaseBottomSheetFragment

class FavoriteBottomSheetContainerFragment : BaseBottomSheetFragment<BottomSheetFragmentFavoritePlaceFolderContainerBinding>() {
    override fun inflateBinding(
        inflater: LayoutInflater,
        container: ViewGroup?,
    ): BottomSheetFragmentFavoritePlaceFolderContainerBinding =
        BottomSheetFragmentFavoritePlaceFolderContainerBinding.inflate(inflater, container, false)

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog: Dialog = super.onCreateDialog(savedInstanceState)
        dialog.setOnShowListener {
            val bottomSheetView: View =
                dialog.findViewById(com.google.android.material.R.id.design_bottom_sheet)
            val behavior = BottomSheetBehavior.from(bottomSheetView)

            bottomSheetView.setBackgroundResource(R.drawable.bg_pure_white_top_radius_20dp)

            val screenHeight = resources.displayMetrics.heightPixels
            val halfHeight = (screenHeight * 0.5f).toInt()

            bottomSheetView.layoutParams.height = halfHeight

            behavior.apply {
                isFitToContents = false
                peekHeight = halfHeight
                isDraggable = false
                state = BottomSheetBehavior.STATE_COLLAPSED
            }
        }
        return dialog
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)

        if (savedInstanceState == null) {
            val placeId = arguments?.getLong(ARGUMENTS_PLACE_ID) ?: 0L
            childFragmentManager
                .beginTransaction()
                .replace(
                    R.id.fcv_bottom_sheet_folder_favorite_place_folder_catalog,
                    FavoritePlaceFolderFragment.newInstance(placeId),
                ).commit()
        }
    }

    companion object {
        private const val ARGUMENTS_PLACE_ID = "PLACE_ID"

        fun instance(placeId: Long): FavoriteBottomSheetContainerFragment =
            FavoriteBottomSheetContainerFragment().apply {
                arguments =
                    Bundle().apply {
                        putLong(ARGUMENTS_PLACE_ID, placeId)
                    }
            }
    }
}
