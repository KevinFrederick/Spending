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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.kevinfreyap.jetspending.R
import com.kevinfreyap.jetspending.ui.theme.Grey500
import com.kevinfreyap.jetspending.ui.theme.JetSpendingTheme
import com.kevinfreyap.jetspending.ui.theme.Theme

@Composable
fun ViewSettingItem(
    title: Int,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
    icon: Int? = null,
    chevronIcon: Boolean = true,
    contentColor: Color? = null
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
                contentDescription = stringResource(title),
                tint = contentColor ?: Theme.custom.iconColor
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
                text = stringResource(title),
                fontWeight = FontWeight.SemiBold,
                style = MaterialTheme.typography.bodyLarge,
                color = contentColor ?: Theme.custom.textColor,
            )

            subtitle?.let {
                Text(
                    text = it,
                    fontWeight = FontWeight.SemiBold,
                    style = MaterialTheme.typography.labelMedium,
                    color = Grey500
                )
            }
        }

        if (chevronIcon) {
            Icon(
                painter = painterResource(R.drawable.ic_chevron_right),
                contentDescription = null,
                tint = contentColor ?: Theme.custom.iconColor
            )
        }
    }
}

@Preview(
    showBackground = true,
    device = Devices.PIXEL_9_PRO,
)
@Composable
fun ViewSettingItemPreview() {
    JetSpendingTheme {
        ViewSettingItem(
            title = R.string.edit_profile,
            icon = R.drawable.ic_mode_edit_24,
            subtitle = "Subtitle"
        )
    }
}