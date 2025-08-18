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
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.on.turip.R
import com.on.turip.databinding.BottomSheetFragmentFavoritePlaceFolderBinding
import com.on.turip.ui.common.base.BaseBottomSheetFragment
import com.on.turip.ui.main.favorite.model.FavoritePlaceFolderModel

class FavoritePlaceFolderBottomSheetFragment : BaseBottomSheetFragment<BottomSheetFragmentFavoritePlaceFolderBinding>() {
    private val favoritePlaceFolderAdapter: FavoritePlaceFolderAdapter by lazy {
        FavoritePlaceFolderAdapter { favoritePlaceFolderModel: FavoritePlaceFolderModel ->
            // TODO : 폴더명과 함께 스낵바 띄우기
        }
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
    }

    private fun setupAdapters() {
        binding.rvBottomSheetFavoritePlaceFolderFolder.adapter = favoritePlaceFolderAdapter
    }

    companion object {
        private const val BASIC_VIEW_PERCENT: Float = 0.5f

        fun instance(): FavoritePlaceFolderBottomSheetFragment = FavoritePlaceFolderBottomSheetFragment()
    }
}
