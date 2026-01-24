package com.on.turip.ui.compose.favorite.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.on.turip.R
import com.on.turip.ui.compose.designsystem.theme.TuripTheme
import com.on.turip.ui.main.favorite.model.FavoritePlaceFolderModel
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

@Composable
fun FavoriteFoldersContent(
    listState: LazyListState,
    placeName: String,
    enableConfirm: Boolean,
    folders: ImmutableList<FavoritePlaceFolderModel>,
    onAddFolderClick: () -> Unit,
    onNavigateToFolder: (folderId: Long, folderName: String) -> Unit,
    onFavoriteClick: (folder: FavoritePlaceFolderModel) -> Unit,
    onConfirmClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Header(placeName, onAddFolderClick)

        FavoriteFolders(
            listState = listState,
            folders = folders,
            onNavigateToFolder = onNavigateToFolder,
            onFavoriteClick = onFavoriteClick,
            modifier = Modifier.weight(1f, fill = false),
        )

        ConfirmButton(
            enabled = enableConfirm,
            onClick = onConfirmClick,
            modifier =
                Modifier.padding(
                    horizontal = TuripTheme.spacing.extraLarge,
                    vertical = TuripTheme.spacing.medium,
                ),
        )
    }
}

@Composable
private fun Header(
    placeName: String,
    onAddFolderClick: () -> Unit,
) {
    FavoritePlaceFolderBottomSheetHeader(
        title = placeName,
        navigation = {
            Text(
                text = stringResource(R.string.bottom_sheet_favorite_place_folder_title),
                style = TuripTheme.typography.body2,
                color = TuripTheme.colors.gray03,
                modifier = Modifier.padding(start = TuripTheme.spacing.small),
            )
        },
        actions = {
            IconButton(
                onClick = onAddFolderClick,
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_folder_plus),
                    contentDescription = stringResource(R.string.bottom_sheet_favorite_place_folder_add_folder_description),
                )
            }
        },
    )
}

@Composable
private fun FavoriteFolders(
    listState: LazyListState,
    folders: ImmutableList<FavoritePlaceFolderModel>,
    onNavigateToFolder: (folderId: Long, folderName: String) -> Unit,
    onFavoriteClick: (folder: FavoritePlaceFolderModel) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        state = listState,
        modifier = modifier,
        contentPadding = PaddingValues(bottom = TuripTheme.spacing.small),
    ) {
        items(items = folders, key = { it.id }) { folder ->
            FavoriteFolderItem(
                folder = folder,
                onItemClick = { onNavigateToFolder(folder.id, folder.name) },
                onFavoriteClick = { onFavoriteClick(folder) },
            )
        }
    }
}

@Composable
private fun ConfirmButton(
    enabled: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val backgroundColor = if (enabled) TuripTheme.colors.primary else TuripTheme.colors.gray02
    val contentColor = if (enabled) TuripTheme.colors.white else TuripTheme.colors.gray03

    Box(
        modifier =
            modifier
                .fillMaxWidth()
                .background(color = backgroundColor, shape = TuripTheme.shape.wideButton)
                .clip(shape = TuripTheme.shape.wideButton)
                .clickable(enabled = enabled, onClick = onClick)
                .padding(vertical = TuripTheme.spacing.large),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = stringResource(R.string.bottom_sheet_favorite_place_folder_confirm),
            style = TuripTheme.typography.title2,
            color = contentColor,
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun FavoriteFoldersContentPreview() {
    val listState = rememberLazyListState()
    TuripTheme {
        Surface {
            FavoriteFoldersContent(
                listState = listState,
                placeName = "부산 경포대 해수욕장 해수욕장 해수욕장 해수욕장",
                enableConfirm = false,
                folders =
                    persistentListOf(
                        FavoritePlaceFolderModel(1L, "서울 여행", 2, false),
                        FavoritePlaceFolderModel(3L, "캐나다 여행 여행 여행 여행 여행 여행 여행 여행 여행 여행", 3, true),
                        FavoritePlaceFolderModel(2L, "일본 여행", 1, true),
                    ),
                onAddFolderClick = { },
                onFavoriteClick = { },
                onNavigateToFolder = { _, _ -> },
                onConfirmClick = {},
            )
        }
    }
}
