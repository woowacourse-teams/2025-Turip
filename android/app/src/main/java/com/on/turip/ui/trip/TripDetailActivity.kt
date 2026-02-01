package com.on.turip.ui.trip

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import com.on.turip.R
import com.on.turip.ui.common.safeStartActivityWithToast
import com.on.turip.ui.compose.designsystem.theme.TuripTheme
import com.on.turip.ui.compose.trip.TripDetailScreen
import com.on.turip.ui.compose.trip.model.MapModel
import com.on.turip.ui.login.LoginActivity
import com.on.turip.ui.main.favorite.PlaceTuripSelectionFragment
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
                    onTuripPlaceClick = { id: Long, placeName: String ->
                        if (supportFragmentManager.findFragmentByTag("place_turip_selection") == null) {
                            PlaceTuripSelectionFragment
                                .newInstance(id, placeName)
                                .show(supportFragmentManager, "place_turip_selection")
                        }
                    },
                )
            }
        }
    }

    companion object {
        const val TRIP_DETAIL_CONTENT_KEY: String = "com.on.turip.TRIP_DETAIL_CONTENT_KEY"

        fun newIntent(
            context: Context,
            contentId: Long,
        ): Intent =
            Intent(context, TripDetailActivity::class.java)
                .putExtra(TRIP_DETAIL_CONTENT_KEY, contentId)
    }
}
