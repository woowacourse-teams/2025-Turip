package com.on.turip.ui.main.favorite

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import com.on.turip.R
import com.on.turip.databinding.FragmentFavoriteContentBinding
import com.on.turip.domain.favorite.FavoriteContent
import com.on.turip.ui.common.ItemDividerDecoration
import com.on.turip.ui.common.base.BaseFragment
import com.on.turip.ui.trip.detail.TripDetailActivity
import timber.log.Timber

class FavoriteContentFragment : BaseFragment<FragmentFavoriteContentBinding>() {
    private val viewModel: FavoriteContentViewModel by viewModels { FavoriteContentViewModel.provideFactory() }

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

                override fun onFavoriteItemClick(
                    contentId: Long,
                    creatorId: Long,
                ) {
                    Timber.d("찜 목록의 아이템 클릭(contentId=$contentId)")
                    val intent: Intent =
                        TripDetailActivity.newIntent(
                            context = requireContext(),
                            contentId = contentId,
                            creatorId = creatorId,
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
            itemAnimator = null
            addItemDecoration(
                ItemDividerDecoration(
                    height = 1,
                    color = ContextCompat.getColor(context, R.color.gray_100_f0f0ee),
                ),
            )
        }
    }

    private fun setupObservers() {
        viewModel.favoriteContents.observe(viewLifecycleOwner) { favoriteContents: List<FavoriteContent> ->
            handleVisibleByHasContent(favoriteContents)
            favoriteContentAdapter.submitList(favoriteContents)
        }
    }

    private fun handleVisibleByHasContent(favoriteContents: List<FavoriteContent>) {
        if (favoriteContents.isEmpty()) {
            binding.clFavoriteContentEmpty.visibility = View.VISIBLE
            binding.clFavoriteContentNotEmpty.visibility = View.GONE
        } else {
            binding.clFavoriteContentEmpty.visibility = View.GONE
            binding.clFavoriteContentNotEmpty.visibility = View.VISIBLE
        }
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
    }
}
