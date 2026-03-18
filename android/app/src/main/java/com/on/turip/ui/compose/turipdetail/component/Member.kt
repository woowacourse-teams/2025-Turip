package com.on.turip.ui.compose.turipdetail.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.on.turip.ui.compose.designsystem.theme.TuripTheme
import com.on.turip.ui.compose.turipdetail.model.ProfileRowStyle
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MemberListSheet(
    title: String,
    nickNames: ImmutableList<String>,
    onDismiss: () -> Unit,
    sheetState: SheetState,
    modifier: Modifier = Modifier,
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = TuripTheme.colors.white,
        modifier = modifier,
    ) {
        Column(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = TuripTheme.spacing.extraLarge),
        ) {
            Text(
                text = title,
                style = TuripTheme.typography.title1,
                color = TuripTheme.colors.black,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(),
            )

            Spacer(modifier = Modifier.height(TuripTheme.spacing.large))

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(TuripTheme.spacing.extraSmall),
                contentPadding = PaddingValues(bottom = TuripTheme.spacing.extraExtraLarge),
                modifier = Modifier.fillMaxWidth(),
            ) {
                items(nickNames) { member ->
                    MemberItem(member = member)
                }
            }
        }
    }
}

@Composable
fun MemberItem(
    member: String,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier =
            modifier
                .fillMaxWidth()
                .padding(vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        AvatarCircle(
            label = member.first(),
            profileRowStyle = ProfileRowStyle.LARGE,
        )

        Spacer(modifier = Modifier.width(TuripTheme.spacing.large))

        Text(
            text = member,
            style = TuripTheme.typography.title2,
            color = TuripTheme.colors.black,
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true, backgroundColor = 0xFFF5F5F5)
@Composable
fun MemberListSheetPreview() {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val nickNames =
        persistentListOf(
            "유범",
            "Eunseon",
            "나",
            "시원",
        )

    TuripTheme {
        MemberListSheet(
            title = "대한민국 부산광역시 부산진구 서면로",
            nickNames = nickNames,
            onDismiss = {},
            sheetState = sheetState,
        )
    }
}
