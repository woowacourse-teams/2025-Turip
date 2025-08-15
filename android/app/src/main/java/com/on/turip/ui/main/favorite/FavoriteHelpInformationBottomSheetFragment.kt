package com.on.turip.ui.main.favorite

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.net.toUri
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.on.turip.BuildConfig
import com.on.turip.R
import com.on.turip.databinding.BottomSheetFragmentFavoriteHelpInformationBinding
import com.on.turip.ui.main.favorite.model.HelpInformationModel

class FavoriteHelpInformationBottomSheetFragment : BottomSheetDialogFragment() {
    private var _binding: BottomSheetFragmentFavoriteHelpInformationBinding? = null
    val binding: BottomSheetFragmentFavoriteHelpInformationBinding get() = _binding!!
    // TODO : backingProperty 사용할 때 public으로 열어놓지 않으면 ktlint 경고문구 나오는데 무시하는 게 좋을 지, 퍼블릭으로 여는게 좋을지 ?

    private val inquireMailUri: Uri by lazy {
        "mailto:$EMAIL_RECIPIENT?subject=${Uri.encode(EMAIL_SUBJECT)}&body=${Uri.encode(EMAIL_BODY)}".toUri()
    }

    private val helpInformationModels: List<HelpInformationModel>
        get() =
            listOf(
                HelpInformationModel(
                    R.drawable.ic_inquire,
                    R.string.bottom_sheet_favorite_help_information_inquiry,
                ) { openInquiryForm() },
                HelpInformationModel(
                    R.drawable.ic_document,
                    R.string.bottom_sheet_favorite_help_information_privacy_policy,
                ) { openPrivacyPolicy() },
            )

    private val helpInformationAdapter: FavoriteHelpInformationAdapter by lazy {
        FavoriteHelpInformationAdapter { helpInformationModel: HelpInformationModel ->
            helpInformationModel.onClick()
        }
    }

    private fun openInquiryForm() {
        val intent: Intent =
            Intent(Intent.ACTION_SENDTO).apply {
                data = inquireMailUri
            }
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

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding =
            BottomSheetFragmentFavoriteHelpInformationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)

        setupAdapters()
        helpInformationAdapter.submitList(helpInformationModels)
    }

    private fun setupAdapters() {
        binding.rvBottomSheetFavoriteHelpInformation.adapter = helpInformationAdapter
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
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
        private const val PRIVACY_POLICY_LINK =
            "https://agate-bandana-491.notion.site/23aeabcebdc180299e11d3bb2fbfaf67?source=copy_link"
    }
}
