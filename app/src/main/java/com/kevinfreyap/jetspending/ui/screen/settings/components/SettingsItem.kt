package com.kevinfreyap.jetspending.ui.screen.settings.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.kevinfreyap.jetspending.R
import com.kevinfreyap.jetspending.ui.screen.settings.state.SettingsItemState
import com.kevinfreyap.jetspending.ui.screen.settings.state.SettingsItemType
import com.kevinfreyap.jetspending.ui.theme.JetSpendingTheme
import com.kevinfreyap.jetspending.ui.theme.Theme
import com.kevinfreyap.jetspending.utils.shimmerEffect

@Composable
fun SettingsItem(
    state: SettingsItemState,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    isLoading: Boolean = false
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .fillMaxWidth()
            .clickable(
                role = Role.Button,
                onClick = onClick
            )
            .padding(
                vertical = 12.dp,
                horizontal = 4.dp
            )
    ) {
        Icon(
            painter = painterResource(state.iconRes),
            contentDescription = null,
            tint = Theme.custom.secondaryTextColor,
            modifier = Modifier
                .size(20.dp)
        )

        Column(
            verticalArrangement = Arrangement.spacedBy(4.dp),
            modifier = Modifier
                .weight(1f)
        ) {
            Text(
                text = stringResource(state.name),
                style = MaterialTheme.typography.bodyLarge,
                color = Theme.custom.primaryTextColor,
            )

            Text(
                text = stringResource(state.description),
                style = MaterialTheme.typography.bodySmall,
                color = Theme.custom.secondaryTextColor,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        state.selectedItem?.let { label ->
            if (isLoading) {
                Text(
                    text = "",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Transparent,
                    modifier = Modifier
                        .clearAndSetSemantics{}
                        .fillMaxWidth(0.3f)
                        .clip(RoundedCornerShape(50))
                        .shimmerEffect(
                            Theme.custom.shimmerColor
                        )
                )
            } else {
                Text(
                    text = label,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Theme.custom.secondaryTextColor
                )
            }
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

@Preview (
    showBackground = true
)
@Composable
fun SettingsItemPreview() {
    JetSpendingTheme {
        SettingsItem(
            state = SettingsItemState(
                type = SettingsItemType.BASE_CURRENCY,
                iconRes = R.drawable.ic_currency_exchange_24,
                name = R.string.settings_base_currency,
                description = R.string.description_settings_base_currency,
                selectedItem = "IDR"
            ),
            onClick = {}
        )
    }
}