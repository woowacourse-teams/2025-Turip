package com.on.turip.feature.home.impl.component

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.on.turip.core.designsystem.generated.resources.Res
import com.on.turip.core.designsystem.generated.resources.home_random_travel_cta
import com.on.turip.core.designsystem.theme.TuripTheme
import org.jetbrains.compose.resources.stringResource

@Composable
fun RandomTravelCtaButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Button(
        onClick = onClick,
        shape = TuripTheme.shape.wideButton,
        colors =
            ButtonDefaults.buttonColors(
                containerColor = TuripTheme.colors.primary,
                contentColor = TuripTheme.colors.white,
            ),
        modifier =
            modifier
                .fillMaxWidth()
                .height(CTA_HEIGHT),
    ) {
        Text(
            text = stringResource(Res.string.home_random_travel_cta),
            style = TuripTheme.typography.title2,
        )
    }
}

private val CTA_HEIGHT = 52.dp
