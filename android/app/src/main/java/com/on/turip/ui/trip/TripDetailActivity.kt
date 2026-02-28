package com.on.turip.ui.trip

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import com.on.turip.R
import com.on.turip.ui.common.extensions.safeStartActivityWithToast
import com.on.turip.ui.compose.designsystem.theme.TuripTheme
import com.on.turip.ui.compose.trip.TripDetailScreen
import com.on.turip.ui.compose.trip.model.MapModel
import com.on.turip.ui.folder.TuripActivity
import com.on.turip.ui.login.LoginActivity
import com.on.turip.ui.compose.favorite.model.turip.TuripShareModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TripDetailActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            TuripTheme {
                TripDetailScreen(
                    navigateToBack = { finish() },
                    navigateToLogin = {
                        val intent: Intent =
                            LoginActivity.newIntent(this).apply {
                                flags =
                                    Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                            }
                        startActivity(intent)
                        finish()
                    },
                    navigateToMap = { mapModel: MapModel ->
                        val intent = Intent(Intent.ACTION_VIEW, mapModel.uri)
                        safeStartActivityWithToast(
                            intent = intent,
                            errorToastMessage = getString(R.string.all_snackbar_not_found_map_url),
                        )
                    },
                    navigateToWebViewUrl = { url: String ->
                        val intent = Intent(Intent.ACTION_VIEW, url.toUri())
                        safeStartActivityWithToast(
                            intent = intent,
                            errorToastMessage = getString(R.string.all_snackbar_not_found_video_url),
                        )
                    },
                    navigateToAddTurip = {
                        val intent: Intent = TuripActivity.newIntent(this)
                        startActivity(intent)
                    },
                    navigateToShareTurip = ::navigateToShareTurip,
                )
            }
        }
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

        startActivity(chooserIntent)
    }

    private fun createShareIntent(
        text: String,
        packageName: String? = null,
    ): Intent =
        Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, text)

            packageName?.let {
                `package` = it
            }
        }

    companion object {
        const val TRIP_DETAIL_CONTENT_KEY: String = "com.on.turip.TRIP_DETAIL_CONTENT_KEY"
        private const val KAKAO_PACKAGE = "com.kakao.talk"
        private const val INSTAGRAM_PACKAGE = "com.instagram.android"

        fun newIntent(
            context: Context,
            contentId: Long,
        ): Intent =
            Intent(context, TripDetailActivity::class.java)
                .putExtra(TRIP_DETAIL_CONTENT_KEY, contentId)
    }
}
