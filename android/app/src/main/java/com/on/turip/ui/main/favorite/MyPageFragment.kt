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
import com.on.turip.ui.setting.SettingActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MyPageFragment : BaseFragment<FragmentMyPageBinding>() {
    private val myPageStateAdapter: FragmentStateAdapter by lazy {
        MyPageStateAdapter(
            this,
            listOf(
                FavoritePlaceFragment.instance(),
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
                    0 -> FAVORITE_PLACE_TAB_NAME
                    else -> FAVORITE_CONTENT_TAB_NAME
                }
        }.attach()
    }

    private fun setupListeners() {
        binding.ivMyPageMoreOptions.setOnClickListener {
            startActivity(SettingActivity.newIntent(requireContext()))
        }
    }

    companion object {
        private const val FAVORITE_CONTENT_TAB_NAME: String = "컨텐츠 찜"
        private const val FAVORITE_PLACE_TAB_NAME: String = "장소 찜"
    }
}
