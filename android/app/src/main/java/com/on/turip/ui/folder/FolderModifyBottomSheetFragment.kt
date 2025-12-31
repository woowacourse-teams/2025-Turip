package com.on.turip.ui.folder

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.InputFilter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.activityViewModels
import com.google.android.material.snackbar.Snackbar
import com.on.turip.databinding.BottomSheetFragmentFolderModifyBinding
import com.on.turip.ui.common.base.BaseBottomSheetFragment
import com.on.turip.ui.common.collectOnStarted
import com.on.turip.ui.common.error.ErrorUiModel
import com.on.turip.ui.common.error.toUiModel
import com.on.turip.ui.folder.model.FolderNameStatusModel
import com.on.turip.ui.folder.model.FolderUiEffect
import com.on.turip.ui.login.LoginActivity

class FolderModifyBottomSheetFragment :
    BaseBottomSheetFragment<BottomSheetFragmentFolderModifyBinding>() {
    private val sharedViewModel: FolderViewModel by activityViewModels()

    override fun inflateBinding(
        inflater: LayoutInflater,
        container: ViewGroup?,
    ): BottomSheetFragmentFolderModifyBinding = BottomSheetFragmentFolderModifyBinding.inflate(inflater, container, false)

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)

        setupEditText()
        setupObservers()
        setupListeners()
    }

    private fun setupEditText() {
        binding.etBottomSheetFolderModifyFolderName.apply {
            requestFocus()
            post {
                val inputMethodManager: InputMethodManager =
                    context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                inputMethodManager.showSoftInput(this, InputMethodManager.SHOW_IMPLICIT)
            }
            filters = arrayOf(InputFilter.LengthFilter(20))
        }
    }

    private fun setupObservers() {
        collectOnStarted(sharedViewModel.folderNameStatus) { folderNameStatus: FolderNameStatusModel ->
            binding.tvBottomSheetFolderModifyConfirm.isEnabled = folderNameStatus.isConfirmEnabled

            if (folderNameStatus.errorMessage != null) {
                binding.tvBottomSheetFolderModifyError.apply {
                    visibility = View.VISIBLE
                    setText(folderNameStatus.errorMessage)
                }
            } else {
                binding.tvBottomSheetFolderModifyError.visibility = View.GONE
            }
        }

        collectOnStarted(sharedViewModel.uiEffect) { uiEffect: FolderUiEffect ->
            when (uiEffect) {
                FolderUiEffect.NavigateToLogin -> {
                    navigateToLoginScreen()
                }

                FolderUiEffect.FolderUpdated -> dismiss()

                is FolderUiEffect.ShowError -> {
                    val uiModel: ErrorUiModel =
                        uiEffect.errorUiState.toUiModel() ?: return@collectOnStarted
                    view?.let { view: View ->
                        Snackbar
                            .make(view, uiModel.titleRes, Snackbar.LENGTH_INDEFINITE)
                            .apply { uiEffect.onRetryClick?.let { action -> setAction(uiModel.retryTextRes) { action() } } }
                            .show()
                    }
                }

                else -> Unit
            }
        }
    }

    private fun setupListeners() {
        binding.tvBottomSheetFolderModifyConfirm.setOnClickListener {
            sharedViewModel.updateFolderName()
        }
        binding.etBottomSheetFolderModifyFolderName.addTextChangedListener { text: Editable? ->
            sharedViewModel.updateInputFolderName(text.toString())
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
        fun instance(): FolderModifyBottomSheetFragment = FolderModifyBottomSheetFragment()
    }
}
