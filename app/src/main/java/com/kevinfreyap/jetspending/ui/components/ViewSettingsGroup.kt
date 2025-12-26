package com.kevinfreyap.jetspending.ui.components

import android.content.res.Configuration
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.kevinfreyap.jetspending.R
import com.kevinfreyap.jetspending.ui.model.SettingsItem
import com.kevinfreyap.jetspending.ui.model.SettingsOption
import com.kevinfreyap.jetspending.ui.theme.JetSpendingTheme
import com.kevinfreyap.jetspending.ui.theme.Theme

@Composable
fun ViewSettingsGroup (
    onSettingsClicked: (SettingsOption) -> Unit,
    groupTitle: Int,
    settings: List<SettingsItem>,
    modifier: Modifier = Modifier
) {
    Column {
        Spacer(
            modifier = Modifier
                .height(16.dp)
        )

        Text(
            text = stringResource(groupTitle),
            style = MaterialTheme.typography.titleLarge,
            color = Theme.custom.textColor,
            modifier = Modifier
                .padding(
                    start = 8.dp,
                    bottom = 8.dp
                )
        )

        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = Theme.custom.cardColor
            ),
            modifier = modifier
                .fillMaxWidth()
        ) {
            settings.forEachIndexed { index, setting ->
                ViewSettingItem(
                    title = setting.title,
                    icon = setting.icon,
                    subtitle = setting.subtitle ?:
                    if (
                        setting.subtitleRes != null
                    ) {
                        stringResource(setting.subtitleRes)
                    } else null,
                    chevronIcon = setting.showChevron,
                    contentColor = setting.contentColor,
                    modifier = Modifier
                        .clickable {
                            onSettingsClicked(setting.id)
                        }
                )

                if (index < settings.lastIndex) {
                    HorizontalDivider(
                        thickness = 1.dp,
                        color = Theme.custom.hintColor,
                        modifier = Modifier
                            .padding(horizontal = 36.dp)
                    )
                }
            }
        }
    }
}

@Preview(
    showBackground = true,
    device = Devices.PIXEL_9_PRO,
    uiMode = Configuration.UI_MODE_NIGHT_NO
)
@Composable
fun ViewSettingsGroupPreview() {
    JetSpendingTheme {
        ViewSettingsGroup(
            settings = listOf(
                SettingsItem(
                    id = SettingsOption.EDIT_PROFILE,
                    title = R.string.edit_profile,
                    icon = R.drawable.ic_mode_edit_24,
                ),
                SettingsItem(
                    id = SettingsOption.NOTIFICATION,
                    title = R.string.notifications,
                    icon = R.drawable.ic_notifications_24,
                ),
                SettingsItem(
                    id = SettingsOption.PRIVACY_SECURITY,
                    title = R.string.security,
                    icon = R.drawable.ic_lock_24,
                ),
                SettingsItem(
                    id = SettingsOption.THEME,
                    title = R.string.theme,
                    icon = R.drawable.ic_nightlight_24,
                    subtitle = "Dark Mode"
                ),
            ),
            onSettingsClicked = {},
            groupTitle = R.string.general
        )
    }
}