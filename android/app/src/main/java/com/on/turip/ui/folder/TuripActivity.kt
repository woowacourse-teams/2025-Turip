package com.on.turip.ui.folder

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import com.on.turip.databinding.ActivityFolderBinding
import com.on.turip.ui.common.base.BaseActivity
import com.on.turip.ui.common.collectOnStarted
import com.on.turip.ui.common.error.ErrorUiState
import com.on.turip.ui.folder.model.TuripUiState
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TuripActivity : BaseActivity<ActivityFolderBinding>() {
    private val viewModel: TuripViewModel by viewModels()

    override val binding: ActivityFolderBinding by lazy {
        ActivityFolderBinding.inflate(layoutInflater)
    }

    private val folderEditAdapter: FolderEditAdapter by lazy {
        FolderEditAdapter(
            object : FolderEditViewHolder.FolderEditListener {
                override fun onRemoveClick(folderId: Long) {
                    viewModel.selectTurip(folderId)
                    val bottomSheet: FolderRemoveBottomSheetFragment =
                        FolderRemoveBottomSheetFragment.instance()
                    bottomSheet.show(supportFragmentManager, "folder_remove")
                }

                override fun onItemClick(folderId: Long) {
                    viewModel.selectTurip(folderId)
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
        collectOnStarted(viewModel.uiState) { uiState: TuripUiState ->
            if (uiState.isLoading) showLoading()
            when {
                uiState.errorUiState != ErrorUiState.None -> showErrorView(uiState.errorUiState)
                uiState.isEmpty -> showEmptyView()
                else -> showContents(uiState)
            }
        }
    }

    private fun showLoading() {
        binding.pbFolderLoading.visibility = View.VISIBLE
        binding.ivFolderFolderPlus.visibility = View.VISIBLE
        binding.rvFolder.visibility = View.GONE
        binding.customErrorView.visibility = View.GONE
        binding.clFolderEmpty.visibility = View.GONE
    }

    private fun showErrorView(errorUiState: ErrorUiState) {
        binding.pbFolderLoading.visibility = View.GONE
        binding.ivFolderFolderPlus.visibility = View.GONE
        binding.rvFolder.visibility = View.GONE
        binding.clFolderEmpty.visibility = View.GONE

        binding.customErrorView.apply {
            visibility = View.VISIBLE
            showErrorView(errorUiState)
            setOnRetryClickListener { viewModel.loadTurips() }
        }
    }

    private fun showEmptyView() {
        binding.pbFolderLoading.visibility = View.GONE
        binding.ivFolderFolderPlus.visibility = View.VISIBLE
        binding.customErrorView.visibility = View.GONE

        binding.rvFolder.visibility = View.GONE
        binding.clFolderEmpty.visibility = View.VISIBLE
    }

    private fun showContents(uiState: TuripUiState) {
        binding.pbFolderLoading.visibility = View.GONE
        binding.ivFolderFolderPlus.visibility = View.VISIBLE
        binding.customErrorView.visibility = View.GONE

        folderEditAdapter.submitList(uiState.turips)

        binding.rvFolder.visibility = View.VISIBLE
        binding.clFolderEmpty.visibility = View.GONE
    }

    companion object {
        fun newIntent(context: Context): Intent = Intent(context, TuripActivity::class.java)
    }
}
