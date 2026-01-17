package com.on.turip.ui.compose.trip.component

import androidx.annotation.DrawableRes
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.on.turip.R
import com.on.turip.ui.common.model.trip.TripDurationModel
import com.on.turip.ui.common.model.trip.toDisplayText
import com.on.turip.ui.compose.designsystem.theme.TuripTheme
import com.on.turip.ui.compose.trip.model.TripDetailInfoModel

@Composable
fun ContentInformation(
    information: TripDetailInfoModel,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.padding(horizontal = TuripTheme.spacing.extraLarge)) {
        Row(horizontalArrangement = Arrangement.spacedBy(TuripTheme.spacing.small)) {
            ContentInfoChip(information.city)
            ContentInfoChip(information.uploadedDate)
        }

        Spacer(modifier = Modifier.height(TuripTheme.spacing.large))

        Column {
            ContentExpandableTitle(
                title = information.contentTitle,
                collapsedMaxLines = 2,
            )

            Spacer(modifier = Modifier.height(TuripTheme.spacing.small))

            ContentInfoItem(
                label = stringResource(R.string.trip_detail_period),
                text = information.duration.toDisplayText(context = LocalContext.current),
                iconPainterRes = R.drawable.ic_calendar,
            )

            Spacer(modifier = Modifier.height(TuripTheme.spacing.extraSmall))

            ContentInfoItem(
                label = stringResource(R.string.trip_detail_visit_place),
                text =
                    stringResource(
                        R.string.trip_detail_day_place_count,
                        information.placeTotalCount,
                    ),
                iconPainterRes = R.drawable.ic_place,
            )
        }
    }
}

@Composable
private fun ContentInfoChip(
    text: String,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier =
            modifier
                .border(
                    width = 1.dp,
                    color = TuripTheme.colors.gray02,
                    shape = TuripTheme.shape.chip,
                ).padding(
                    horizontal = TuripTheme.spacing.small,
                    vertical = TuripTheme.spacing.extraSmall,
                ),
    ) {
        Text(
            text = text,
            style = TuripTheme.typography.body2,
        )
    }
}

@Composable
private fun ContentInfoItem(
    label: String,
    text: String,
    @DrawableRes iconPainterRes: Int,
    modifier: Modifier = Modifier,
    textColor: Color = TuripTheme.colors.gray03,
    iconTint: Color = TuripTheme.colors.gray03,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(TuripTheme.spacing.extraSmall),
        modifier = modifier,
    ) {
        Icon(
            painter = painterResource(iconPainterRes),
            tint = iconTint,
            contentDescription = null,
            modifier = Modifier.size(12.dp),
        )

        Text(
            text = label,
            style = TuripTheme.typography.title3,
            color = textColor,
        )

        Text(
            text = text,
            style = TuripTheme.typography.body2,
            color = textColor,
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun ContentInformationPreview() {
    TuripTheme {
        ContentInformation(
            information =
                TripDetailInfoModel(
                    creatorName = "",
                    creatorThumbnail = "",
                    city = "서울",
                    videoLink = "",
                    contentTitle = "컨텐츠 제목이 2줄을 넘어가면 expandable 텍스트를 볼 수 있도록 지원한다 테스트 테스트 테스트",
                    uploadedDate = "2025-07-12",
                    placeTotalCount = 12,
                    duration = TripDurationModel(0, 1),
                ),
            modifier = Modifier.padding(vertical = TuripTheme.spacing.extraLarge),
        )
    }
}
