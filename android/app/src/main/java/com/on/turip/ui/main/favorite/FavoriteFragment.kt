package com.on.turip.ui.main.favorite

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.on.turip.databinding.FragmentFavoriteBinding
import com.on.turip.ui.common.base.BaseFragment

class FavoriteFragment : BaseFragment<FragmentFavoriteBinding>() {
    private val favoriteStateAdapter: FragmentStateAdapter by lazy {
        FavoriteStateAdapter(
            this,
            listOf(
                FavoriteContentFragment(),
                FavoritePlaceFragment(),
            ),
        )
    }

    override fun inflateBinding(
        inflater: LayoutInflater,
        container: ViewGroup?,
    ): FragmentFavoriteBinding = FragmentFavoriteBinding.inflate(inflater, container, false)

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
        binding.vpFavorite.adapter = favoriteStateAdapter
    }

    private fun setupTabDisplayName() {
        TabLayoutMediator(
            binding.tlFavorite,
            binding.vpFavorite,
        ) { tab: TabLayout.Tab, position: Int ->
            tab.text =
                when (position) {
                    0 -> FAVORITE_CONTENT_TAB_NAME
                    else -> FAVORITE_PLACE_TAB_NAME
                }
        }.attach()
    }

    private fun setupListeners() {
        binding.ivFavoriteMoreOptions.setOnClickListener {
            val bottomSheetDialog: FavoriteHelpInformationBottomSheetFragment =
                FavoriteHelpInformationBottomSheetFragment()
            bottomSheetDialog.show(parentFragmentManager, "favorite_help_information")
        }
    }

    companion object {
        private const val FAVORITE_CONTENT_TAB_NAME: String = "컨텐츠 찜"
        private const val FAVORITE_PLACE_TAB_NAME: String = "장소 찜"
    }
}
