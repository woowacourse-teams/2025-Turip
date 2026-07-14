package com.on.turip.feature.home.impl.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import com.on.turip.core.designsystem.generated.resources.Res
import com.on.turip.core.designsystem.generated.resources.search_result_hint_text
import com.on.turip.core.designsystem.theme.TuripTheme
import org.jetbrains.compose.resources.stringResource

@Composable
fun SearchTextField(
    keyword: String,
    onKeywordChange: (keyword: String) -> Unit,
    onSearch: (keyword: String) -> Unit,
    modifier: Modifier = Modifier,
) {
    OutlinedTextField(
        value = keyword,
        onValueChange = onKeywordChange,
        placeholder = {
            Text(
                text = stringResource(Res.string.search_result_hint_text),
                style = TuripTheme.typography.title3,
                color = TuripTheme.colors.gray02,
            )
        },
        textStyle = TuripTheme.typography.title3,
        singleLine = true,
        modifier =
            modifier
                .fillMaxWidth()
                .background(
                    color = TuripTheme.colors.gray02,
                    shape = TuripTheme.shape.largeContainer,
                ),
        shape = TuripTheme.shape.largeContainer,
        colors =
            OutlinedTextFieldDefaults.colors(
                focusedBorderColor = TuripTheme.colors.gray02,
                unfocusedBorderColor = TuripTheme.colors.gray02,
                focusedContainerColor = TuripTheme.colors.white,
                unfocusedContainerColor = TuripTheme.colors.white,
                cursorColor = TuripTheme.colors.black,
                focusedTextColor = TuripTheme.colors.black,
                unfocusedTextColor = TuripTheme.colors.black,
            ),
        trailingIcon = {
            IconButton(onClick = {
                val trimmedKeyword = keyword.trim()
                if (trimmedKeyword.isNotBlank()) onSearch(trimmedKeyword)
            }) {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = null,
                    tint = TuripTheme.colors.gray03,
                )
            }
        },
        keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Search),
        keyboardActions =
            KeyboardActions(onSearch = {
                val trimmedKeyword = keyword.trim()
                if (trimmedKeyword.isNotBlank()) onSearch(trimmedKeyword)
            }),
    )
}

@Preview(showBackground = true)
@Composable
private fun SearchTextFieldPreview() {
    var keyword by remember { mutableStateOf("테스트 문자열 테스트 문자열 테스트 문자열 테스트 문자열 테스트 문자열 ") }
    TuripTheme {
        SearchTextField(
            keyword = keyword,
            onKeywordChange = { newKeyword -> keyword = newKeyword },
            onSearch = {},
        )
    }
}
