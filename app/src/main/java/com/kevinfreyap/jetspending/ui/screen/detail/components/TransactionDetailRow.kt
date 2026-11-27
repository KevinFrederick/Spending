package com.kevinfreyap.jetspending.ui.screen.detail.components

import android.content.res.Configuration.UI_MODE_NIGHT_NO
import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.kevinfreyap.jetspending.ui.theme.JetSpendingTheme
import com.kevinfreyap.jetspending.ui.theme.Theme
import com.kevinfreyap.jetspending.utils.shimmerEffect

@Composable
fun TransactionDetailRow(
    label: String,
    value: String,
    showDivider: Boolean,
    modifier: Modifier = Modifier,
    valueTrailingIcon: (@Composable () -> Unit)? = null,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    vertical = 12.dp
                )
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium,
                color = Theme.custom.secondaryTextColor
            )

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = value,
                    style = MaterialTheme.typography.bodySmall,
                    color = Theme.custom.primaryTextColor
                )

                valueTrailingIcon?.invoke()
            }
        }

        if (showDivider) {
            HorizontalDivider(
                thickness = 1.dp,
                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
            )
        }
    }
}

@Composable
fun TransactionDetailRowPlaceholder(
    showDivider: Boolean = true,
    labelLength: Float = 0.3f,
    valueLength: Float = 0.6f
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    vertical = 12.dp
                )
        ) {
            Text(
                text = "",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Transparent,
                modifier = Modifier
                    .clearAndSetSemantics {}
                    .fillMaxWidth(labelLength)
                    .clip(RoundedCornerShape(50))
                    .shimmerEffect(
                        shimmerColor = Theme.custom.shimmerColor
                    )
            )

            Text(
                text = "",
                style = MaterialTheme.typography.bodySmall,
                color = Color.Transparent,
                modifier = Modifier
                    .clearAndSetSemantics {}
                    .fillMaxWidth(valueLength)
                    .clip(RoundedCornerShape(50))
                    .shimmerEffect(
                        shimmerColor = Theme.custom.shimmerColor
                    )
            )
        }

        if (showDivider) {
            HorizontalDivider(
                thickness = 1.dp,
                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
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
private fun TransactionDetailRowPreview() {
    JetSpendingTheme {
        TransactionDetailRow(
            label = "Category",
            value = "Food & Drink",
            showDivider = true,
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
private fun TransactionDetailRowPlaceholderPreview() {
    JetSpendingTheme {
        TransactionDetailRowPlaceholder()
    }
}