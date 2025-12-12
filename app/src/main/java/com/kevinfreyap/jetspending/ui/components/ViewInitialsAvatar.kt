package com.kevinfreyap.jetspending.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.kevinfreyap.jetspending.ui.theme.JetSpendingTheme

@Composable
fun ViewInitialsAvatar(
    name: String,
    modifier: Modifier = Modifier,
    backgroundColor: Color = MaterialTheme.colorScheme.primary,
    textColor: Color = MaterialTheme.colorScheme.onPrimary,
    size: Dp = 48.dp
) {
    val initials = remember(name) {
        val names = name.trim().split(" ")
        when {
            names.isEmpty() -> ""
            names.size == 1 -> names[0].take(1).uppercase()
            else -> "${names[0].take(1)}${names.last().take(1)}".uppercase()
        }
    }

    val density = LocalDensity.current
    val dynamicTextSize = with(density) {
        (size.toPx() * 0.5f).toSp()
    }

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .background(backgroundColor, CircleShape)
    ) {
        Text(
            text = initials,
            fontSize = dynamicTextSize,
            fontWeight = FontWeight.Bold,
            color = textColor
        )
    }
}

@Preview(
    showBackground = true,
    device = Devices.PIXEL_9_PRO
)
@Composable
fun ViewInitialsAvatarPreview() {
    JetSpendingTheme {
        ViewInitialsAvatar(
            name = "John Doe"
        )
    }
}