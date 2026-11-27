package com.kevinfreyap.jetspending.utils

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp

fun Modifier.shimmerEffect(
    shimmerColor: Color
): Modifier = composed {
    var size by remember { mutableStateOf(IntSize.Zero) }
    val transition = rememberInfiniteTransition(label = "shimmer_transition")

    val startOffsetX by transition.animateFloat(
        initialValue = -2 * size.width.toFloat(),
        targetValue = 2 * size.width.toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmer_offset"
    )

    val highlightColor = shimmerColor.copy(alpha = 0.5f)

    background(
        brush = Brush.linearGradient(
            colors = listOf(
                shimmerColor,
                highlightColor,
                shimmerColor
            ),
            start = Offset(startOffsetX, 0f),
            end = Offset(startOffsetX + size.width.toFloat(), size.height.toFloat())
        )
    ).onGloballyPositioned {
        size = it.size
    }
}

fun Modifier.drawScrollbar(
    scrollState: ScrollState,
    color: Color,
    width: Dp = 4.dp,
    minHeight: Dp = 24.dp
): Modifier = this.drawWithContent {
    drawContent()

    val maxValue = scrollState.maxValue
    if (maxValue > 0) {
        val viewportHeight = size.height
        val totalContentHeight = maxValue + viewportHeight

        val scrollbarHeight = ((viewportHeight / totalContentHeight) * viewportHeight)
            .coerceAtLeast(minHeight.toPx())

        val scrollbarOffset = (scrollState.value.toFloat() / maxValue.toFloat()) * (viewportHeight - scrollbarHeight)
        val widthPx = width.toPx()

        drawRoundRect(
            color = color,
            topLeft = Offset(size.width - widthPx, scrollbarOffset),
            size = Size(widthPx, scrollbarHeight),
            cornerRadius = CornerRadius(widthPx / 2, widthPx / 2)
        )
    }
}