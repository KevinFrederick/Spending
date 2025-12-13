package com.kevinfreyap.jetspending.utils

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

@Composable
fun rememberShimmerBrush(): Brush {
    val isDark = isSystemInDarkTheme()

    val shimmerColors = if (isDark) {
        listOf(
            Color.Transparent,
            Color.White.copy(alpha = 0.1f), // Subtle shine for Dark Mode
            Color.Transparent,
        )
    } else {
        listOf(
            Color.Transparent,
            Color.White.copy(alpha = 0.5f), // Bright shine for Light Mode
            Color.Transparent,
        )
    }

    val transition = rememberInfiniteTransition(label = "shimmer")
    val translateAnimation = transition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(800), // Speed of shimmer
            repeatMode = RepeatMode.Reverse
        ), label = "shimmer"
    )

    return Brush.linearGradient(
        colors = shimmerColors,
        start = Offset.Zero,
        end = Offset(x = translateAnimation.value, y = translateAnimation.value)
    )
}