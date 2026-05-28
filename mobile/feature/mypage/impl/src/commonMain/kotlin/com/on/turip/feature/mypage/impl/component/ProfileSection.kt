package com.on.turip.feature.mypage.impl.component

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.on.turip.core.designsystem.theme.TuripTheme
import com.on.turip.core.model.Member
import kotlinx.coroutines.launch

@Composable
fun ProfileSection(
    profile: Member?,
    isLoading: Boolean,
    isError: Boolean,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(TuripTheme.spacing.extraLarge),
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier,
    ) {
        ProfileImage(
            imageUrl = profile?.profileImageUrl,
            isLoading = isLoading,
            isError = isError,
            onRetry = onRetry,
            modifier = Modifier.size(84.dp),
        )

        if (!isError && !isLoading && profile != null) {
            Box(
                modifier = Modifier.weight(1f),
                contentAlignment = Alignment.CenterStart,
            ) {
                Text(
                    text = profile.nickname,
                    textAlign = TextAlign.Start,
                    style = TuripTheme.typography.title1,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
    }
}

@Composable
private fun ProfileImage(
    imageUrl: String?,
    isLoading: Boolean,
    isError: Boolean,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val shape = CircleShape

    val rotation = remember { Animatable(0f) }
    var isSpinning by remember { mutableStateOf(false) }
    val interactionSource = remember { MutableInteractionSource() }
    val scope = rememberCoroutineScope()

    Box(modifier = modifier.aspectRatio(1f)) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .shadow(
                    elevation = 14.dp,
                    shape = shape,
                    ambientColor = TuripTheme.colors.black,
                    spotColor = TuripTheme.colors.black,
                )
                .clip(shape)
                .background(TuripTheme.colors.white)
                .border(width = 2.dp, color = TuripTheme.colors.gray01, shape = shape),
            contentAlignment = Alignment.Center,
        ) {
            when {
                isLoading -> CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = TuripTheme.colors.gray03,
                    strokeWidth = 2.dp,
                )
                imageUrl.isNullOrBlank() -> Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "프로필 이미지",
                    modifier = Modifier.fillMaxSize(0.5f),
                )
                else -> AsyncImage(
                    model = imageUrl,
                    contentDescription = "프로필 이미지",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize(),
                )
            }
        }

        if (isError) {
            val isPressed by interactionSource.collectIsPressedAsState()
            val animatedBackgroundColor by animateColorAsState(
                targetValue = if (isPressed) TuripTheme.colors.gray01 else TuripTheme.colors.white,
                animationSpec = tween(durationMillis = 120),
                label = "retryBackgroundAnimation",
            )

            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .size(24.dp)
                    .shadow(elevation = 4.dp, shape = CircleShape)
                    .clip(CircleShape)
                    .background(animatedBackgroundColor)
                    .border(width = 1.dp, color = TuripTheme.colors.gray02, shape = CircleShape)
                    .clickable(
                        enabled = !isSpinning,
                        interactionSource = interactionSource,
                        indication = null,
                    ) {
                        scope.launch {
                            isSpinning = true
                            rotation.snapTo(0f)
                            rotation.animateTo(360f, tween(450, easing = LinearEasing))
                            isSpinning = false
                        }
                        onRetry()
                    },
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = Icons.Rounded.Refresh,
                    contentDescription = "다시 시도",
                    tint = TuripTheme.colors.primary,
                    modifier = Modifier
                        .size(14.dp)
                        .graphicsLayer { rotationZ = rotation.value },
                )
            }
        }
    }
}
