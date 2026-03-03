package com.on.turip.ui.compose.mypage.navigation

import android.content.Intent
import android.net.Uri
import androidx.core.net.toUri
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.on.turip.R
import com.on.turip.navigation.NavKeyProvider
import com.on.turip.navigation.Navigator
import com.on.turip.ui.common.extensions.safeStartActivityWithToast
import com.on.turip.ui.compose.mypage.model.InquiryMail
import dagger.hilt.android.qualifiers.ActivityContext
import jakarta.inject.Inject
import kotlinx.serialization.modules.PolymorphicModuleBuilder
import android.content.Context

class MyPageNavKeyProvider @Inject constructor(
    @ActivityContext private val context: Context,
) : NavKeyProvider {
    override fun PolymorphicModuleBuilder<NavKey>.registerNavKeys() {
        subclass(MyPageNavKey::class, MyPageNavKey.serializer())
    }

    override fun EntryProviderScope<NavKey>.registerScreens(navigator: Navigator) {
        myPageScreen(
            navigator = navigator,
            openInquiry = ::openInquiry,
            openPrivacyPolicy = ::openPrivacyPolicy,
        )
    }

    private fun openInquiry(mail: InquiryMail) {
        val uri =
            "mailto:${InquiryMail.RECIPIENT}?subject=${Uri.encode(InquiryMail.TITLE)}&body=${
                Uri.encode(mail.content)
            }".toUri()

        val intent = Intent(Intent.ACTION_SENDTO).apply { data = uri }
        context.safeStartActivityWithToast(
            intent = intent,
            errorToastMessage = context.getString(R.string.all_snackbar_not_found_inquiry_url),
        )
    }

    private fun openPrivacyPolicy(url: String) {
        val intent = Intent(Intent.ACTION_VIEW, url.toUri())
        context.safeStartActivityWithToast(
            intent = intent,
            errorToastMessage = context.getString(R.string.all_snackbar_not_found_privacy_policy_url),
        )
    }
}
