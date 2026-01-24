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
import androidx.fragment.app.viewModels
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.on.turip.R
import com.on.turip.ui.common.safeStartActivityWithToast
import com.on.turip.ui.compose.designsystem.theme.TuripTheme
import com.on.turip.ui.compose.favorite.FavoritePlaceFolderBottomSheet
import com.on.turip.ui.folder.FolderActivity
import com.on.turip.ui.login.LoginActivity
import com.on.turip.ui.main.favorite.model.FavoriteFolderShareModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FavoritePlaceFolderBottomSheetFragment : BottomSheetDialogFragment() {
    private val viewModel: FavoritePlaceFolderViewModel by viewModels()

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
                        onNavigateToMap = { map ->
                            val intent = Intent(Intent.ACTION_VIEW, map.uri)
                            requireContext().safeStartActivityWithToast(
                                intent = intent,
                                errorToastMessage = getString(R.string.all_snackbar_not_found_map_url),
                            )
                        },
                        onShareFolder = { model: FavoriteFolderShareModel ->
                            navigateToShareFolder(model)
                        },
                        onDismiss = { dismiss() },
                        viewModel = viewModel,
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

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        viewModel.commitFavoritePlaceDelete()
    }

    private fun navigateToShareFolder(folderShareModel: FavoriteFolderShareModel) {
        val sharedContents: String = folderShareModel.toShareFormat()

        val intent =
            createShareIntent(text = sharedContents).apply {
                putExtra(Intent.EXTRA_TITLE, folderShareModel.name)
            }

        val initialIntents =
            arrayOf(
                createShareIntent(text = sharedContents, packageName = KAKAO_PACKAGE),
                createShareIntent(text = sharedContents, packageName = INSTAGRAM_PACKAGE),
            )

        val chooserIntent =
            Intent
                .createChooser(intent, folderShareModel.name)
                .apply { putExtra(Intent.EXTRA_INITIAL_INTENTS, initialIntents) }

        startActivity(chooserIntent)
    }

    private fun createShareIntent(
        text: String,
        packageName: String? = null,
    ): Intent =
        Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, text)

            packageName?.let {
                `package` = it
            }
        }

    companion object {
        const val FAVORITE_PLACE_FOLDER_ARGUMENTS_PLACE_ID =
            "com.on.turip.FAVORITE_PLACE_FOLDER_ARGUMENTS_PLACE_ID"
        const val FAVORITE_PLACE_FOLDER_ARGUMENTS_PLACE_NAME =
            "com.on.turip.FAVORITE_PLACE_FOLDER_ARGUMENTS_PLACE_NAME"

        private const val KAKAO_PACKAGE = "com.kakao.talk"
        private const val INSTAGRAM_PACKAGE = "com.instagram.android"

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
