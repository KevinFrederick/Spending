package com.kevinfreyap.jetspending.ui.components.v2

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.kevinfreyap.jetspending.ui.theme.Theme

@Composable
fun ViewOutlinedButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    cornerRadiusPercentage: Int = 50,
    textAlign: TextAlign = TextAlign.Center,
    borderColor: Color = MaterialTheme.colorScheme.outline,
    contentColor: Color = Theme.custom.secondaryTextColor,
    icon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
) {
    OutlinedButton(
        onClick = onClick,
        enabled = enabled,
        shape = RoundedCornerShape(cornerRadiusPercentage),
        border = BorderStroke(
            width = 1.dp,
            color = borderColor
        ),
        colors = ButtonDefaults.outlinedButtonColors(
            contentColor = contentColor
        ),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
        modifier = modifier
    ) {
        if (icon != null) {
            icon()
            Spacer(Modifier.width(8.dp))
        }

        Text(
            text = text,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Medium,
            textAlign = textAlign,
            modifier = if (textAlign == TextAlign.Center) Modifier else Modifier.weight(1f)
        )

        if (trailingIcon != null) {
            trailingIcon()
        }
    }
}