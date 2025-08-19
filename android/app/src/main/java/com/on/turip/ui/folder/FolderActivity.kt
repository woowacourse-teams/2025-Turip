package com.on.turip.ui.folder

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import com.on.turip.databinding.ActivityFolderBinding
import com.on.turip.ui.common.base.BaseActivity

class FolderActivity : BaseActivity<ActivityFolderBinding>() {
    private val viewModel: FolderViewModel by viewModels { FolderViewModel.provideFactory() }

    override val binding: ActivityFolderBinding by lazy {
        ActivityFolderBinding.inflate(layoutInflater)
    }

    private val folderEditAdapter: FolderEditAdapter by lazy {
        FolderEditAdapter(
            object : FolderEditViewHolder.FolderEditListener {
                override fun onRemoveClick(folderId: Long) {
                    viewModel.selectFolder(folderId)
                    val bottomSheet: FolderRemoveBottomSheetFragment =
                        FolderRemoveBottomSheetFragment.instance()
                    bottomSheet.show(supportFragmentManager, "folder_remove")
                }

                override fun onItemClick(folderId: Long) {
                    viewModel.selectFolder(folderId)
                    val bottomSheet: FolderModifyBottomSheetFragment =
                        FolderModifyBottomSheetFragment.instance()
                    bottomSheet.show(supportFragmentManager, "folder_modify")
                }
            },
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setupAdapters()
        setupListeners()
        setupObservers()
    }

    private fun setupAdapters() {
        binding.rvFolder.adapter = folderEditAdapter
    }

    private fun setupListeners() {
        binding.ivFolderFolderPlus.setOnClickListener {
            val bottomSheet: FolderAddBottomSheetFragment = FolderAddBottomSheetFragment.instance()
            bottomSheet.show(supportFragmentManager, "folder_add")
        }
        binding.ivFolderBack.setOnClickListener {
            finish()
        }
    }

    private fun setupObservers() {
        viewModel.folders.observe(this) {
            folderEditAdapter.submitList(it)
        }
    }

    companion object {
        fun newIntent(context: Context): Intent = Intent(context, FolderActivity::class.java)
    }
}
