package com.on.turip.feature.article.impl.component

import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.on.turip.core.designsystem.component.TuripAppBar
import com.on.turip.core.designsystem.generated.resources.Res
import com.on.turip.core.designsystem.generated.resources.all_back_description
import com.on.turip.core.designsystem.theme.TuripTheme
import org.jetbrains.compose.resources.stringResource

private val APP_BAR_ICON_SIZE: Dp = 40.dp

/**
 * 히어로 위에 겹쳐 뜨는 앱 바.
 *
 * [scrollProgress] 는 히어로 높이만큼 스크롤됐을 때 1이 된다.
 * 0이면 배경이 완전히 투명하고 아이콘이 흰색이며, 1이면 불투명한 흰 배경에 검은 아이콘과 제목이 드러난다.
 */
@Composable
fun ArticleDetailAppBar(
    title: String,
    scrollProgress: Float,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val contentColor: Color =
        lerp(TuripTheme.colors.white, TuripTheme.colors.black, scrollProgress)

    TuripAppBar(
        modifier = modifier,
        containerColor = TuripTheme.colors.white.copy(alpha = scrollProgress),
        contentColor = contentColor,
        start = {
            IconButton(
                onClick = onBackClick,
                modifier = Modifier.size(APP_BAR_ICON_SIZE),
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = stringResource(Res.string.all_back_description),
                    tint = contentColor,
                )
            }
        },
        center = {
            Text(
                text = title,
                color = TuripTheme.colors.black.copy(alpha = scrollProgress),
                style = TuripTheme.typography.title2,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        },
    )
}
