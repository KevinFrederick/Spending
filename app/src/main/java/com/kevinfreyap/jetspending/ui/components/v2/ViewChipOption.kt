package com.kevinfreyap.jetspending.ui.components.v2

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.kevinfreyap.jetspending.R
import com.kevinfreyap.jetspending.ui.theme.JetSpendingTheme
import com.kevinfreyap.jetspending.ui.theme.Theme

@Composable
fun ViewChipOption(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    isSelected: Boolean = false
) {
    val backgroundColor: Color
    val contentColor: Color

    if (isSelected) {
        backgroundColor = Theme.custom.highlightContainer
        contentColor = MaterialTheme.colorScheme.primary
    } else {
        backgroundColor = MaterialTheme.colorScheme.surfaceContainer
        contentColor = Theme.custom.secondaryTextColor
    }

    Surface(
        shape = RoundedCornerShape(12.dp),
        color = backgroundColor,
        onClick = onClick,
        modifier = modifier
            .height(36.dp)
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .padding(
                    horizontal = 12.dp
                )
        ) {
            if (isSelected) {
                Icon(
                    painter = painterResource(R.drawable.ic_check_24),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier
                        .size(16.dp)
                )
            }

            Text(
                text = text,
                style = MaterialTheme.typography.labelLarge,
                color = contentColor
            )
        }
    }
}

@Preview
@Composable
fun ViewChipOptionPreview_NotSelected() {
    JetSpendingTheme {
        ViewChipOption(
            text = "Last 7 days",
            isSelected = false,
            onClick = {}
        )
    }
}


@Preview
@Composable
fun ViewChipOptionPreview_Selected() {
    JetSpendingTheme {
        ViewChipOption(
            text = "Last 7 days",
            isSelected = true,
            onClick = {}
        )
    }
}