package com.on.turip.feature.search.impl.component

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
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.on.turip.core.designsystem.component.TuripAppBar
import com.on.turip.core.designsystem.theme.TuripTheme

@Composable
fun SearchAppBar(
    searchText: String,
    onSearchTextChanged: (String) -> Unit,
    onSearchAction: () -> Unit,
    onClearClick: () -> Unit,
    onBackClick: () -> Unit,
    onFocusChanged: (Boolean) -> Unit,
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
                modifier = Modifier
                    .weight(1f)
                    .height(44.dp)
                    .background(color = TuripTheme.colors.white, shape = TuripTheme.shape.wideButton)
                    .border(width = 1.dp, color = TuripTheme.colors.gray01, shape = TuripTheme.shape.wideButton)
                    .padding(horizontal = TuripTheme.spacing.large),
            ) {
                BasicTextField(
                    value = searchText,
                    onValueChange = onSearchTextChanged,
                    modifier = Modifier
                        .weight(1f)
                        .onFocusChanged { onFocusChanged(it.isFocused) },
                    textStyle = TuripTheme.typography.title3.copy(color = TuripTheme.colors.black),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                    keyboardActions = KeyboardActions(onSearch = { onSearchAction() }),
                    decorationBox = { innerTextField ->
                        if (searchText.isEmpty()) {
                            Text(
                                text = "여행지를 검색해보세요",
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
                            imageVector = Icons.Default.Close,
                            contentDescription = null,
                            tint = TuripTheme.colors.gray03,
                        )
                    }
                }
            }
        },
    )
}
