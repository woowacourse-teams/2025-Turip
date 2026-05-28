package com.on.turip.feature.invitation.impl

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.collectAsState
import com.on.turip.core.designsystem.component.TuripAppBar
import com.on.turip.core.designsystem.snackbar.LocalSnackbarDelegate
import com.on.turip.core.designsystem.theme.TuripTheme
import com.on.turip.feature.invitation.impl.viewmodel.InvitationEntryEffect
import com.on.turip.feature.invitation.impl.viewmodel.InvitationEntryIntent
import com.on.turip.feature.invitation.impl.viewmodel.InvitationEntryState
import com.on.turip.feature.invitation.impl.viewmodel.InvitationEntryViewModel
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun InvitationEntryScreen(
    deepLinkUrl: String,
    onNavigateToHome: () -> Unit,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: InvitationEntryViewModel = koinViewModel(parameters = { parametersOf(deepLinkUrl) }),
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarDelegate = LocalSnackbarDelegate.current

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                InvitationEntryEffect.NavigateToHome -> onNavigateToHome()
                InvitationEntryEffect.NavigateBack -> onNavigateBack()
                is InvitationEntryEffect.ShowSnackbar -> snackbarDelegate.showSnackbar(effect.message)
            }
        }
    }

    InvitationEntryContent(
        state = uiState,
        onIntent = viewModel::onIntent,
        modifier = modifier,
    )
}

@Composable
private fun InvitationEntryContent(
    state: InvitationEntryState,
    onIntent: (InvitationEntryIntent) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(TuripTheme.colors.background),
    ) {
        TuripAppBar(
            start = {
                // Back button placeholder — use Icon/IconButton with back arrow in practice
            },
            center = {
                Text(
                    text = "투립 초대",
                    style = TuripTheme.typography.title2,
                    color = TuripTheme.colors.black,
                )
            },
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp),
            contentAlignment = Alignment.Center,
        ) {
            when {
                state.isLoading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.size(40.dp),
                        color = TuripTheme.colors.primary,
                    )
                }

                state.shouldShowError -> {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                    ) {
                        Text(
                            text = "초대 정보를 불러올 수 없습니다.",
                            style = TuripTheme.typography.title2,
                            color = TuripTheme.colors.black,
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "링크가 유효하지 않거나 만료되었습니다.",
                            style = TuripTheme.typography.body2,
                            color = TuripTheme.colors.gray03,
                        )
                        Spacer(modifier = Modifier.height(24.dp))
                        Button(
                            onClick = { onIntent(InvitationEntryIntent.ClickBack) },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = TuripTheme.colors.primary,
                            ),
                            shape = RoundedCornerShape(8.dp),
                        ) {
                            Text(
                                text = "돌아가기",
                                style = TuripTheme.typography.body1,
                                color = TuripTheme.colors.white,
                            )
                        }
                    }
                }

                state.shouldShowContent -> {
                    val info = state.invitationInfo!!
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        Text(
                            text = "투립에 초대받았어요!",
                            style = TuripTheme.typography.title1,
                            color = TuripTheme.colors.black,
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = info.turipName,
                            style = TuripTheme.typography.display,
                            color = TuripTheme.colors.primary,
                        )

                        Text(
                            text = "멤버 ${info.memberCount}명과 함께하는 여행",
                            style = TuripTheme.typography.body1,
                            color = TuripTheme.colors.gray03,
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                        ) {
                            Button(
                                onClick = { onIntent(InvitationEntryIntent.ClickBack) },
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = TuripTheme.colors.container,
                                    contentColor = TuripTheme.colors.black,
                                ),
                                shape = RoundedCornerShape(8.dp),
                            ) {
                                Text(
                                    text = "나중에",
                                    style = TuripTheme.typography.body1,
                                )
                            }

                            Button(
                                onClick = { onIntent(InvitationEntryIntent.ClickJoin) },
                                enabled = !state.isJoining,
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = TuripTheme.colors.primary,
                                    contentColor = TuripTheme.colors.white,
                                    disabledContainerColor = TuripTheme.colors.primary,
                                    disabledContentColor = TuripTheme.colors.white,
                                ),
                                shape = RoundedCornerShape(8.dp),
                            ) {
                                if (state.isJoining) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(18.dp),
                                        color = TuripTheme.colors.white,
                                        strokeWidth = 2.dp,
                                    )
                                } else {
                                    Text(
                                        text = if (info.isAlreadyJoined) "투립으로 이동" else "참여하기",
                                        style = TuripTheme.typography.body1,
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
