package com.on.turip.ui.compose.turip.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.SecondaryTabRow
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.on.turip.ui.compose.designsystem.theme.TuripTheme
import com.on.turip.ui.compose.login.util.noRippleClickable
import com.on.turip.ui.compose.turip.model.MyTuripTab

@Composable
fun MyTuripTabRow(
    selectedTab: MyTuripTab,
    onTabSelected: (MyTuripTab) -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        shape = TuripTheme.shape.chip,
        border = BorderStroke(width = 1.dp, color = TuripTheme.colors.border),
        modifier = modifier.fillMaxWidth(),
    ) {
        val selectedTabIndex = selectedTab.ordinal

        SecondaryTabRow(
            selectedTabIndex = selectedTabIndex,
            containerColor = TuripTheme.colors.gray01,
            contentColor = TuripTheme.colors.black,
            indicator = {},
            divider = {},
        ) {
            MyTuripTab.entries.forEach { myTuripTab ->
                val isSelected = selectedTab == myTuripTab

                Box(
                    modifier =
                        Modifier
                            .padding(TuripTheme.spacing.extraSmall)
                            .clip(TuripTheme.shape.chip)
                            .background(if (isSelected) TuripTheme.colors.white else Color.Transparent)
                            .noRippleClickable { onTabSelected(myTuripTab) }
                            .padding(vertical = TuripTheme.spacing.medium),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = stringResource(myTuripTab.tabRes),
                        style = TuripTheme.typography.title3,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                        color = if (isSelected) TuripTheme.colors.black else TuripTheme.colors.gray03,
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MyTuripTabRowPreview() {
    var selectedTab: MyTuripTab by remember { mutableStateOf(MyTuripTab.ALL) }

    TuripTheme {
        MyTuripTabRow(
            selectedTab = selectedTab,
            onTabSelected = { selectedTab = it },
        )
    }
}
