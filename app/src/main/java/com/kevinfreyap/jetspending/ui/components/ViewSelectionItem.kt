package com.kevinfreyap.jetspending.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.kevinfreyap.jetspending.R
import com.kevinfreyap.jetspending.ui.theme.Grey700
import com.kevinfreyap.jetspending.ui.theme.JetSpendingTheme
import com.kevinfreyap.jetspending.ui.theme.Theme

@Composable
fun ViewSelectionItem (
    title: String,
    modifier: Modifier = Modifier,
    selected: Boolean = false,
    subtitle: String? = null,
    icon: Int? = null,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min)
            .padding(
                horizontal = 12.dp,
                vertical = 16.dp
            )
    ) {
        icon?.let {
            Icon(
                painter = painterResource(it),
                contentDescription = title,
                tint = Theme.custom.iconColor
            )

            Spacer(
                modifier = Modifier
                    .width(16.dp)
            )
        }

        Column (
            modifier = Modifier
                .weight(1f)
        ) {
            Text(
                text = title,
                fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
                style = MaterialTheme.typography.bodyLarge,
                color = Theme.custom.textColor,
            )

            subtitle?.let {
                Text(
                    text = it,
                    fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal,
                    style = MaterialTheme.typography.labelMedium,
                    color = Grey700
                )
            }
        }

        if (selected) {
            Icon(
                painter = painterResource(R.drawable.ic_check_24),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Preview(
    showBackground = true,
    device = Devices.PIXEL_9_PRO,
)
@Composable
fun ViewSelectionItemPreview() {
    JetSpendingTheme {
        ViewSelectionItem(
            title = "Indonesia",
            subtitle = "Indonesia (Rupiah | Rp)",
            selected = true
        )
    }
}