package com.on.turip.feature.article.impl.platform

import android.content.Intent
import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.core.net.toUri

@Composable
internal actual fun rememberArticleDetailPlatformActions(): ArticleDetailPlatformActions {
    val context = LocalContext.current
    return remember(context) {
        ArticleDetailPlatformActions(
            openUrl = { url: String ->
                runCatching { context.startActivity(Intent(Intent.ACTION_VIEW, url.toUri())) }
                    .onFailure {
                        Toast.makeText(context, "링크를 열 수 없어요", Toast.LENGTH_SHORT).show()
                    }
            },
        )
    }
}
