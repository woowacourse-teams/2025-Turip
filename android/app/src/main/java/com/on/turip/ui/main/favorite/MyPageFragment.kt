package com.on.turip.ui.main.favorite

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.on.turip.databinding.FragmentMyPageBinding
import com.on.turip.ui.common.base.BaseFragment
import com.on.turip.ui.main.bookmarks.BookmarkContentFragment
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@AndroidEntryPoint
class MyPageFragment : BaseFragment<FragmentMyPageBinding>() {
    private val myPageStateAdapter: FragmentStateAdapter by lazy {
        MyPageStateAdapter(
            this,
            listOf(
                TuripPlaceFragment.instance(),
                BookmarkContentFragment.instance(),
            ),
        )
    }

    override fun inflateBinding(
        inflater: LayoutInflater,
        container: ViewGroup?,
    ): FragmentMyPageBinding = FragmentMyPageBinding.inflate(inflater, container, false)

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)

        setupAdapters()
        setupTabDisplayName()
        setupListeners()
    }

    private fun setupAdapters() {
        binding.vpMyPage.adapter = myPageStateAdapter
    }

    private fun setupTabDisplayName() {
        TabLayoutMediator(
            binding.tlMyPage,
            binding.vpMyPage,
        ) { tab: TabLayout.Tab, position: Int ->
            tab.text =
                when (position) {
                    0 -> TURIP_TAB_NAME
                    else -> BOOKMARK_TAB_NAME
                }
        }.attach()
    }

    private fun setupListeners() {
        binding.ivMyPageMoreOptions.setOnClickListener {
            Timber.d("마이 페이지 컴포즈로 마이그레이션 됨 제거 해도 되는 부분")
        }
    }

    companion object {
        private const val BOOKMARK_TAB_NAME: String = "북마크"
        private const val TURIP_TAB_NAME: String = "튜립"
    }
}
