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
import com.on.turip.data.common.ErrorUiModel
import com.on.turip.data.common.toUiModel
import com.on.turip.databinding.BottomSheetFragmentFolderAddBinding
import com.on.turip.ui.common.base.BaseBottomSheetFragment
import com.on.turip.ui.common.collectOnStarted
import com.on.turip.ui.folder.model.FolderNameStatusModel
import com.on.turip.ui.folder.model.FolderUiEffect
import com.on.turip.ui.login.LoginActivity

class FolderAddBottomSheetFragment :
    BaseBottomSheetFragment<BottomSheetFragmentFolderAddBinding>() {
    private val sharedViewModel: FolderViewModel by activityViewModels()

    override fun inflateBinding(
        inflater: LayoutInflater,
        container: ViewGroup?,
    ): BottomSheetFragmentFolderAddBinding =
        BottomSheetFragmentFolderAddBinding.inflate(inflater, container, false)

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
        binding.etBottomSheetFolderAddFolderName.apply {
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
            binding.tvBottomSheetFolderAddConfirm.isEnabled = folderNameStatus.isConfirmEnabled

            if (folderNameStatus.errorMessage != null) {
                binding.tvBottomSheetFolderAddError.apply {
                    visibility = View.VISIBLE
                    setText(folderNameStatus.errorMessage)
                }
            } else {
                binding.tvBottomSheetFolderAddError.visibility = View.GONE
            }
        }

        collectOnStarted(sharedViewModel.uiEffect) { uiEffect: FolderUiEffect ->
            when (uiEffect) {
                FolderUiEffect.NavigateToLogin -> {
                    navigateToLoginScreen()
                }

                is FolderUiEffect.ShowErrorSnackbar -> {
                    val uiModel: ErrorUiModel =
                        uiEffect.errorUiState.toUiModel() ?: return@collectOnStarted
                    view?.let { view: View ->
                        Snackbar
                            .make(view, uiModel.titleRes, Snackbar.LENGTH_INDEFINITE)
                            .apply { uiEffect.onRetryClick?.let { action -> setAction(uiModel.retryTextRes) { action() } } }
                            .show()
                    }
                }
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

    private fun setupListeners() {
        binding.tvBottomSheetFolderAddConfirm.setOnClickListener {
            sharedViewModel.addFolder()
            dismiss()
        }
        binding.etBottomSheetFolderAddFolderName.addTextChangedListener { text: Editable? ->
            sharedViewModel.updateInputFolderName(text.toString())
        }
    }

    companion object {
        fun instance(): FolderAddBottomSheetFragment = FolderAddBottomSheetFragment()
    }
}
