package com.on.turip.ui.compose.home.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.on.turip.R
import com.on.turip.ui.compose.designsystem.theme.TuripTheme

@Composable
fun SearchTextField(
    keyword: String,
    onKeywordChange: (String) -> Unit,
    onSearch: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    OutlinedTextField(
        value = keyword,
        onValueChange = onKeywordChange,
        placeholder = {
            Text(
                text = stringResource(R.string.search_result_hint_text),
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
                    shape = RoundedCornerShape(16.dp),
                ),
        shape = RoundedCornerShape(16.dp),
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
                    painter = painterResource(R.drawable.btn_search),
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
