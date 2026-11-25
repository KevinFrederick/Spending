package com.kevinfreyap.jetspending.ui.components

import android.content.res.Configuration
import androidx.annotation.DrawableRes
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.kevinfreyap.jetspending.R
import com.kevinfreyap.jetspending.ui.theme.JetSpendingTheme
import com.kevinfreyap.jetspending.ui.theme.Theme

@Composable
fun ViewSelectionItem (
    title: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    isSelected: Boolean = false,
    subtitle: String? = null,
    @DrawableRes icon: Int? = null,
) {
    val iconColor: Color
    val titleColor: Color
    val subtitleColor: Color
    val fontWeight: FontWeight

    if (isSelected) {
        iconColor = MaterialTheme.colorScheme.primary
        titleColor = MaterialTheme.colorScheme.primary
        subtitleColor = MaterialTheme.colorScheme.primary.copy(0.7f)
        fontWeight = FontWeight.SemiBold
    } else {
        iconColor = Theme.custom.secondaryTextColor
        titleColor = Theme.custom.primaryTextColor
        subtitleColor = Theme.custom.secondaryTextColor
        fontWeight = FontWeight.Normal
    }

    Row(
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min)
            .clip(RoundedCornerShape(12.dp))
            .clickable(
                role = Role.RadioButton,
                onClick = onClick
            )
            .padding(
                horizontal = 12.dp,
                vertical = 12.dp
            )
    ) {
        icon?.let {
            Icon(
                painter = painterResource(it),
                contentDescription = null,
                tint = iconColor,
                modifier = Modifier
                    .size(20.dp)
            )
        }

        Column (
            modifier = Modifier
                .weight(1f)
        ) {
            Text(
                text = title,
                fontWeight = fontWeight,
                style = MaterialTheme.typography.bodyMedium,
                color = titleColor,
            )

            subtitle?.let {
                Text(
                    text = it,
                    style = MaterialTheme.typography.labelMedium,
                    color = subtitleColor
                )
            }
        }

        if (isSelected) {
            Icon(
                painter = painterResource(R.drawable.ic_check_24),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .size(20.dp)
            )
        }
    }
}

@Preview(
    showBackground = true,
    backgroundColor = 0xFFFFFFFF,
    uiMode = Configuration.UI_MODE_NIGHT_NO
)
@Preview(
    showBackground = true,
    backgroundColor = 0xFF1C1C1F,
    uiMode = Configuration.UI_MODE_NIGHT_YES
)
@Composable
private fun ViewSelectionItemPreview() {
    JetSpendingTheme {
        ViewSelectionItem(
            title = "Indonesia",
            subtitle = "Indonesia (Rupiah | Rp)",
            isSelected = true,
            onClick = {}
        )
    }
}