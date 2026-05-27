package com.kevinfreyap.jetspending.ui.components

import android.content.res.Configuration
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.LocalMinimumInteractiveComponentSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.kevinfreyap.jetspending.ui.theme.Grey500
import com.kevinfreyap.jetspending.ui.theme.JetSpendingTheme
import com.kevinfreyap.jetspending.ui.theme.Theme

@Composable
fun ViewPreferenceRow(
    title: String,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    subtitle: String? = null,
    cardColor: Color? = null,
    contentColor: Color? = null,
    action: @Composable () -> Unit
) {
    Card (
        colors = CardDefaults.cardColors(
            containerColor = cardColor ?: Theme.custom.cardColor
        ),
        shape = RoundedCornerShape(16.dp),
        modifier = modifier
            .fillMaxWidth()
            .clip(
                shape = RoundedCornerShape(16.dp)
            )
            .clickable(
                enabled = onClick != null,
                onClick = {onClick?.invoke()}
            )
    ) {
        Row (
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Column (
                modifier = Modifier
                    .weight(1f)
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.SemiBold,
                    color = contentColor ?: Theme.custom.textColor,
                )
                subtitle?.let{
                    Text(
                        text = subtitle,
                        fontWeight = FontWeight.SemiBold,
                        style = MaterialTheme.typography.labelMedium,
                        color = Grey500
                    )
                }
            }

            Spacer(
                modifier = Modifier
                    .width(16.dp)
            )

            action()
        }
    }
}

@Preview(
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_NO
)
@Composable
fun ViewPreferenceRowPreview() {
    JetSpendingTheme {
        ViewPreferenceRow(
            title = "Daily Reminder",
            onClick = {},
            subtitle = "Everyday Reminder",
            action = {
                CompositionLocalProvider(
                    LocalMinimumInteractiveComponentSize provides 0.dp
                ) {
                    Switch(
                        checked = true,
                        onCheckedChange = null,
                    )
                }
            }
        )
    }
}