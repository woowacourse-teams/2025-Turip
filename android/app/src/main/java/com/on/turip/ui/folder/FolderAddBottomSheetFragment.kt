package com.on.turip.ui.folder

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.InputFilter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.activityViewModels
import com.on.turip.databinding.BottomSheetFragmentFolderAddBinding
import com.on.turip.ui.common.base.BaseBottomSheetFragment
import com.on.turip.ui.folder.model.FolderNameStatusModel

class FolderAddBottomSheetFragment : BaseBottomSheetFragment<BottomSheetFragmentFolderAddBinding>() {
    private val sharedViewModel: FolderViewModel by activityViewModels()

    override fun inflateBinding(
        inflater: LayoutInflater,
        container: ViewGroup?,
    ): BottomSheetFragmentFolderAddBinding = BottomSheetFragmentFolderAddBinding.inflate(inflater, container, false)

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
        sharedViewModel.folderNameStatus.observe(this) { folderNameStatusModel: FolderNameStatusModel ->
            binding.tvBottomSheetFolderAddConfirm.isEnabled =
                (folderNameStatusModel == FolderNameStatusModel.OK || folderNameStatusModel == FolderNameStatusModel.MAX_LENGTH_FOLDER_NAME)

            folderNameStatusModel.errorMessage?.let {
                binding.tvBottomSheetFolderAddError.apply {
                    visibility = View.VISIBLE
                    setText(it)
                }
            } ?: run {
                binding.tvBottomSheetFolderAddError.visibility = View.GONE
            }
        }
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
