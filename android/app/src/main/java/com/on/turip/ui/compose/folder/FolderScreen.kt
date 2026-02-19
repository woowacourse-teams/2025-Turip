package com.on.turip.ui.compose.folder

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.on.turip.ui.main.favorite.model.TuripModel

@Composable
fun FolderScreen(viewModel: FolderViewModel = hiltViewModel()) {
}

private fun FolderContent(
    modifier: Modifier = Modifier,
    onFolderClick: (String) -> Unit,
    onFolderLongClick: (String) -> Unit,
    folder: TuripModel,
) {
}
