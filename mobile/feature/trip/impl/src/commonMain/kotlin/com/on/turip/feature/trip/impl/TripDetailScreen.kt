package com.on.turip.feature.trip.impl

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.on.turip.core.designsystem.snackbar.LocalSnackbarDelegate
import com.on.turip.core.designsystem.theme.TuripTheme
import com.on.turip.core.model.Place
import com.on.turip.feature.trip.impl.component.TripDetailAppBar
import com.on.turip.feature.trip.impl.viewmodel.TripDetailEffect
import com.on.turip.feature.trip.impl.viewmodel.TripDetailIntent
import com.on.turip.feature.trip.impl.viewmodel.TripDetailViewModel
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun TripDetailScreen(
    contentId: Long,
    onNavigateBack: () -> Unit,
    onNavigateToLogin: () -> Unit,
    viewModel: TripDetailViewModel = koinViewModel(parameters = { parametersOf(contentId) }),
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarDelegate = LocalSnackbarDelegate.current

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                TripDetailEffect.NavigateToLogin -> onNavigateToLogin()
                is TripDetailEffect.ShowBookmarkStatus ->
                    snackbarDelegate.showSnackbar(
                        message = if (effect.isBookmarked) "북마크에 추가됐어요" else "북마크에서 제거됐어요",
                    )
                TripDetailEffect.ShowBookmarkError ->
                    snackbarDelegate.showSnackbar(message = "북마크 업데이트에 실패했어요")
                is TripDetailEffect.OpenVideoUrl -> { /* Platform-specific URL opening handled by navigation */ }
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .systemBarsPadding(),
    ) {
        TripDetailAppBar(
            isBookmarked = uiState.content?.isBookmarked == true,
            isLoading = uiState.isLoading,
            isError = uiState.isError,
            onBackClick = onNavigateBack,
            onBookmarkClick = { viewModel.onIntent(TripDetailIntent.ToggleBookmark) },
        )

        when {
            uiState.isLoading -> Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center,
            ) {
                CircularProgressIndicator()
            }
            uiState.isError -> Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center,
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("불러오기에 실패했어요", style = TuripTheme.typography.title1)
                    Spacer(modifier = Modifier.height(TuripTheme.spacing.large))
                    Button(onClick = { viewModel.onIntent(TripDetailIntent.Retry) }) {
                        Text("다시 시도")
                    }
                }
            }
            uiState.content != null -> {
                val content = uiState.content!!

                LazyColumn(
                    contentPadding = PaddingValues(bottom = TuripTheme.spacing.extraLarge),
                ) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .aspectRatio(16f / 9f)
                                .background(TuripTheme.colors.black),
                            contentAlignment = Alignment.Center,
                        ) {
                            content.videoData.thumbnailUrl?.let { thumbnailUrl ->
                                AsyncImage(
                                    model = thumbnailUrl,
                                    contentDescription = content.title,
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.fillMaxSize(),
                                )
                            }
                            Box(
                                modifier = Modifier
                                    .size(56.dp)
                                    .clip(CircleShape)
                                    .background(TuripTheme.colors.black.copy(alpha = 0.5f)),
                                contentAlignment = Alignment.Center,
                            ) {
                                Icon(
                                    imageVector = Icons.Default.PlayArrow,
                                    contentDescription = "동영상 재생",
                                    tint = TuripTheme.colors.white,
                                    modifier = Modifier.size(32.dp),
                                )
                            }
                        }
                    }

                    item {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(TuripTheme.spacing.extraLarge),
                        ) {
                            Text(
                                text = content.title,
                                style = TuripTheme.typography.title1,
                                color = TuripTheme.colors.black,
                            )

                            Spacer(modifier = Modifier.height(TuripTheme.spacing.medium))

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                AsyncImage(
                                    model = content.creator.profileImageUrl,
                                    contentDescription = content.creator.name,
                                    modifier = Modifier.size(36.dp).clip(CircleShape),
                                    contentScale = ContentScale.Crop,
                                )
                                Spacer(modifier = Modifier.width(TuripTheme.spacing.medium))
                                Text(
                                    text = content.creator.name,
                                    style = TuripTheme.typography.title3,
                                    color = TuripTheme.colors.gray03,
                                )
                            }

                            Spacer(modifier = Modifier.height(TuripTheme.spacing.medium))

                            if (content.description.isNotBlank()) {
                                Text(
                                    text = content.description,
                                    style = TuripTheme.typography.body2,
                                    color = TuripTheme.colors.gray04,
                                    maxLines = 3,
                                    overflow = TextOverflow.Ellipsis,
                                )
                                Spacer(modifier = Modifier.height(TuripTheme.spacing.medium))
                            }

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(TuripTheme.shape.wideButton)
                                    .clickable { viewModel.onIntent(TripDetailIntent.ToggleBookmark) }
                                    .background(
                                        color = TuripTheme.colors.primary,
                                        shape = TuripTheme.shape.wideButton,
                                    )
                                    .padding(vertical = TuripTheme.spacing.medium),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Icon(
                                    imageVector = if (content.isBookmarked) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                                    tint = TuripTheme.colors.white,
                                    contentDescription = null,
                                    modifier = Modifier.size(24.dp),
                                )
                                Spacer(modifier = Modifier.width(TuripTheme.spacing.extraSmall))
                                Text(
                                    text = if (content.isBookmarked) "북마크 완료" else "북마크",
                                    color = TuripTheme.colors.white,
                                    style = TuripTheme.typography.title2,
                                )
                            }
                        }
                    }

                    if (content.places.isNotEmpty()) {
                        item {
                            HorizontalDivider(
                                color = TuripTheme.colors.gray01,
                                modifier = Modifier.padding(horizontal = TuripTheme.spacing.extraLarge),
                            )
                            Text(
                                text = "여행 장소 ${content.places.size}곳",
                                style = TuripTheme.typography.title2,
                                modifier = Modifier.padding(
                                    horizontal = TuripTheme.spacing.extraLarge,
                                    vertical = TuripTheme.spacing.medium,
                                ),
                            )
                        }

                        items(items = content.places, key = { it.id }) { place ->
                            PlaceItem(
                                place = place,
                                modifier = Modifier.padding(
                                    horizontal = TuripTheme.spacing.extraLarge,
                                    vertical = TuripTheme.spacing.extraSmall,
                                ),
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun PlaceItem(place: Place, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(TuripTheme.spacing.medium),
    ) {
        Icon(
            imageVector = Icons.Default.LocationOn,
            contentDescription = null,
            tint = TuripTheme.colors.primary,
            modifier = Modifier.size(20.dp),
        )
        Column {
            Text(
                text = place.name,
                style = TuripTheme.typography.title3,
                color = TuripTheme.colors.black,
            )
            Text(
                text = place.category,
                style = TuripTheme.typography.info2,
                color = TuripTheme.colors.gray03,
            )
        }
    }
}
