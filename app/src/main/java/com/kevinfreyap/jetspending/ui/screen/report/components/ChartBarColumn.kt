package com.kevinfreyap.jetspending.ui.screen.report.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Surface
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.unit.sp
import com.kevinfreyap.jetspending.ui.screen.report.state.ChartBarItemState
import com.kevinfreyap.jetspending.ui.theme.JetSpendingTheme
import com.kevinfreyap.jetspending.ui.theme.Theme
import com.kevinfreyap.jetspending.utils.shimmerEffect

@Composable
fun ChartBarColumn(
    bar: ChartBarItemState,
    barWidthFraction: Float,
    barShape: RoundedCornerShape,
    isDenseChart: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val animatedFraction by animateFloatAsState(
        targetValue = bar.heightFraction,
        animationSpec = tween(
            durationMillis = 350,
            easing = FastOutSlowInEasing
        ),
        label = "bar_height_${bar.id}"
    )

    val isActiveHighlight = bar.isHighlighted && bar.hasValue

    val barColor = if (isActiveHighlight) {
        MaterialTheme.colorScheme.primary
    } else {
        Theme.custom.highlightContainer
    }

    val labelColor = if (isActiveHighlight) {
        Theme.custom.primaryTextColor
    } else {
        Theme.custom.secondaryTextColor
    }

    // In 12-month Yearly view, only show the top value on the active bar to prevent overlap
    val showValueText = bar.hasValue && (!isDenseChart || isActiveHighlight)

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Bottom,
        modifier = modifier
            .semantics(mergeDescendants = true) {
                selected = isActiveHighlight
                contentDescription = "${bar.label}: ${bar.formattedAmount}"
            }
            .clickable(
                enabled = bar.hasValue,
                role = Role.Button,
                onClick = onClick
            )
    ) {
        // Top Value Label (space kept so every bar baseline aligns evenly)
        Text(
            text = if (showValueText) bar.formattedAmount else " ",
            style = if (isDenseChart) {
                MaterialTheme.typography.labelSmall.copy(fontSize = 8.sp)
            } else {
                MaterialTheme.typography.labelMedium
            },
            fontWeight = if (isActiveHighlight) FontWeight.SemiBold else FontWeight.Normal,
            color = labelColor,
            maxLines = 1,
            softWrap = false,
            overflow = TextOverflow.Visible
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Bar Track & Animated Fill
        Box(
            contentAlignment = Alignment.BottomCenter,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(barWidthFraction)
                    .fillMaxHeight(animatedFraction)
                    .heightIn(min = 4.dp)
                    .clip(barShape)
                    .background(barColor)
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = bar.label,
            style = if (isDenseChart) {
                MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp)
            } else {
                MaterialTheme.typography.labelMedium
            },
            fontWeight = if (isActiveHighlight) FontWeight.SemiBold else FontWeight.Normal,
            color = labelColor,
            maxLines = 1,
            softWrap = false
        )
    }
}

@Composable
fun ChartBarColumnPlaceholder(
    modifier: Modifier = Modifier
) {
    val barShape = remember {
        RoundedCornerShape(
            topStart = 8.dp,
            topEnd = 8.dp,
            bottomStart = 2.dp,
            bottomEnd = 2.dp
        )
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Bottom,
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
    ) {
        // Top Value Label (space kept so every bar baseline aligns evenly)
        Text(
            text = "",
            style = MaterialTheme.typography.labelMedium,
            color = Color.Transparent,
            maxLines = 1,
            softWrap = false,
            overflow = TextOverflow.Clip,
            modifier = Modifier
                .clearAndSetSemantics {}
                .fillMaxWidth(0.45f)
                .clip(RoundedCornerShape(50))
                .shimmerEffect(
                    shimmerColor = Theme.custom.shimmerColor
                )
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Bar Track & Animated Fill
        Box(
            contentAlignment = Alignment.BottomCenter,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.55f)
                    .fillMaxHeight()
                    .heightIn(min = 4.dp)
                    .clip(barShape)
                    .shimmerEffect(
                        Theme.custom.shimmerColor
                    )
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "",
            style = MaterialTheme.typography.labelMedium,
            color = Color.Transparent,
            maxLines = 1,
            softWrap = false,
            modifier = Modifier
                .clearAndSetSemantics {}
                .fillMaxWidth(0.5f)
                .clip(RoundedCornerShape(50))
                .shimmerEffect(
                    shimmerColor = Theme.custom.shimmerColor
                )
        )
    }
}

@Preview(
    showBackground = true
)
@Composable
private fun ChartBarColumnPreview() {
    val barShape = remember {
        RoundedCornerShape(
            topStart = 8.dp,
            topEnd = 8.dp,
            bottomStart = 2.dp,
            bottomEnd = 2.dp
        )
    }

    JetSpendingTheme {
        Surface(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.Bottom,
                modifier = Modifier
                    .height(164.dp)
                    .width(220.dp)
            ) {
                ChartBarColumn(
                    bar = ChartBarItemState(
                        id = "1",
                        label = "Mon",
                        heightFraction = 0.65f,
                        formattedAmount = "650K",
                        hasValue = true,
                        isHighlighted = false
                    ),
                    barWidthFraction = 0.7f,
                    barShape = barShape,
                    isDenseChart = false,
                    onClick = {},
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                )
                ChartBarColumn(
                    bar = ChartBarItemState(
                        id = "2",
                        label = "Tue",
                        heightFraction = 1.0f,
                        formattedAmount = "1M",
                        hasValue = true,
                        isHighlighted = true
                    ),
                    barWidthFraction = 0.7f,
                    barShape = barShape,
                    isDenseChart = false,
                    onClick = {},
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                )
                ChartBarColumn(
                    bar = ChartBarItemState(
                        id = "3",
                        label = "Wed",
                        heightFraction = 0.0f,
                        formattedAmount = "0",
                        hasValue = false,
                        isHighlighted = false
                    ),
                    barWidthFraction = 0.7f,
                    barShape = barShape,
                    isDenseChart = false,
                    onClick = {},
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                )
            }
        }
    }
}

@Preview(
    showBackground = true
)
@Composable
fun ChartBarColumnPlaceholderPreview() {
    JetSpendingTheme {
        ChartBarColumnPlaceholder()
    }
}