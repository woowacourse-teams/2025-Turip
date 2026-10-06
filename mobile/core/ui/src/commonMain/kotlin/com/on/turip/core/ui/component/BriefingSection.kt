package com.on.turip.core.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.on.turip.core.designsystem.generated.resources.Res
import com.on.turip.core.designsystem.generated.resources.retry
import com.on.turip.core.designsystem.theme.TuripTheme
import org.jetbrains.compose.resources.stringResource

/**
 * 브리핑 본문의 섹션 제목. 왼쪽 제목 + 오른쪽 보조 텍스트(개수 칩) 배치다.
 */
@Composable
fun BriefingSectionHeader(
    title: String,
    modifier: Modifier = Modifier,
    trailingText: String? = null,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = title,
            style = TuripTheme.typography.title1,
            color = TuripTheme.colors.gray04,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f, fill = false),
        )

        if (trailingText != null) {
            Text(
                text = trailingText,
                style = TuripTheme.typography.info1,
                color = TuripTheme.colors.gray03,
                modifier =
                    Modifier
                        .padding(start = TuripTheme.spacing.small)
                        .clip(TuripTheme.shape.chip)
                        .background(TuripTheme.colors.container)
                        .padding(
                            horizontal = TuripTheme.spacing.small,
                            vertical = TuripTheme.spacing.extraSmall,
                        ),
            )
        }
    }
}

/**
 * 섹션 하나가 목록 대신 짧은 안내(로딩/미지원/빈 상태/에러)를 보여줄 때 쓰는 자리.
 * [BriefingPlaceholder] 와 달리 높이를 고정하지 않아 한두 줄짜리 안내에 어울린다.
 */
@Composable
fun BriefingNotice(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    Box(
        modifier =
            modifier
                .fillMaxWidth()
                .padding(vertical = TuripTheme.spacing.large),
        contentAlignment = Alignment.Center,
        content = { content() },
    )
}

@Composable
fun BriefingNoticeText(text: String) {
    Text(
        text = text,
        style = TuripTheme.typography.body2,
        color = TuripTheme.colors.gray03,
        textAlign = TextAlign.Center,
    )
}

/** 목록 자리를 미리 잡아 두는 고정 높이 영역. 로딩·빈 상태에서 화면이 덜컥이지 않게 한다. */
@Composable
fun BriefingPlaceholder(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    Box(
        modifier =
            modifier
                .fillMaxWidth()
                .height(BRIEFING_PLACEHOLDER_HEIGHT),
        contentAlignment = Alignment.Center,
        content = { content() },
    )
}

/** 영상 목록을 불러오지 못했을 때. 자리 크기는 [BriefingPlaceholder] 와 같게 둔다. */
@Composable
fun BriefingErrorView(
    message: String,
    onRetryClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier =
            modifier
                .fillMaxWidth()
                .height(BRIEFING_PLACEHOLDER_HEIGHT),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = message,
            style = TuripTheme.typography.body1,
            color = TuripTheme.colors.gray03,
            textAlign = TextAlign.Center,
        )

        TextButton(onClick = onRetryClick) {
            Text(
                text = stringResource(Res.string.retry),
                style = TuripTheme.typography.body2,
                color = TuripTheme.colors.primary,
            )
        }
    }
}

private val BRIEFING_PLACEHOLDER_HEIGHT = 160.dp
