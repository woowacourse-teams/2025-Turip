package com.on.turip.feature.article.impl.platform

import android.content.Intent
import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.core.net.toUri
import com.on.turip.core.model.trip.MapType

@Composable
internal actual fun rememberArticleDetailPlatformActions(): ArticleDetailPlatformActions {
    val context = LocalContext.current
    return remember(context) {
        ArticleDetailPlatformActions(
            openUrl = { url: String ->
                // 서버에서 내려온 링크를 그대로 실행하지 않고, https + 허용된 지도 host만 연다.
                if (!MapType.isAllowedMapUrl(url)) {
                    Toast.makeText(context, "링크를 열 수 없어요", Toast.LENGTH_SHORT).show()
                    return@ArticleDetailPlatformActions
                }
                runCatching { context.startActivity(Intent(Intent.ACTION_VIEW, url.toUri())) }
                    .onFailure {
                        Toast.makeText(context, "링크를 열 수 없어요", Toast.LENGTH_SHORT).show()
                    }
            },
        )
    }
}
