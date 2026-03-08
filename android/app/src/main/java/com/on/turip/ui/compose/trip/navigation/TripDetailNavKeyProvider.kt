package com.on.turip.ui.compose.trip.navigation

import android.content.Context
import android.content.Intent
import androidx.core.net.toUri
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.on.turip.R
import com.on.turip.navigation.NavKeyProvider
import com.on.turip.navigation.Navigator
import com.on.turip.ui.common.extensions.safeStartActivityWithToast
import com.on.turip.ui.compose.trip.model.MapModel
import com.on.turip.ui.compose.turipdetail.model.turip.TuripShareModel
import com.on.turip.ui.folder.TuripActivity
import dagger.hilt.android.qualifiers.ActivityContext
import kotlinx.serialization.modules.PolymorphicModuleBuilder
import javax.inject.Inject

class TripDetailNavKeyProvider @Inject constructor(
    @ActivityContext private val context: Context,
) : NavKeyProvider {
    override fun PolymorphicModuleBuilder<NavKey>.registerNavKeys() {
        subclass(TripDetailNavKey::class, TripDetailNavKey.serializer())
    }

    override fun EntryProviderScope<NavKey>.registerScreens(navigator: Navigator) {
        tripDetailScreen(
            navigator = navigator,
            navigateToMap = ::navigateToMap,
            navigateToWebViewUrl = ::navigateToWebViewUrl,
            navigateToAddTurip = ::navigateToAddTurip,
            navigateToShareTurip = ::navigateToShareTurip,
        )
    }

    private fun navigateToMap(mapModel: MapModel) {
        val intent = Intent(Intent.ACTION_VIEW, mapModel.uri)
        context.safeStartActivityWithToast(
            intent = intent,
            errorToastMessage = context.getString(R.string.all_snackbar_not_found_map_url),
        )
    }

    private fun navigateToWebViewUrl(url: String) {
        val intent = Intent(Intent.ACTION_VIEW, url.toUri())
        context.safeStartActivityWithToast(
            intent = intent,
            errorToastMessage = context.getString(R.string.all_snackbar_not_found_video_url),
        )
    }

    private fun navigateToAddTurip() {
        val intent: Intent = TuripActivity.newIntent(context)
        context.safeStartActivityWithToast(
            intent = intent,
            errorToastMessage = context.getString(R.string.all_snackbar_not_found_trip_add),
        )
    }

    private fun navigateToShareTurip(folderShareModel: TuripShareModel) {
        val sharedContents: String = folderShareModel.toShareFormat()

        val intent =
            createShareIntent(text = sharedContents).apply {
                putExtra(Intent.EXTRA_TITLE, folderShareModel.name)
            }

        val initialIntents =
            arrayOf(
                createShareIntent(text = sharedContents, packageName = KAKAO_PACKAGE),
                createShareIntent(text = sharedContents, packageName = INSTAGRAM_PACKAGE),
            )

        val chooserIntent =
            Intent
                .createChooser(intent, folderShareModel.name)
                .apply { putExtra(Intent.EXTRA_INITIAL_INTENTS, initialIntents) }

        context.safeStartActivityWithToast(
            intent = chooserIntent,
            errorToastMessage = context.getString(R.string.all_snackbar_not_found_share),
        )
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
