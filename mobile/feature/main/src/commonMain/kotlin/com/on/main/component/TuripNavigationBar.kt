package com.on.turip.feature.main.component

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationVector1D
import androidx.compose.animation.core.tween
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalRippleConfiguration
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation3.runtime.NavKey
import com.on.turip.core.designsystem.theme.TuripTheme
import com.on.turip.feature.main.navigation.model.NavigationIconModel
import com.on.turip.feature.main.navigation.model.NavigationItem
import kotlinx.serialization.Serializable
import org.jetbrains.compose.resources.painterResource

private val NAVIGATION_BAR_CONTENT_HEIGHT = 64.dp

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
    onSelectedKeyChange: (navKey: NavKey) -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(shadowElevation = 8.dp) {
        NavigationBar(
            containerColor = TuripTheme.colors.background,
            contentColor = TuripTheme.colors.black,
            modifier =
                modifier.height(
                    NAVIGATION_BAR_CONTENT_HEIGHT +
                        WindowInsets.navigationBars
                            .asPaddingValues()
                            .calculateBottomPadding(),
                ),
        ) {
            items.forEach { (navKey: NavKey, item: NavigationItem) ->
                val isSelected: Boolean = selectedKey == navKey
                val interactionSource: MutableInteractionSource =
                    remember(navKey) { MutableInteractionSource() }
                val isPressed: Boolean by interactionSource.collectIsPressedAsState()

                val iconScale: Animatable<Float, AnimationVector1D> =
                    remember(navKey) { Animatable(1f) }
                var wasPressedBefore by remember(navKey) { mutableStateOf(false) }

                LaunchedEffect(isPressed) {
                    if (isPressed) {
                        wasPressedBefore = true
                    } else if (wasPressedBefore) {
                        iconScale.animateTo(
                            targetValue = 0.75f,
                            animationSpec = tween(durationMillis = 100),
                        )
                        iconScale.animateTo(
                            targetValue = 1f,
                            animationSpec = tween(durationMillis = 150),
                        )
                    }
                }

                CompositionLocalProvider(LocalRippleConfiguration provides null) {
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
                                            contentDescription = item.label,
                                            modifier =
                                                Modifier
                                                    .size(32.dp)
                                                    .graphicsLayer(
                                                        scaleX = iconScale.value,
                                                        scaleY = iconScale.value,
                                                    ),
                                        )
                                    }

                                    is NavigationIconModel.PainterIcon -> {
                                        Icon(
                                            painter = painterResource(item.icon.drawRes),
                                            contentDescription = item.label,
                                            modifier =
                                                Modifier
                                                    .size(32.dp)
                                                    .graphicsLayer(
                                                        scaleX = iconScale.value,
                                                        scaleY = iconScale.value,
                                                    ),
                                        )
                                    }
                                }
                                Text(
                                    text = item.label,
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
                        interactionSource = interactionSource,
                    )
                }
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
                            label = "홈",
                        ),
                    Example2 to
                        NavigationItem(
                            icon = NavigationIconModel.Vector(Icons.Default.Folder),
                            label = "튜립",
                        ),
                    Example3 to
                        NavigationItem(
                            icon = NavigationIconModel.Vector(Icons.Default.Person),
                            label = "마이",
                        ),
                ),
            selectedKey = selectedIndex,
            onSelectedKeyChange = { },
        )
    }
}
