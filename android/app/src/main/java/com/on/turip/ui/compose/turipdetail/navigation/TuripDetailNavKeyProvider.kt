package com.on.turip.ui.compose.turipdetail.navigation

import android.content.Context
import android.content.Intent
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.on.turip.R
import com.on.turip.navigation.NavKeyProvider
import com.on.turip.navigation.Navigator
import com.on.turip.ui.common.extensions.safeStartActivityWithToast
import com.on.turip.ui.compose.trip.model.MapModel
import com.on.turip.ui.compose.turipdetail.model.turip.TuripShareModel
import dagger.hilt.android.qualifiers.ActivityContext
import kotlinx.serialization.modules.PolymorphicModuleBuilder
import javax.inject.Inject

class TuripDetailNavKeyProvider @Inject constructor(
    @ActivityContext private val context: Context,
) : NavKeyProvider {
    override fun PolymorphicModuleBuilder<NavKey>.registerNavKeys() {
        subclass(TuripDetailNavKey::class, TuripDetailNavKey.serializer())
    }

    override fun EntryProviderScope<NavKey>.registerScreens(navigator: Navigator) {
        turipDetailScreen(
            navigator = navigator,
            navigateToMap = ::navigateToMap,
            onShareTuripByText = ::shareTuripByText,
            onShareTuripInvitationLink = ::shareTuripInvitationLink,
        )
    }

    private fun navigateToMap(mapModel: MapModel) {
        val intent = Intent(Intent.ACTION_VIEW, mapModel.uri)
        context.safeStartActivityWithToast(
            intent = intent,
            errorToastMessage = context.getString(R.string.all_snackbar_not_found_map_url),
        )
    }

    private fun shareTuripByText(folderShareModel: TuripShareModel) {
        val sharedContents: String = folderShareModel.toShareFormat()
        val chooserIntent =
            createShareChooserIntent(
                text = sharedContents,
                title = folderShareModel.name,
                baseIntentTitle = folderShareModel.name,
            )

        context.safeStartActivityWithToast(
            intent = chooserIntent,
            errorToastMessage = context.getString(R.string.all_snackbar_not_found_share),
        )
    }

    private fun shareTuripInvitationLink(invitationLink: String) {
        val chooserIntent =
            createShareChooserIntent(
                text = invitationLink,
                title = context.getString(R.string.all_turip_invite_by_link),
            )

        context.safeStartActivityWithToast(
            intent = chooserIntent,
            errorToastMessage = context.getString(R.string.all_snackbar_not_found_share),
        )
    }

    private fun createShareChooserIntent(
        text: String,
        title: String,
        baseIntentTitle: String? = null,
    ): Intent {
        val intent =
            createShareIntent(text = text).apply {
                baseIntentTitle?.let { putExtra(Intent.EXTRA_TITLE, it) }
            }

        val initialIntents =
            arrayOf(
                createShareIntent(text = text, packageName = KAKAO_PACKAGE),
                createShareIntent(text = text, packageName = INSTAGRAM_PACKAGE),
            )

        return Intent
            .createChooser(intent, title)
            .apply { putExtra(Intent.EXTRA_INITIAL_INTENTS, initialIntents) }
    }

    private fun createShareIntent(
        text: String,
        packageName: String? = null,
    ): Intent =
        Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, text)

            packageName?.let { packageName ->
                `package` = packageName
            }
        }

    private companion object {
        private const val KAKAO_PACKAGE = "com.kakao.talk"
        private const val INSTAGRAM_PACKAGE = "com.instagram.android"
    }
}
