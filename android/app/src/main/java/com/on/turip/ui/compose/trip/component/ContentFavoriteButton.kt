package com.on.turip.ui.compose.trip.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.on.turip.R
import com.on.turip.ui.compose.designsystem.theme.TuripTheme

@Composable
fun ContentFavoriteButton(
    isContentFavorite: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val iconRes =
        if (isContentFavorite) R.drawable.btn_bookmark_selected else R.drawable.btn_bookmark_normal

    val titleRes =
        if (isContentFavorite) R.string.trip_detail_trip_favorite_select else R.string.trip_detail_trip_favorite_normal

    Row(
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
        modifier =
            modifier
                .clickable(onClick = onClick)
                .background(color = TuripTheme.colors.primary, shape = TuripTheme.shape.wideButton)
                .fillMaxWidth()
                .padding(vertical = 12.dp),
    ) {
        Icon(
            painter = painterResource(iconRes),
            tint = TuripTheme.colors.white,
            contentDescription = null,
            modifier = Modifier.size(24.dp),
        )

        Spacer(modifier = Modifier.width(4.dp))

        Text(
            text = stringResource(titleRes),
            color = TuripTheme.colors.white,
            style = TuripTheme.typography.title2,
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun FavoriteContentFavoriteButtonPreview() {
    TuripTheme {
        ContentFavoriteButton(
            isContentFavorite = true,
            onClick = {},
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun DefaultContentFavoriteButtonPreview() {
    TuripTheme {
        ContentFavoriteButton(
            isContentFavorite = false,
            onClick = {},
        )
    }
}
