package com.kevinfreyap.jetspending.ui.screen.notification.components

import android.content.res.Configuration.UI_MODE_NIGHT_NO
import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.kevinfreyap.jetspending.R
import com.kevinfreyap.jetspending.ui.theme.JetSpendingTheme
import com.kevinfreyap.jetspending.ui.theme.Theme
import java.time.LocalTime
import java.time.format.DateTimeFormatter

@Composable
fun ReminderTime(
    hour: Int,
    minute: Int,
    enabled: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val formattedTime = remember(hour, minute) {
        LocalTime.of(hour, minute)
            .format(DateTimeFormatter.ofPattern("HH:mm"))
    }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .clickable(
                enabled = enabled,
                onClick = onClick
            )
            .padding(
                vertical = 12.dp,
                horizontal = 4.dp
            )
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_schedule_24),
                contentDescription = null,
                tint = if (enabled) {
                    Theme.custom.secondaryTextColor
                } else {
                    Theme.custom.secondaryTextColor.copy(alpha = 0.5f)
                },
                modifier = Modifier.size(24.dp)
            )

            Column(
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                Text(
                    text = stringResource(R.string.label_title_reminder_time),
                    style = MaterialTheme.typography.bodyLarge,
                    color = if (enabled) {
                        Theme.custom.primaryTextColor
                    } else {
                        Theme.custom.primaryTextColor.copy(alpha = 0.5f)
                    }
                )
                Text(
                    text = stringResource(R.string.label_subtitle_reminder_time),
                    style = MaterialTheme.typography.bodySmall,
                    color = if (enabled) {
                        Theme.custom.secondaryTextColor
                    } else {
                        Theme.custom.secondaryTextColor.copy(alpha = 0.5f)
                    },
                )
            }
        }

        // Pill-shaped time badge
        Surface(
            shape = CircleShape,
            color = if (enabled) {
                Theme.custom.highlightContainer
            } else {
                Theme.custom.highlightContainer.copy(alpha = 0.5f)
            }
        ) {
            Text(
                text = formattedTime,
                style = MaterialTheme.typography.labelLarge,
                color = if (enabled) {
                    MaterialTheme.colorScheme.primary
                } else {
                    MaterialTheme.colorScheme.primary.copy(0.5f)
                },
                modifier = Modifier.padding(
                    horizontal = 16.dp,
                    vertical = 8.dp
                )
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
private fun ReminderTimePreview() {
    JetSpendingTheme {
        ReminderTime(
            hour = 20,
            minute = 0,
            enabled = true,
            onClick = {  },
        )
    }
}