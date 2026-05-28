package com.on.turip.feature.turipdetail.impl.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.on.turip.core.designsystem.theme.TuripTheme
import com.on.turip.core.model.Member
import kotlinx.collections.immutable.ImmutableList

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MemberListSheet(
    members: ImmutableList<Member>,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = TuripTheme.colors.white,
        modifier = modifier,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = TuripTheme.spacing.extraLarge)
                .padding(bottom = TuripTheme.spacing.extraLarge),
        ) {
            Text(
                text = "참여자 ${members.size}명",
                style = TuripTheme.typography.title1,
            )

            Spacer(modifier = Modifier.height(TuripTheme.spacing.large))

            LazyColumn {
                items(items = members, key = { it.id }) { member ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = TuripTheme.spacing.medium),
                    ) {
                        if (member.profileImageUrl != null) {
                            AsyncImage(
                                model = member.profileImageUrl,
                                contentDescription = null,
                                modifier = Modifier.size(40.dp).clip(CircleShape),
                                contentScale = ContentScale.Crop,
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = null,
                                modifier = Modifier.size(40.dp),
                                tint = TuripTheme.colors.gray03,
                            )
                        }
                        Spacer(modifier = Modifier.width(TuripTheme.spacing.medium))
                        Text(
                            text = member.nickname,
                            style = TuripTheme.typography.title3,
                        )
                    }
                }
            }
        }
    }
}
