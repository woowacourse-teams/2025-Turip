package com.on.turip.ui.main.favorite

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.android.material.tabs.TabLayoutMediator
import com.on.turip.BuildConfig
import com.on.turip.databinding.FragmentFavoriteBinding
import com.on.turip.ui.common.base.BaseFragment

class FavoriteFragment : BaseFragment<FragmentFavoriteBinding>() {
//    private val viewModel: FavoriteViewModel by viewModels { FavoriteViewModel.provideFactory() }
//    private val inquireMailUri: Uri by lazy {
//        "mailto:$EMAIL_RECIPIENT?subject=${Uri.encode(EMAIL_SUBJECT)}&body=${Uri.encode(EMAIL_BODY)}".toUri()
//    }

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
    }

    private fun setupAdapters() {
        binding.vpFavorite.adapter = favoriteStateAdapter
    }

    private fun setupTabDisplayName() {
        TabLayoutMediator(binding.tlFavorite, binding.vpFavorite) { tab, position ->
            tab.text =
                when (position) {
                    0 -> FAVORITE_CONTENT_TAB_NAME
                    else -> FAVORITE_PLACE_TAB_NAME
                }
        }.attach()
    }

//    private fun setupListeners() {
//        binding.ivFavoriteInquire.setOnClickListener {
//            val intent: Intent =
//                Intent(Intent.ACTION_SENDTO).apply {
//                    data = inquireMailUri
//                }
//            startActivity(intent)
//        }
//    }

    companion object {
        private const val FAVORITE_CONTENT_TAB_NAME = "컨텐츠 찜"
        private const val FAVORITE_PLACE_TAB_NAME = "장소 찜"

        private const val EMAIL_RECIPIENT: String = "team.turip@gmail.com"
        private const val EMAIL_SUBJECT: String = "튜립 사용 문의 및 불편 사항 건의 "
        private val EMAIL_BODY: String =
            """
            문의 사항을 편하게 작성해주세요! 🙂
            
            문의 내용 작성:
            
            
            
            
            
            
            
            
            
            
            
            
            --------------------------------------------------------
            
            사용자의 튜립 앱 버전: ${BuildConfig.VERSION_NAME} (${BuildConfig.VERSION_CODE})
            사용자의 OS: Android ${Build.VERSION.RELEASE} (SDK ${Build.VERSION.SDK_INT})
            사용자 기기: ${Build.MANUFACTURER} ${Build.MODEL}
            """.trimIndent()
    }
}
