package com.on.turip.feature.turip.impl.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.on.turip.core.designsystem.theme.TuripTheme
import com.on.turip.feature.turip.impl.viewmodel.MyTuripTab

@Composable
fun MyTuripTabRow(
    selectedTab: MyTuripTab,
    onTabSelected: (MyTuripTab) -> Unit,
    modifier: Modifier = Modifier,
) {
    androidx.compose.material3.Card(
        shape = TuripTheme.shape.chip,
        border = BorderStroke(width = 1.dp, color = TuripTheme.colors.border),
        modifier = modifier.fillMaxWidth(),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(TuripTheme.colors.gray01),
        ) {
            MyTuripTab.entries.forEach { tab ->
                val isSelected = selectedTab == tab
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .padding(TuripTheme.spacing.extraSmall)
                        .clip(TuripTheme.shape.chip)
                        .background(if (isSelected) TuripTheme.colors.white else Color.Transparent)
                        .clickable { onTabSelected(tab) }
                        .padding(vertical = TuripTheme.spacing.medium),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = tab.label,
                        style = TuripTheme.typography.title3,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                        color = if (isSelected) TuripTheme.colors.black else TuripTheme.colors.gray03,
                    )
                }
            }
        }
    }
}
