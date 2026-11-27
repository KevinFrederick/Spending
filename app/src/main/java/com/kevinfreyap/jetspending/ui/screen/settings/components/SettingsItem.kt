package com.kevinfreyap.jetspending.ui.screen.settings.components

import android.content.res.Configuration.UI_MODE_NIGHT_NO
import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.selection.toggleable
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
import com.kevinfreyap.jetspending.ui.components.ViewAppSwitch
import com.kevinfreyap.jetspending.ui.screen.settings.state.SettingsItemState
import com.kevinfreyap.jetspending.ui.screen.settings.state.SettingsItemType
import com.kevinfreyap.jetspending.ui.theme.JetSpendingTheme
import com.kevinfreyap.jetspending.ui.theme.Theme
import com.kevinfreyap.jetspending.utils.shimmerEffect

@Composable
fun SettingsItem(
    state: SettingsItemState,
    modifier: Modifier = Modifier,
    isLoading: Boolean = false,
    onClick: (() -> Unit)? = null,
    enabled: Boolean = true,
    contentPadding: PaddingValues = PaddingValues(vertical = 12.dp, horizontal = 4.dp)
) {
    val isSwitch = state.isChecked != null
    val contentAlpha = if (enabled) 1f else 0.38f

    val interactionModifier = if (isSwitch) {
        Modifier.toggleable(
            value = state.isChecked,
            enabled = onClick != null && enabled,
            role = Role.Switch,
            onValueChange = { onClick?.invoke() }
        )
    } else {
        Modifier.clickable(
            enabled = onClick != null && enabled,
            role = Role.Button,
            onClick = { onClick?.invoke() }
        )
    }

    Row(
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .then(interactionModifier)
            .padding(contentPadding)
    ) {
        Icon(
            painter = painterResource(state.iconRes),
            contentDescription = null,
            tint = Theme.custom.secondaryTextColor.copy(alpha = contentAlpha),
            modifier = Modifier
                .size(24.dp)
        )

        Column(
            verticalArrangement = Arrangement.spacedBy(4.dp),
            modifier = Modifier
                .weight(1f)
        ) {
            Text(
                text = stringResource(state.name),
                style = MaterialTheme.typography.bodyLarge,
                color = Theme.custom.primaryTextColor.copy(alpha = contentAlpha),
            )

            Text(
                text = stringResource(state.description),
                style = MaterialTheme.typography.bodySmall,
                color = Theme.custom.secondaryTextColor.copy(alpha = contentAlpha),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }

        if (!isSwitch) {
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
                        color = Theme.custom.secondaryTextColor.copy(alpha = contentAlpha)
                    )
                }
            }

            Icon(
                painter = painterResource(R.drawable.ic_chevron_right),
                contentDescription = null,
                tint = Theme.custom.secondaryTextColor.copy(alpha = contentAlpha),
                modifier = Modifier
                    .size(20.dp)
            )
        } else {
            ViewAppSwitch(
                checked = state.isChecked,
                enabled = enabled,
                onCheckedChange = null
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
private fun SettingsItemPreview() {
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
private fun SettingsItemPreview_Switch() {
    JetSpendingTheme {
        SettingsItem(
            state = SettingsItemState(
                type = SettingsItemType.APP_LOCK,
                iconRes = R.drawable.ic_fingerprint_24,
                name = R.string.settings_app_lock,
                description = R.string.description_settings_app_lock,
                isChecked = true
            ),
            onClick = {},
        )
    }
}