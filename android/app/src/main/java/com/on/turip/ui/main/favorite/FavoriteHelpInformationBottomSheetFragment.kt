package com.on.turip.ui.main.favorite

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.net.toUri
import androidx.fragment.app.viewModels
import com.on.turip.BuildConfig
import com.on.turip.R
import com.on.turip.databinding.BottomSheetFragmentFavoriteHelpInformationBinding
import com.on.turip.ui.common.base.BaseBottomSheetFragment
import com.on.turip.ui.main.favorite.model.HelpInformationModel
import com.on.turip.ui.main.favorite.model.HelpInformationModelType

class FavoriteHelpInformationBottomSheetFragment : BaseBottomSheetFragment<BottomSheetFragmentFavoriteHelpInformationBinding>() {
    private val viewModel: FavoriteHelpInformationViewModel by viewModels {
        FavoriteHelpInformationViewModel.provideFactory()
    }

    private val inquireMailUri: Uri
        get() {
            val fid = viewModel.userId.value?.fid ?: ""
            return "mailto:$EMAIL_RECIPIENT?subject=${Uri.encode(EMAIL_SUBJECT)}&body=${
                Uri.encode(String.format(EMAIL_BODY, fid))
            }".toUri()
        }

    private val helpInformationAdapter: FavoriteHelpInformationAdapter by lazy {
        FavoriteHelpInformationAdapter { helpInformationModel: HelpInformationModel ->
            helpInformationModel.onClick()
        }
    }

    private fun openInquiryForm() {
        val intent: Intent =
            Intent(Intent.ACTION_SENDTO).apply { data = inquireMailUri }

        startActivity(intent)
    }

    private fun openPrivacyPolicy() {
        val intent: Intent =
            Intent(
                Intent.ACTION_VIEW,
                PRIVACY_POLICY_LINK.toUri(),
            )
        startActivity(intent)
    }

    override fun inflateBinding(
        inflater: LayoutInflater,
        container: ViewGroup?,
    ): BottomSheetFragmentFavoriteHelpInformationBinding =
        BottomSheetFragmentFavoriteHelpInformationBinding.inflate(inflater, container, false)

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)

        setupAdapters()
        setupObservers()
    }

    private fun setupAdapters() {
        binding.rvBottomSheetFavoriteHelpInformation.adapter = helpInformationAdapter
    }

    private fun setupObservers() {
        viewModel.helpInformationItems.observe(viewLifecycleOwner) { helpInformationModelTypes: List<HelpInformationModelType> ->
            val helpInformationModels: List<HelpInformationModel> =
                helpInformationModelTypes.map { helpInformationModelType: HelpInformationModelType ->
                    when (helpInformationModelType) {
                        HelpInformationModelType.INQUIRY ->
                            HelpInformationModel(
                                R.drawable.ic_inquire,
                                R.string.bottom_sheet_favorite_help_information_inquiry,
                            ) { openInquiryForm() }

                        HelpInformationModelType.PRIVACY_POLICY ->
                            HelpInformationModel(
                                R.drawable.ic_document,
                                R.string.bottom_sheet_favorite_help_information_privacy_policy,
                            ) { openPrivacyPolicy() }
                    }
                }

            helpInformationAdapter.submitList(helpInformationModels)
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
            사용자 ID: %s
            """.trimIndent()
        private const val PRIVACY_POLICY_LINK =
            "https://agate-bandana-491.notion.site/23aeabcebdc180299e11d3bb2fbfaf67?source=copy_link"

        fun instance(): FavoriteHelpInformationBottomSheetFragment = FavoriteHelpInformationBottomSheetFragment()
    }
}
