package com.on.turip.ui.common

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation3.runtime.NavKey
import com.on.turip.R
import com.on.turip.ui.compose.designsystem.source.NoRippleInteractionSource
import com.on.turip.ui.compose.designsystem.theme.TuripTheme
import com.on.turip.ui.compose.main.navigation.model.NavigationIconModel
import com.on.turip.ui.compose.main.navigation.model.NavigationItem
import kotlinx.serialization.Serializable

/**
 * 튜립 디자인 시스템의 하단 내비게이션 바.
 * 화면 하단에 메뉴 목록을 표시하고, 선택된 메뉴를 강조합니다.
 *
 * @param items 표시할 [NavKey]와[NavigationItem]의 Map. 각 아이템은 라벨, 기본 아이콘으로 구성됩니다.
 * @param selectedKey 현재 선택된 아이템의 인덱스.
 * @param onSelectedKeyChange 아이템을 클릭했을 때 호출될 콜백. 클릭된 아이템의 인덱스를 전달합니다.
 * @param modifier 내비게이션 바에 적용할 [Modifier].
 */
@Composable
fun TuripNavigationBar(
    items: Map<NavKey, NavigationItem>,
    selectedKey: NavKey,
    onSelectedKeyChange: (selectNavKey: NavKey) -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(shadowElevation = 8.dp) {
        NavigationBar(
            containerColor = TuripTheme.colors.white,
            contentColor = TuripTheme.colors.black,
            modifier = modifier,
        ) {
            items.forEach { (navKey, item) ->
                val isSelected = selectedKey == navKey
                NavigationBarItem(
                    selected = isSelected,
                    onClick = { onSelectedKeyChange(navKey) },
                    icon = {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(2.dp),
                        ) {
                            when (item.icon) {
                                is NavigationIconModel.Vector -> {
                                    Icon(
                                        imageVector = item.icon.imageVector,
                                        contentDescription = stringResource(item.labelRes),
                                        modifier = Modifier.size(32.dp),
                                    )
                                }

                                is NavigationIconModel.PainterIcon -> {
                                    Icon(
                                        painter = painterResource(item.icon.drawRes),
                                        contentDescription = stringResource(item.labelRes),
                                        modifier = Modifier.size(32.dp),
                                    )
                                }
                            }
                            Text(
                                text = stringResource(item.labelRes),
                                style = TuripTheme.typography.info1,
                            )
                        }
                    },
                    colors =
                        NavigationBarItemDefaults.colors(
                            indicatorColor = Color.Transparent,
                            selectedIconColor = TuripTheme.colors.gray03,
                            unselectedIconColor = TuripTheme.colors.gray02,
                        ),
                    interactionSource = NoRippleInteractionSource,
                )
            }
        }
    }
}

@Serializable
private data object Example1 : NavKey

@Serializable
private data object Example2 : NavKey

@Serializable
private data object Example3 : NavKey

@Preview(showBackground = true, backgroundColor = 0)
@Composable
private fun TuripNavigationBarPreview() {
    var selectedIndex by remember { mutableStateOf(Example1) }
    TuripTheme {
        TuripNavigationBar(
            items =
                mapOf(
                    Example1 to
                        NavigationItem(
                            icon = NavigationIconModel.Vector(Icons.Default.Home),
                            labelRes = R.string.bottom_navigation_home,
                        ),
                    Example2 to
                        NavigationItem(
                            icon = NavigationIconModel.Vector(Icons.Default.Folder),
                            labelRes = R.string.bottom_navigation_my_turip,
                        ),
                    Example3 to
                        NavigationItem(
                            icon = NavigationIconModel.Vector(Icons.Default.Person),
                            labelRes = R.string.bottom_navigation_my_page,
                        ),
                ),
            selectedKey = selectedIndex,
            onSelectedKeyChange = { },
        )
    }
}
