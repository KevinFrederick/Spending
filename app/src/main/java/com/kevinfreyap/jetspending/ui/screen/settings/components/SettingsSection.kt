package com.kevinfreyap.jetspending.ui.screen.settings.components

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.kevinfreyap.jetspending.R
import com.kevinfreyap.jetspending.ui.screen.settings.state.SettingsItemState
import com.kevinfreyap.jetspending.ui.screen.settings.state.SettingsItemType
import com.kevinfreyap.jetspending.ui.theme.JetSpendingTheme
import com.kevinfreyap.jetspending.ui.theme.Theme

@Composable
fun SettingsSection(
    @StringRes header: Int,
    settings: List<SettingsItemState>,
    onItemClick: (SettingsItemType) -> Unit,
    modifier: Modifier = Modifier,
    isLoading: Boolean = false
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(4.dp),
        modifier = modifier
            .fillMaxWidth()
    ) {
        Text(
            text = stringResource(header),
            style = MaterialTheme.typography.labelMedium,
            color = Theme.custom.secondaryTextColor
        )

        settings.forEach { setting ->
            SettingsItem(
                state = setting,
                onClick = { onItemClick(setting.type) },
                isLoading = isLoading,
                enabled = setting.enabled
            )
        }
    }
}

@Preview (
    showBackground = true
)
@Composable
fun SettingsSectionPreview() {
    JetSpendingTheme {
        SettingsSection(
            header = R.string.settings_header_preferences,
            settings = listOf(
                SettingsItemState(
                    type = SettingsItemType.BASE_CURRENCY,
                    iconRes = R.drawable.ic_currency_exchange_24,
                    name = R.string.settings_base_currency,
                    description = R.string.description_settings_base_currency,
                    selectedItem = "IDR"
                ),
                SettingsItemState(
                    type = SettingsItemType.APPEARANCE,
                    iconRes = R.drawable.ic_light_mode_24,
                    name = R.string.settings_appearance,
                    description = R.string.description_settings_appearance,
                    selectedItem = "System"
                ),
            ),
            onItemClick = {}
        )
    }
}