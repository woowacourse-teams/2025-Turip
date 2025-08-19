package com.on.turip.ui.folder

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.on.turip.R
import com.on.turip.databinding.BottomSheetFragmentFolderRemoveBinding
import com.on.turip.ui.common.base.BaseBottomSheetFragment
import com.on.turip.ui.folder.model.FolderEditModel

class FolderRemoveBottomSheetFragment : BaseBottomSheetFragment<BottomSheetFragmentFolderRemoveBinding>() {
    private val sharedViewModel: FolderViewModel by activityViewModels()

    override fun inflateBinding(
        inflater: LayoutInflater,
        container: ViewGroup?,
    ): BottomSheetFragmentFolderRemoveBinding = BottomSheetFragmentFolderRemoveBinding.inflate(inflater, container, false)

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
            dismiss()
        }
        binding.tvBottomSheetFolderRemoveCancel.setOnClickListener {
            dismiss()
        }
    }

    private fun setupObservers() {
        sharedViewModel.selectFolder.observe(viewLifecycleOwner) { selectedFolder: FolderEditModel ->
            binding.tvBottomSheetFolderRemoveTitle.text =
                getString(R.string.bottom_sheet_folder_remove_title, selectedFolder.name)
            binding.tvBottomSheetFolderRemovePlaceCount.text =
                getString(R.string.all_total_place_count, selectedFolder.count)
        }
    }

    companion object {
        fun instance(): FolderRemoveBottomSheetFragment = FolderRemoveBottomSheetFragment()
    }
}
