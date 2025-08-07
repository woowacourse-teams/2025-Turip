package com.on.turip.ui.main.favorite

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.fragment.app.viewModels
import com.on.turip.BuildConfig
import com.on.turip.R
import com.on.turip.databinding.FragmentFavoriteBinding
import com.on.turip.ui.common.ItemDividerDecoration
import com.on.turip.ui.common.base.BaseFragment
import com.on.turip.ui.trip.detail.TripDetailActivity
import timber.log.Timber

class FavoriteFragment : BaseFragment<FragmentFavoriteBinding>() {
    private val viewModel: FavoriteViewModel by viewModels { FavoriteViewModel.provideFactory() }
    private val inquireMailUri: Uri by lazy {
        "mailto:$EMAIL_RECIPIENT?subject=${Uri.encode(EMAIL_SUBJECT)}&body=${Uri.encode(EMAIL_BODY)}".toUri()
    }
    private val favoriteItemAdapter: FavoriteItemAdapter by lazy {
        FavoriteItemAdapter(
            object : FavoriteItemViewHolder.OnFavoriteItemListener {
                override fun onFavoriteClick(contentId: Long) {
                    Timber.d("찜 목록의 찜 버튼을 클릭(contentId=$contentId)")
                }

                override fun onFavoriteItemClick(
                    contentId: Long,
                    creatorId: Long,
                ) {
                    Timber.d("찜 목록의 아이템 클릭(contentId=$contentId)")
                    val intent =
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // TODO: Use the ViewModel
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
        setupListeners()
    }

    private fun setupAdapters() {
        binding.rvFavoriteContent.apply {
            adapter = favoriteItemAdapter
            addItemDecoration(
                ItemDividerDecoration(
                    height = 8,
                    color = ContextCompat.getColor(context, R.color.gray_100_f0f0ee),
                ),
            )
        }
    }

    private fun setupListeners() {
        binding.ivFavoriteInquire.setOnClickListener {
            val intent: Intent =
                Intent(Intent.ACTION_SENDTO).apply {
                    data = inquireMailUri
                }
            startActivity(intent)
        }
    }

    companion object {
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
