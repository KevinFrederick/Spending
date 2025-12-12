package com.kevinfreyap.jetspending.ui.components

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil3.compose.SubcomposeAsyncImage
import com.kevinfreyap.jetspending.ui.theme.JetSpendingTheme

@Composable
fun ViewUserProfile(
    imageUrl: String?,
    name: String,
    modifier: Modifier = Modifier,
    size: Dp = 48.dp
) {
    val imageModifier = modifier
        .size(size)
        .clip(CircleShape)

    if (imageUrl.isNullOrBlank()) {
        ViewInitialsAvatar(
            name = name,
            modifier = imageModifier,
            size = size
        )
    } else {
        SubcomposeAsyncImage(
            model = imageUrl,
            contentDescription = "Profile Picture",
            contentScale = ContentScale.Crop,
            modifier = imageModifier,
            loading = {
                ViewInitialsAvatar(
                    name,
                    modifier = Modifier
                        .fillMaxSize()
                )
            },
            error = {
                ViewInitialsAvatar(
                    name,
                    modifier = Modifier
                        .fillMaxSize()
                )
            }
        )
    }
}

@Preview(
    showBackground = true,
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
