package com.on.turip.ui.folder

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.google.android.material.snackbar.Snackbar
import com.on.turip.R
import com.on.turip.databinding.BottomSheetFragmentFolderRemoveBinding
import com.on.turip.ui.common.base.BaseBottomSheetFragment
import com.on.turip.ui.common.collectOnStarted
import com.on.turip.ui.common.error.ErrorUiModel
import com.on.turip.ui.common.error.toUiModel
import com.on.turip.ui.folder.model.FolderUiEffect
import com.on.turip.ui.login.LoginActivity

class FolderRemoveBottomSheetFragment :
    BaseBottomSheetFragment<BottomSheetFragmentFolderRemoveBinding>() {
    private val sharedViewModel: FolderViewModel by activityViewModels()

    override fun inflateBinding(
        inflater: LayoutInflater,
        container: ViewGroup?,
    ): BottomSheetFragmentFolderRemoveBinding =
        BottomSheetFragmentFolderRemoveBinding.inflate(inflater, container, false)

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)

        setupListeners()
        setupObservers()
    }

    private fun setupListeners() {
        binding.tvBottomSheetFolderRemoveRemove.setOnClickListener {
            sharedViewModel.deleteFolder()
        }
        binding.tvBottomSheetFolderRemoveCancel.setOnClickListener {
            dismiss()
        }
    }

    private fun setupObservers() {
        sharedViewModel.getSelectedFolderOrNull()?.let { selectFolder ->
            binding.tvBottomSheetFolderRemoveTitle.text =
                getString(R.string.bottom_sheet_folder_remove_title, selectFolder.name)
            binding.tvBottomSheetFolderRemovePlaceCount.text =
                getString(R.string.all_total_place_count, selectFolder.count)
        }

        collectOnStarted(sharedViewModel.uiEffect) { uiEffect: FolderUiEffect ->
            when (uiEffect) {
                FolderUiEffect.NavigateToLogin -> {
                    navigateToLoginScreen()
                }

                FolderUiEffect.FolderDeleted -> dismiss()

                is FolderUiEffect.ShowError -> {
                    val uiModel: ErrorUiModel =
                        uiEffect.errorUiState.toUiModel() ?: return@collectOnStarted
                    view?.let { view: View ->
                        Snackbar
                            .make(view, uiModel.titleRes, Snackbar.LENGTH_INDEFINITE)
                            .apply {
                                setAction(uiModel.retryTextRes) {
                                    sharedViewModel.onErrorRetryRequested(uiEffect.action)
                                }
                            }.show()
                    }
                }

                else -> Unit
            }
        }
    }

    private fun navigateToLoginScreen() {
        val intent: Intent =
            LoginActivity
                .newIntent(requireActivity())
                .apply { flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK }
        startActivity(intent)
        requireActivity().finish()
    }

    companion object {
        fun instance(): FolderRemoveBottomSheetFragment = FolderRemoveBottomSheetFragment()
    }
}
