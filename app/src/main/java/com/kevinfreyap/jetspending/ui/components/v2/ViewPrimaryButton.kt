package com.kevinfreyap.jetspending.ui.components.v2

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.kevinfreyap.jetspending.R
import com.kevinfreyap.jetspending.ui.theme.JetSpendingTheme
import com.kevinfreyap.jetspending.utils.debouncedClick

@Composable
fun ViewPrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    textAlign: TextAlign = TextAlign.Center,
    icon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    containerColor: Color = MaterialTheme.colorScheme.primary,
    contentColor: Color = MaterialTheme.colorScheme.onPrimary
) {
    val debouncedClick = debouncedClick(onClick = onClick)

    Button(
        onClick = debouncedClick,
        enabled = enabled,
        shape = RoundedCornerShape(100),
        colors = ButtonDefaults.buttonColors(
            containerColor = containerColor,
            contentColor = contentColor
        ),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
        modifier = modifier
    ) {
        if (icon != null) {
            icon()
            Spacer(modifier = Modifier.width(8.dp))
        }
        Text(
            text = text,
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.Medium,
            textAlign = textAlign,
            modifier = if (textAlign == TextAlign.Center) Modifier else Modifier.weight(1f)
        )

        if (trailingIcon != null) {
            trailingIcon()
        }
    }
}

@Preview
@Composable
fun ViewPrimaryButtonPreview() {
    JetSpendingTheme {
        ViewPrimaryButton(
            text = "Get started",
            onClick = {},
            trailingIcon = {
                Icon(
                    painterResource(R.drawable.ic_arrow_forward_24),
                    contentDescription = null,
                    modifier = Modifier
                        .padding(start = 8.dp)
                )
            },
            modifier = Modifier
                .fillMaxWidth()
        )
    }
}