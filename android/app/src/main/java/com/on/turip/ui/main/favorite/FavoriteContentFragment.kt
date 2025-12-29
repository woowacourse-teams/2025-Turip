package com.on.turip.ui.main.favorite

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.google.android.material.snackbar.Snackbar
import com.on.turip.R
import com.on.turip.data.common.ErrorUiEffect
import com.on.turip.data.common.ErrorUiModel
import com.on.turip.data.common.ErrorUiState
import com.on.turip.data.common.toUiModel
import com.on.turip.databinding.FragmentFavoriteContentBinding
import com.on.turip.domain.favorite.FavoriteContent
import com.on.turip.ui.common.ItemDividerDecoration
import com.on.turip.ui.common.base.BaseFragment
import com.on.turip.ui.common.collectOnStarted
import com.on.turip.ui.common.event.CommonUiEffect
import com.on.turip.ui.login.LoginActivity
import com.on.turip.ui.main.favorite.model.FavoriteContentUiState
import com.on.turip.ui.trip.detail.TripDetailActivity
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@AndroidEntryPoint
class FavoriteContentFragment : BaseFragment<FragmentFavoriteContentBinding>() {
    private val viewModel: FavoriteContentViewModel by viewModels()

    private val favoriteContentAdapter: FavoriteContentAdapter by lazy {
        FavoriteContentAdapter(
            object : FavoriteContentViewHolder.FavoriteContentListener {
                override fun onFavoriteClick(
                    contentId: Long,
                    isFavorite: Boolean,
                ) {
                    Timber.d("찜 목록의 찜 버튼을 클릭(contentId=$contentId)\n업데이트 된 찜 상태 =${!isFavorite}")
                    viewModel.updateFavorite(contentId, isFavorite)
                }

                override fun onFavoriteItemClick(contentId: Long) {
                    Timber.d("찜 목록의 아이템 클릭(contentId=$contentId)")
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
        collectOnStarted(viewModel.uiState) { uiState: FavoriteContentUiState ->
            when {
                uiState.isLoading -> renderLoading()
                uiState.errorUiState != ErrorUiState.None -> renderErrorView(uiState.errorUiState)
                else -> renderContents(uiState.favoriteContents)
            }
        }

        collectOnStarted(viewModel.errorUiEffect) { errorUiEffect: ErrorUiEffect ->
            when (errorUiEffect) {
                is ErrorUiEffect.ShowSnackbar -> {
                    val uiModel: ErrorUiModel =
                        errorUiEffect.errorUiState.toUiModel() ?: return@collectOnStarted
                    view?.let { view: View ->
                        Snackbar
                            .make(view, uiModel.titleRes, Snackbar.LENGTH_LONG)
                            .apply {
                                errorUiEffect.onRetryClick?.let { action -> setAction(uiModel.retryTextRes) { action() } }
                            }.show()
                    }
                }
            }
        }

        collectOnStarted(viewModel.commonUiEffect) { commonUiEffect: CommonUiEffect ->
            when (commonUiEffect) {
                CommonUiEffect.NavigateToLogin -> navigateToLoginScreen()
            }
        }
    }

    private fun renderLoading() {
        binding.pbFavoriteContentLoading.visibility = View.VISIBLE
        binding.clFavoriteContentEmpty.visibility = View.GONE
        binding.clFavoriteContentNotEmpty.visibility = View.GONE
        binding.customErrorView.visibility = View.GONE
    }

    private fun renderErrorView(errorUiState: ErrorUiState) {
        binding.customErrorView.visibility = View.VISIBLE
        binding.pbFavoriteContentLoading.visibility = View.GONE
        binding.clFavoriteContentEmpty.visibility = View.GONE
        binding.clFavoriteContentNotEmpty.visibility = View.GONE

        binding.customErrorView.apply {
            visibility = View.VISIBLE
            render(errorUiState)
            setOnRetryClickListener { viewModel.loadFavoriteContents() }
        }
    }

    private fun renderContents(favoriteContents: List<FavoriteContent>) {
        binding.customErrorView.visibility = View.GONE
        binding.pbFavoriteContentLoading.visibility = View.GONE

        if (favoriteContents.isEmpty()) {
            binding.clFavoriteContentEmpty.visibility = View.VISIBLE
            binding.clFavoriteContentNotEmpty.visibility = View.GONE
        } else {
            binding.clFavoriteContentNotEmpty.visibility = View.VISIBLE
            binding.clFavoriteContentEmpty.visibility = View.GONE
            favoriteContentAdapter.submitList(favoriteContents)
        }
    }

    private fun navigateToLoginScreen() {
        val intent: Intent =
            LoginActivity.newIntent(requireActivity()).apply {
                flags =
                    Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
        startActivity(intent)
        requireActivity().finish()
    }

    override fun onResume() {
        super.onResume()
        viewModel.loadFavoriteContents()
    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        if (!hidden) {
            viewModel.loadFavoriteContents()
        }
    }

    companion object {
        fun instance(): FavoriteContentFragment = FavoriteContentFragment()
    }
}
