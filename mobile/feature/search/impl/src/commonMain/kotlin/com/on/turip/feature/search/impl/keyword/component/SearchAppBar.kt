package com.on.turip.feature.search.impl.keyword.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.on.turip.core.designsystem.component.TuripAppBar
import com.on.turip.core.designsystem.generated.resources.*
import com.on.turip.core.designsystem.generated.resources.Res
import com.on.turip.core.designsystem.theme.TuripTheme
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@Composable
fun SearchAppBar(
    searchText: String,
    onSearchTextChanged: (text: String) -> Unit,
    onSearchAction: () -> Unit,
    onClearClick: () -> Unit,
    onBackClick: () -> Unit,
    onFocusChanged: (isFocusChanged: Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    TuripAppBar(
        modifier = modifier,
        start = {
            IconButton(
                onClick = onBackClick,
                modifier = Modifier.size(36.dp),
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = null,
                    tint = TuripTheme.colors.gray03,
                )
            }

            Spacer(modifier = Modifier.width(TuripTheme.spacing.medium))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier =
                    Modifier
                        .weight(1f)
                        .height(44.dp)
                        .background(
                            color = TuripTheme.colors.white,
                            shape = TuripTheme.shape.wideButton,
                        ).border(
                            width = 1.dp,
                            color = TuripTheme.colors.gray01,
                            shape = TuripTheme.shape.wideButton,
                        ).padding(horizontal = TuripTheme.spacing.large),
            ) {
                BasicTextField(
                    value = searchText,
                    onValueChange = onSearchTextChanged,
                    modifier =
                        Modifier
                            .weight(1f)
                            .onFocusChanged { onFocusChanged(it.isFocused) },
                    textStyle = TuripTheme.typography.title3.copy(color = TuripTheme.colors.black),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                    keyboardActions = KeyboardActions(onSearch = { onSearchAction() }),
                    decorationBox = { innerTextField ->
                        if (searchText.isEmpty()) {
                            Text(
                                text = stringResource(Res.string.search_result_hint_text),
                                style = TuripTheme.typography.title3,
                                color = TuripTheme.colors.gray02,
                            )
                        }
                        innerTextField()
                    },
                )

                if (searchText.isNotEmpty()) {
                    IconButton(
                        onClick = onClearClick,
                        modifier = Modifier.size(TuripTheme.spacing.extraLarge),
                    ) {
                        Icon(
                            painter = painterResource(Res.drawable.ic_cancel),
                            contentDescription = null,
                            tint = Color.Unspecified,
                        )
                    }
                }
            }
        },
    )
}

@Preview(showBackground = true)
@Composable
private fun SearchAppBarPreview() {
    TuripTheme {
        Surface {
            SearchAppBar(
                searchText = "",
                onSearchTextChanged = {},
                onSearchAction = {},
                onClearClick = {},
                onBackClick = {},
                onFocusChanged = {},
            )
        }
    }
}
