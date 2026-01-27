package com.on.turip.ui.main.bookmarks

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.google.android.material.snackbar.Snackbar
import com.on.turip.R
import com.on.turip.databinding.FragmentFavoriteContentBinding
import com.on.turip.domain.favorite.BookmarkContent
import com.on.turip.ui.common.ItemDividerDecoration
import com.on.turip.ui.common.base.BaseFragment
import com.on.turip.ui.common.collectOnStarted
import com.on.turip.ui.common.error.ErrorUiModel
import com.on.turip.ui.common.error.ErrorUiState
import com.on.turip.ui.common.error.toUiModel
import com.on.turip.ui.login.LoginActivity
import com.on.turip.ui.trip.TripDetailActivity
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@AndroidEntryPoint
class BookmarkContentFragment : BaseFragment<FragmentFavoriteContentBinding>() {
    private val viewModel: BookmarkContentViewModel by viewModels()

    private val favoriteContentAdapter: FavoriteContentAdapter by lazy {
        FavoriteContentAdapter(
            object : FavoriteContentViewHolder.FavoriteContentListener {
                override fun onFavoriteClick(
                    contentId: Long,
                    isFavorite: Boolean,
                ) {
                    Timber.d("컨텐츠 목록의 북마크 버튼을 클릭(contentId=$contentId)\n업데이트 된 북마크 상태 =${!isFavorite}")
                    viewModel.updateBookmark(contentId, isFavorite)
                }

                override fun onFavoriteItemClick(contentId: Long) {
                    Timber.d("컨텐츠 목록의 아이템 클릭(contentId=$contentId)")
                    val intent: Intent =
                        TripDetailActivity.newIntent(
                            context = requireContext(),
                            contentId = contentId,
                        )
                    startActivity(intent)
                }
            },
        )
    }

    override fun inflateBinding(
        inflater: LayoutInflater,
        container: ViewGroup?,
    ): FragmentFavoriteContentBinding = FragmentFavoriteContentBinding.inflate(inflater, container, false)

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)
        setupAdapters()
        setupObservers()
    }

    private fun setupAdapters() {
        binding.rvFavoriteContentContents.apply {
            adapter = favoriteContentAdapter
            addItemDecoration(
                ItemDividerDecoration(
                    height = 1,
                    color = requireContext().getColor(R.color.gray_100_f0f0ee),
                ),
            )
        }
    }

    private fun setupObservers() {
        collectOnStarted(viewModel.uiState) { uiState: BookmarkContentUiState ->
            if (uiState.isLoading) showLoading()
            when {
                uiState.errorUiState != ErrorUiState.None -> showErrorView(uiState.errorUiState)
                uiState.isEmpty -> showEmptyView()
                else -> showContents(uiState.bookmarkContents)
            }
        }

        collectOnStarted(viewModel.uiEffect) { uiEffect: BookmarkContentUiEffect ->
            when (uiEffect) {
                BookmarkContentUiEffect.NavigateToLogin -> {
                    navigateToLoginScreen()
                }

                is BookmarkContentUiEffect.ShowError -> {
                    val uiModel: ErrorUiModel =
                        uiEffect.errorUiState.toUiModel() ?: return@collectOnStarted
                    view?.let { view: View ->
                        Snackbar
                            .make(view, uiModel.titleRes, Snackbar.LENGTH_INDEFINITE)
                            .apply {
                                setAction(uiModel.retryTextRes) {
                                    viewModel.handleErrorRetryRequest(uiEffect.action)
                                }
                            }.show()
                    }
                }
            }
        }
    }

    private fun showLoading() {
        binding.pbFavoriteContentLoading.visibility = View.VISIBLE
        binding.clFavoriteContentEmpty.visibility = View.GONE
        binding.clFavoriteContentNotEmpty.visibility = View.GONE
        binding.customErrorView.visibility = View.GONE
    }

    private fun showErrorView(errorUiState: ErrorUiState) {
        binding.customErrorView.visibility = View.VISIBLE
        binding.pbFavoriteContentLoading.visibility = View.GONE
        binding.clFavoriteContentEmpty.visibility = View.GONE
        binding.clFavoriteContentNotEmpty.visibility = View.GONE

        binding.customErrorView.apply {
            visibility = View.VISIBLE
            showErrorView(errorUiState)
            setOnRetryClickListener { viewModel.loadBookmarkContents() }
        }
    }

    private fun showEmptyView() {
        binding.customErrorView.visibility = View.GONE
        binding.pbFavoriteContentLoading.visibility = View.GONE
        binding.clFavoriteContentEmpty.visibility = View.VISIBLE
        binding.clFavoriteContentNotEmpty.visibility = View.GONE
    }

    private fun showContents(bookmarkContents: List<BookmarkContent>) {
        binding.customErrorView.visibility = View.GONE
        binding.pbFavoriteContentLoading.visibility = View.GONE

        binding.clFavoriteContentNotEmpty.visibility = View.VISIBLE
        binding.clFavoriteContentEmpty.visibility = View.GONE
        favoriteContentAdapter.submitList(bookmarkContents)
    }

    private fun navigateToLoginScreen() {
        val intent: Intent =
            LoginActivity
                .newIntent(requireActivity())
                .apply { flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK }
        startActivity(intent)
        requireActivity().finish()
    }

    override fun onResume() {
        super.onResume()
        viewModel.loadBookmarkContents()
    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        if (!hidden) {
            viewModel.loadBookmarkContents()
        }
    }

    companion object {
        fun instance(): BookmarkContentFragment = BookmarkContentFragment()
    }
}
