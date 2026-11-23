package com.kevinfreyap.jetspending.ui.components.v2

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil3.compose.SubcomposeAsyncImage
import com.kevinfreyap.jetspending.ui.theme.JetSpendingTheme
import com.kevinfreyap.jetspending.ui.theme.Theme

@Composable
fun ViewUserProfile(
    imageUrl: String?,
    name: String,
    modifier: Modifier = Modifier,
    textStyle: TextStyle = MaterialTheme.typography.titleLarge,
    size: Dp = 52.dp
) {
    val initials = remember(name) {
        val names = name.trim().split(" ")
        when {
            names.isEmpty() -> ""
            names.size == 1 -> names[0].take(1).uppercase()
            else -> "${names[0].take(1)}${names.last().take(1)}".uppercase()
        }
    }

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .size(size)
            .clip(CircleShape)
            .background(Theme.custom.highlightContainer)
    ) {
        if (!imageUrl.isNullOrBlank()) {
            SubcomposeAsyncImage(
                model = imageUrl,
                contentDescription = null,
                contentScale = ContentScale.Crop, // 1. Crops image uniformly to fill 100% of circle
                loading = {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        Text(
                            text = initials,
                            style = textStyle,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                },
                error = {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        Text(
                            text = initials,
                            style = textStyle,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                },
                modifier = Modifier.fillMaxSize() // 2. Fills circular box
            )
        } else {
            Text(
                text = initials,
                style = textStyle,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
fun ViewUserProfilePlaceholder(
    size: Dp = 52.dp
) {
    Box(
        modifier = Modifier
            .size(size)
            .clip(CircleShape)
            .background(Theme.custom.shimmerColor)
    )
}

@Preview(
    showBackground = false,
    device = Devices.PIXEL_9_PRO
)
@Composable
fun ViewUserProfilePreview() {
    JetSpendingTheme {
        ViewUserProfile(
            imageUrl = "",
            name = "John Dow",
        )
    }
}

@Preview
@Composable
fun ViewUserProfilePlaceholderPreview() {
    JetSpendingTheme {
        ViewUserProfilePlaceholder()
    }
}