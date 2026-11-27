package com.kevinfreyap.jetspending.ui.screen.settings.components

import android.content.res.Configuration.UI_MODE_NIGHT_NO
import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.kevinfreyap.jetspending.R
import com.kevinfreyap.jetspending.ui.components.ViewUserProfile
import com.kevinfreyap.jetspending.ui.components.ViewUserProfilePlaceholder
import com.kevinfreyap.jetspending.ui.screen.settings.state.ProfileCardState
import com.kevinfreyap.jetspending.ui.theme.JetSpendingTheme
import com.kevinfreyap.jetspending.ui.theme.Theme
import com.kevinfreyap.jetspending.utils.shimmerEffect

@Composable
fun ProfileCard(
    state: ProfileCardState,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface (
        shape = RoundedCornerShape(20.dp),
        onClick = onClick,
        color = MaterialTheme.colorScheme.surfaceContainer,
        modifier = modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min)
    ) {
        Row (
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp)
        ) {
            ViewUserProfile(
                imageUrl = state.imageUrl,
                name = state.name,
            )

            Column(
                modifier = Modifier
                    .weight(1f)
            ) {
                Text(
                    text = state.name,
                    style = MaterialTheme.typography.titleMedium,
                    color = Theme.custom.primaryTextColor,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Text(
                    text = state.email,
                    style = MaterialTheme.typography.bodySmall,
                    color = Theme.custom.secondaryTextColor,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(Modifier.height(4.dp))

                Text(
                    text = stringResource(R.string.btn_txt_edit_profile),
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary,
                )
            }

            Icon(
                painter = painterResource(R.drawable.ic_chevron_right),
                contentDescription = null,
                tint = Theme.custom.secondaryTextColor,
                modifier = Modifier
                    .size(20.dp)
            )
        }
    }
}

@Composable
fun ProfileCardPlaceholder() {
    Surface (
        shape = RoundedCornerShape(20.dp),
        color = MaterialTheme.colorScheme.surfaceContainer,
        modifier = Modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min)
    ) {
        Row (
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp)
        ) {
            ViewUserProfilePlaceholder()

            Column(
                verticalArrangement = Arrangement.spacedBy(4.dp),
                modifier = Modifier
                    .weight(1f)
            ) {
                Text(
                    text = "",
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.Transparent,
                    modifier = Modifier
                        .clearAndSetSemantics{}
                        .fillMaxWidth(0.5f)
                        .clip(RoundedCornerShape(50))
                        .shimmerEffect(
                            Theme.custom.shimmerColor
                        )
                )

                Text(
                    text = "",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Transparent,
                    modifier = Modifier
                        .clearAndSetSemantics{}
                        .fillMaxWidth(0.6f)
                        .clip(RoundedCornerShape(50))
                        .shimmerEffect(
                            Theme.custom.shimmerColor
                        )
                )

                Text(
                    text = "",
                    style = MaterialTheme.typography.labelLarge,
                    color = Color.Transparent,
                    modifier = Modifier
                        .clearAndSetSemantics{}
                        .fillMaxWidth(0.3f)
                        .clip(RoundedCornerShape(50))
                        .shimmerEffect(
                            Theme.custom.shimmerColor
                        )
                )
            }

            Icon(
                painter = painterResource(R.drawable.ic_chevron_right),
                contentDescription = null,
                tint = Theme.custom.secondaryTextColor,
                modifier = Modifier
                    .size(20.dp)
            )
        }
    }
}

@Preview(
    showBackground = true,
    backgroundColor = 0xFFFAFCFB,
    uiMode = UI_MODE_NIGHT_NO
)
@Preview(
    showBackground = true,
    backgroundColor = 0xFF121214,
    uiMode = UI_MODE_NIGHT_YES
)
@Composable
private fun ProfileCardPreview() {
    JetSpendingTheme {
        ProfileCard(
            state = ProfileCardState(
                imageUrl = null,
                name = "John Doe",
                email = "JohnDoe@email.com",
            ),
            onClick = {}
        )
    }
}

@Preview(
    showBackground = true,
    backgroundColor = 0xFFFAFCFB,
    uiMode = UI_MODE_NIGHT_NO
)
@Preview(
    showBackground = true,
    backgroundColor = 0xFF121214,
    uiMode = UI_MODE_NIGHT_YES
)
@Composable
private fun ProfileCardPlaceholderPreview() {
    JetSpendingTheme {
        ProfileCardPlaceholder()
    }
}