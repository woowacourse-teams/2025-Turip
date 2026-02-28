package com.on.turip.ui.bookmarks

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.on.turip.ui.compose.bookmarks.BookmarkContentScreen
import com.on.turip.ui.compose.designsystem.theme.TuripTheme
import com.on.turip.ui.login.LoginActivity
import com.on.turip.ui.trip.TripDetailActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class BookmarkContentActivity : AppCompatActivity() {
    private var hasBookmarkChanges = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        hasBookmarkChanges =
            savedInstanceState?.getBoolean(EXTRA_BOOKMARK_CONTENT_HAS_BOOKMARK_CHANGES, false)
                ?: false
        setContent {
            TuripTheme {
                BookmarkContentScreen(
                    onNavigateToBack = {
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
                    onBookmarkChanged = {
                        hasBookmarkChanges = true
                    },
                )
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean(EXTRA_BOOKMARK_CONTENT_HAS_BOOKMARK_CHANGES, hasBookmarkChanges)
    }

    override fun finish() {
        val data =
            Intent().apply {
                putExtra(EXTRA_BOOKMARK_CONTENT_HAS_BOOKMARK_CHANGES, hasBookmarkChanges)
            }
        setResult(RESULT_OK, data)
        super.finish()
    }

    companion object {
        const val EXTRA_BOOKMARK_CONTENT_HAS_BOOKMARK_CHANGES =
            "com.on.turip.ui.bookmarks.BOOKMARK_CHANGES"

        fun newIntent(context: Context): Intent = Intent(context, BookmarkContentActivity::class.java)
    }
}
