package com.on.turip.feature.turipdetail.impl

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.on.turip.core.designsystem.component.TuripDialog
import com.on.turip.core.designsystem.snackbar.LocalSnackbarDelegate
import com.on.turip.core.designsystem.theme.TuripTheme
import com.on.turip.feature.turipdetail.impl.component.MemberListSheet
import com.on.turip.feature.turipdetail.impl.component.MoreOptionBottomSheet
import com.on.turip.feature.turipdetail.impl.component.TuripDetailAppBar
import com.on.turip.feature.turipdetail.impl.component.TuripInfoRow
import com.on.turip.feature.turipdetail.impl.component.TuripPlaces
import com.on.turip.feature.turipdetail.impl.viewmodel.TuripDetailEffect
import com.on.turip.feature.turipdetail.impl.viewmodel.TuripDetailIntent
import com.on.turip.feature.turipdetail.impl.viewmodel.TuripDetailViewModel
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TuripDetailScreen(
    turipId: Long,
    onNavigateBack: () -> Unit,
    onNavigateToLogin: () -> Unit,
    viewModel: TuripDetailViewModel = koinViewModel(parameters = { parametersOf(turipId) }),
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarDelegate = LocalSnackbarDelegate.current

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                TuripDetailEffect.NavigateBack -> onNavigateBack()
                TuripDetailEffect.NavigateToLogin -> onNavigateToLogin()
                TuripDetailEffect.ShowError ->
                    snackbarDelegate.showSnackbar(message = "작업에 실패했어요. 다시 시도해주세요")
                is TuripDetailEffect.ShowPlaceRemoved ->
                    snackbarDelegate.showSnackbar(message = "'${effect.placeName}' 장소가 삭제됐어요")
            }
        }
    }

    if (uiState.showMemberSheet) {
        MemberListSheet(
            members = uiState.members,
            onDismiss = { viewModel.onIntent(TuripDetailIntent.DismissMemberSheet) },
        )
    }

    if (uiState.showMoreOptionsSheet) {
        MoreOptionBottomSheet(
            isShared = uiState.turip?.isShared == true,
            isDefault = uiState.turip?.isDefault == true,
            onRenameClick = { viewModel.onIntent(TuripDetailIntent.ShowRenameSheet) },
            onDeleteClick = { viewModel.onIntent(TuripDetailIntent.ShowDeleteDialog) },
            onDismiss = { viewModel.onIntent(TuripDetailIntent.DismissMoreOptions) },
        )
    }

    if (uiState.showRenameSheet) {
        androidx.compose.material3.ModalBottomSheet(
            onDismissRequest = { viewModel.onIntent(TuripDetailIntent.DismissRenameSheet) },
            containerColor = TuripTheme.colors.white,
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = TuripTheme.spacing.extraLarge)
                    .padding(bottom = TuripTheme.spacing.extraLarge),
            ) {
                Text("이름 변경", style = TuripTheme.typography.title1)
                Spacer(modifier = Modifier.height(TuripTheme.spacing.large))
                OutlinedTextField(
                    value = uiState.inputName,
                    onValueChange = { viewModel.onIntent(TuripDetailIntent.UpdateName(it)) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    shape = TuripTheme.shape.wideButton,
                )
                Spacer(modifier = Modifier.height(TuripTheme.spacing.large))
                Button(
                    onClick = { viewModel.onIntent(TuripDetailIntent.ConfirmRename) },
                    enabled = uiState.inputName.isNotBlank() && !uiState.isRenaming,
                    shape = TuripTheme.shape.wideButton,
                    colors = ButtonDefaults.buttonColors(containerColor = TuripTheme.colors.primary),
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text("변경")
                }
            }
        }
    }

    if (uiState.showDeleteDialog) {
        val isShared = uiState.turip?.isShared == true
        TuripDialog(
            title = if (isShared) "튜립 나가기" else "튜립 삭제",
            message = if (isShared) "튜립에서 나가시겠어요?" else "'${uiState.turip?.name}' 튜립을 삭제하시겠어요?",
            confirmText = if (isShared) "나가기" else "삭제",
            dismissText = "취소",
            confirmButtonColor = TuripTheme.colors.error,
            dismissButtonColor = TuripTheme.colors.gray02,
            onConfirmation = { viewModel.onIntent(TuripDetailIntent.ConfirmDelete) },
            onDismissRequest = { viewModel.onIntent(TuripDetailIntent.DismissDeleteDialog) },
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .systemBarsPadding(),
    ) {
        TuripDetailAppBar(
            title = uiState.turip?.name.orEmpty(),
            onBackClick = onNavigateBack,
            onMoreClick = { viewModel.onIntent(TuripDetailIntent.ShowMoreOptions) },
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
                    Button(onClick = { viewModel.onIntent(TuripDetailIntent.Retry) }) {
                        Text("다시 시도")
                    }
                }
            }
            uiState.turip != null -> {
                TuripInfoRow(
                    placeCount = uiState.places.size,
                    memberCount = uiState.members.size,
                    isShared = uiState.turip?.isShared == true,
                    onMembersClick = { viewModel.onIntent(TuripDetailIntent.ShowMemberSheet) },
                    modifier = Modifier.padding(vertical = TuripTheme.spacing.medium),
                )

                TuripPlaces(
                    places = uiState.places,
                    onRemovePlace = { viewModel.onIntent(TuripDetailIntent.RemovePlace(it)) },
                    modifier = Modifier.fillMaxSize(),
                )
            }
        }
    }
}
