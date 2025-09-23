package com.on.turip.ui.compose.main.home.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.on.turip.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeTopAppBar(modifier: Modifier = Modifier) {
    TopAppBar(
        title = {},
        navigationIcon = {
            Image(
                painter = painterResource(R.drawable.ic_logo),
                contentDescription = null,
                modifier = modifier.padding(start = 20.dp),
            )
        },
        windowInsets = WindowInsets(0),
    )
}
