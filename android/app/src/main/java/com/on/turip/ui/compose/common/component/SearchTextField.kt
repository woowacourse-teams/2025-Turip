package com.on.turip.ui.compose.common.component

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
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.on.turip.R
import com.on.turip.ui.compose.theme.TuripTypography

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
                style = TuripTypography.titleSmall,
                color = colorResource(R.color.gray_200_c1c1c1),
            )
        },
        textStyle =
            TuripTypography.titleSmall.copy(letterSpacing = 1.5.sp),
        singleLine = true,
        modifier =
            modifier
                .fillMaxWidth()
                .background(
                    color = colorResource(R.color.gray_200_c1c1c1),
                    shape = RoundedCornerShape(16.dp),
                ),
        shape = RoundedCornerShape(16.dp),
        colors =
            OutlinedTextFieldDefaults.colors(
                focusedBorderColor = colorResource(R.color.gray_200_c1c1c1),
                unfocusedBorderColor = colorResource(R.color.gray_200_c1c1c1),
                focusedContainerColor = colorResource(R.color.pure_white_ffffff),
                unfocusedContainerColor = colorResource(R.color.pure_white_ffffff),
                cursorColor = colorResource(R.color.pure_black_151515),
                focusedTextColor = colorResource(R.color.pure_black_151515),
                unfocusedLabelColor = colorResource(R.color.pure_black_151515),
            ),
        trailingIcon = {
            IconButton(onClick = {
                if (keyword.isNotBlank()) onSearch(keyword)
            }) {
                Icon(
                    painter = painterResource(R.drawable.btn_search),
                    contentDescription = null,
                    tint = colorResource(R.color.gray_300_5b5b5b),
                )
            }
        },
        keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Search),
        keyboardActions =
            KeyboardActions(onSearch = { if (keyword.isNotBlank()) onSearch(keyword) }),
    )
}

@Preview(showBackground = true)
@Composable
private fun SearchTextFieldPreview() {
    var keyword by remember { mutableStateOf("테스트 문자열 테스트 문자열 테스트 문자열 테스트 문자열 테스트 문자열 ") }
    SearchTextField(
        keyword = keyword,
        onKeywordChange = { newKeyword -> keyword = newKeyword },
        onSearch = {},
    )
}
