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
import androidx.core.os.bundleOf
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.viewModels
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.on.turip.R
import com.on.turip.ui.common.safeStartActivityWithToast
import com.on.turip.ui.compose.designsystem.theme.TuripTheme
import com.on.turip.ui.compose.turip.selection.PlaceTuripSelectionBottomSheet
import com.on.turip.ui.compose.turip.selection.PlaceTuripSelectionViewModel
import com.on.turip.ui.folder.TuripActivity
import com.on.turip.ui.login.LoginActivity
import com.on.turip.ui.main.favorite.model.TuripShareModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PlaceTuripSelectionFragment : BottomSheetDialogFragment() {
    private val viewModel: PlaceTuripSelectionViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View =
        ComposeView(requireContext()).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                TuripTheme {
                    PlaceTuripSelectionBottomSheet(
                        onNavigateToLogin = {
                            val intent: Intent =
                                LoginActivity.newIntent(requireActivity()).apply {
                                    flags =
                                        Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                }
                            startActivity(intent)
                            requireActivity().finish()
                        },
                        onNavigateToAddTurip = {
                            val intent: Intent = TuripActivity.newIntent(requireContext())
                            startActivity(intent)
                        },
                        onNavigateToMap = { map ->
                            val intent = Intent(Intent.ACTION_VIEW, map.uri)
                            requireContext().safeStartActivityWithToast(
                                intent = intent,
                                errorToastMessage = getString(R.string.all_snackbar_not_found_map_url),
                            )
                        },
                        onShareTurip = { model: TuripShareModel ->
                            navigateToShareTurip(model)
                        },
                        onTuripSelectionConfirm = { placeId, hasTurip ->
                            setFragmentResult(
                                "TURIP_SELECTION_RESULT",
                                bundleOf(
                                    "TURIP_SELECTION_PLACE_ID" to placeId,
                                    "TURIP_SELECTION_HAS_TURIP" to hasTurip,
                                ),
                            )

                            dismiss()
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
                behavior.isDraggable = false
                behavior.state = BottomSheetBehavior.STATE_EXPANDED
            }
        }
        return dialog
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        viewModel.commitTuripPlaceDelete()
    }

    private fun navigateToShareTurip(folderShareModel: TuripShareModel) {
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

    override fun onResume() {
        super.onResume()
        viewModel.loadTuripsByPlace()
    }

    companion object {
        const val PLACE_TURIP_SELECTION_ARGUMENTS_PLACE_ID =
            "com.on.turip.PLACE_TURIP_SELECTION_ARGUMENTS_PLACE_ID"
        const val PLACE_TURIP_SELECTION_ARGUMENTS_PLACE_NAME =
            "com.on.turip.PLACE_TURIP_SELECTION_ARGUMENTS_PLACE_NAME"

        private const val KAKAO_PACKAGE = "com.kakao.talk"
        private const val INSTAGRAM_PACKAGE = "com.instagram.android"

        fun newInstance(
            placeId: Long,
            placeName: String,
        ): PlaceTuripSelectionFragment =
            PlaceTuripSelectionFragment().apply {
                arguments =
                    Bundle().apply {
                        putLong(PLACE_TURIP_SELECTION_ARGUMENTS_PLACE_ID, placeId)
                        putString(PLACE_TURIP_SELECTION_ARGUMENTS_PLACE_NAME, placeName)
                    }
            }
    }
}
