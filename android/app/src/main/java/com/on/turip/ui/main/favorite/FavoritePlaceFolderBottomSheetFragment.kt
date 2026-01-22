package com.on.turip.ui.main.favorite

import android.app.Dialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.on.turip.R
import com.on.turip.ui.compose.designsystem.theme.TuripTheme
import com.on.turip.ui.compose.favorite.FavoritePlaceFolderBottomSheet
import com.on.turip.ui.folder.FolderActivity
import com.on.turip.ui.login.LoginActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FavoritePlaceFolderBottomSheetFragment : BottomSheetDialogFragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View =
        ComposeView(requireContext()).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                TuripTheme {
                    FavoritePlaceFolderBottomSheet(
                        onNavigateToLogin = {
                            val intent: Intent =
                                LoginActivity.newIntent(requireActivity()).apply {
                                    flags =
                                        Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                }
                            startActivity(intent)
                            requireActivity().finish()
                        },
                        onNavigateToAddFolder = {
                            val intent: Intent = FolderActivity.newIntent(requireContext())
                            startActivity(intent)
                        },
                        onDismiss = { dismiss() },
                    )
                }
            }
        }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog: Dialog = super.onCreateDialog(savedInstanceState)
        dialog.setOnShowListener { dialogInterface: DialogInterface ->
            val bottomSheetDialog = dialogInterface as BottomSheetDialog
            val bottomSheet =
                bottomSheetDialog.findViewById<FrameLayout>(com.google.android.material.R.id.design_bottom_sheet)

            bottomSheet?.let {
                it.setBackgroundColor(Color.White.toArgb())
                it.setBackgroundResource(R.drawable.bg_pure_white_top_radius_20dp)

                val behavior = BottomSheetBehavior.from(it)
                behavior.isFitToContents = true
                behavior.state = BottomSheetBehavior.STATE_EXPANDED
            }
        }
        return dialog
    }

    companion object {
        const val FAVORITE_PLACE_FOLDER_ARGUMENTS_PLACE_ID =
            "com.on.turip.FAVORITE_PLACE_FOLDER_ARGUMENTS_PLACE_ID"
        const val FAVORITE_PLACE_FOLDER_ARGUMENTS_PLACE_NAME =
            "com.on.turip.FAVORITE_PLACE_FOLDER_ARGUMENTS_PLACE_NAME"

        fun newInstance(
            placeId: Long,
            placeName: String,
        ): FavoritePlaceFolderBottomSheetFragment =
            FavoritePlaceFolderBottomSheetFragment().apply {
                arguments =
                    Bundle().apply {
                        putLong(FAVORITE_PLACE_FOLDER_ARGUMENTS_PLACE_ID, placeId)
                        putString(FAVORITE_PLACE_FOLDER_ARGUMENTS_PLACE_NAME, placeName)
                    }
            }
    }
}
