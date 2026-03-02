package com.on.turip.ui.main.bookmarks

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.on.turip.databinding.FragmentFavoriteContentBinding
import com.on.turip.ui.common.base.BaseFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class BookmarkContentFragment : BaseFragment<FragmentFavoriteContentBinding>() {
    override fun inflateBinding(
        inflater: LayoutInflater,
        container: ViewGroup?,
    ): FragmentFavoriteContentBinding = FragmentFavoriteContentBinding.inflate(inflater, container, false)

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)
        showEmptyView()
    }

    private fun showEmptyView() {
        binding.customErrorView.visibility = View.GONE
        binding.pbFavoriteContentLoading.visibility = View.GONE
        binding.clFavoriteContentEmpty.visibility = View.VISIBLE
        binding.clFavoriteContentNotEmpty.visibility = View.GONE
    }

    companion object {
        fun instance(): BookmarkContentFragment = BookmarkContentFragment()
    }
}
