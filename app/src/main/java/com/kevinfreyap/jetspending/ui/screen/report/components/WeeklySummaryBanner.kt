package com.kevinfreyap.jetspending.ui.screen.report.components

import android.content.res.Configuration.UI_MODE_NIGHT_NO
import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.kevinfreyap.jetspending.R
import com.kevinfreyap.jetspending.ui.screen.report.state.WeeklySummaryBannerState
import com.kevinfreyap.jetspending.ui.state.UiText
import com.kevinfreyap.jetspending.ui.theme.JetSpendingTheme
import com.kevinfreyap.jetspending.ui.theme.Theme

@Composable
fun WeeklySummaryBanner(
    state: WeeklySummaryBannerState,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(20.dp),
        color = MaterialTheme.colorScheme.surfaceContainer,
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            val iconRes = if (state.isLowerSpending) R.drawable.ic_trending_down_24 else R.drawable.ic_trending_up_24
            val iconTint = if (state.isLowerSpending) Theme.custom.statusPositive else Theme.custom.statusWarning
            val iconBg = if (state.isLowerSpending) Theme.custom.highlightContainer else Theme.custom.warningContainer

            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(44.dp)
                    .background(
                        color = iconBg,
                        shape = CircleShape
                    )
            ) {
                Icon(
                    painter = painterResource(iconRes),
                    contentDescription = null,
                    tint = iconTint,
                    modifier = Modifier.size(24.dp)
                )
            }

            Column(
                verticalArrangement = Arrangement.spacedBy(2.dp),
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = state.dateRangeLabel,
                    style = MaterialTheme.typography.labelSmall,
                    color = Theme.custom.secondaryTextColor
                )

                Text(
                    text = state.headlineText.asString(),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = Theme.custom.primaryTextColor
                )

                Text(
                    text = state.supportingText.asString(),
                    style = MaterialTheme.typography.bodySmall,
                    color = Theme.custom.secondaryTextColor
                )
            }

            IconButton(
                onClick = onDismiss,
                modifier = Modifier.size(32.dp)
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_close_24),
                    contentDescription = stringResource(R.string.cd_dismiss_weekly_banner),
                    tint = Theme.custom.secondaryTextColor,
                    modifier = Modifier.size(20.dp)
                )
            }
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
private fun WeeklySummaryBannerPreview() {
    JetSpendingTheme {
        WeeklySummaryBanner(
            state = WeeklySummaryBannerState(
                dateRangeLabel = "Nov 16 – Nov 22",
                headlineText = UiText.DynamicString("Spent 12% less than last week"),
                supportingText = UiText.DynamicString("Top category: Food & Drink ($420.00)"),
                isLowerSpending = true
            ),
            onDismiss = {}
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
private fun WeeklySummaryBannerPreview_higher() {
    JetSpendingTheme {
        WeeklySummaryBanner(
            state = WeeklySummaryBannerState(
                dateRangeLabel = "Nov 16 – Nov 22",
                headlineText = UiText.DynamicString("Spent 12% more than last week"),
                supportingText = UiText.DynamicString("Top category: Food & Drink ($420.00)"),
                isLowerSpending = false
            ),
            onDismiss = {}
        )
    }
}