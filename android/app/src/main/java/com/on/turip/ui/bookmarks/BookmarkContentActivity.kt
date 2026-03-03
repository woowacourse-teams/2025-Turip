package com.on.turip.ui.bookmarks

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.on.turip.ui.compose.bookmark.BookmarkContentListScreen
import com.on.turip.ui.compose.designsystem.theme.TuripTheme
import com.on.turip.ui.login.LoginActivity
import com.on.turip.ui.trip.TripDetailActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class BookmarkContentActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TuripTheme {
                BookmarkContentListScreen(
                    onBack = {
                        finish()
                    },
                    onNavigateToLogin = {
                        val intent: Intent =
                            LoginActivity.newIntent(this).apply {
                                flags =
                                    Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                            }
                        startActivity(intent)
                        finish()
                    },
                    onNavigateToContent = { contentId: Long ->
                        val intent: Intent =
                            TripDetailActivity.newIntent(context = this, contentId = contentId)
                        startActivity(intent)
                    },
                )
            }
        }
    }

    companion object {
        fun newIntent(context: Context): Intent = Intent(context, BookmarkContentActivity::class.java)
    }
}
