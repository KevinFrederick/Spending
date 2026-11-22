package com.kevinfreyap.jetspending.ui.screen.report.components

import android.content.res.Configuration
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.kevinfreyap.domain.model.AppCurrency
import com.kevinfreyap.jetspending.R
import com.kevinfreyap.jetspending.ui.screen.report.state.ChartBarItemState
import com.kevinfreyap.jetspending.ui.screen.report.state.ChartUiState
import com.kevinfreyap.jetspending.ui.theme.JetSpendingTheme
import com.kevinfreyap.jetspending.ui.theme.Theme
import com.kevinfreyap.jetspending.utils.shimmerEffect

@Composable
fun BarChartCard(
    state: ChartUiState,
    onBarClick: (ChartBarItemState) -> Unit,
    onClearSelection: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isDenseChart = state.bars.size > 7
    val barSpacing = if (isDenseChart) 4.dp else 8.dp
    val barWidthFraction = when {
        state.bars.size <= 5 -> 0.55f
        state.bars.size <= 7 -> 0.70f
        else -> 0.70f
    }

    val barShape = remember {
        RoundedCornerShape(
            topStart = 8.dp,
            topEnd = 8.dp,
            bottomStart = 2.dp,
            bottomEnd = 2.dp
        )
    }

    Surface(
        shape = RoundedCornerShape(20.dp),
        color = MaterialTheme.colorScheme.surfaceContainer,
        modifier = modifier
            .fillMaxWidth()
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) {
                onClearSelection()
            }
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier
                .padding(16.dp)
        ) {
            // Card Header
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                Text(
                    text = "${stringResource(state.titleRes)} • ${state.selectedCurrency.name}",
                    style = MaterialTheme.typography.bodySmall,
                    color = Theme.custom.secondaryTextColor,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier
                        .weight(1f, fill = false)
                )

                Text(
                    text = stringResource(R.string.label_chart_max, state.maxAmount),
                    style = MaterialTheme.typography.bodySmall,
                    color = Theme.custom.secondaryTextColor,
                    maxLines = 1,
                    softWrap = false
                )
            }

            // Chart Body
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(164.dp)
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(barSpacing),
                    verticalAlignment = Alignment.Bottom,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(164.dp)
                ) {
                    state.bars.forEach { bar ->
                        key(bar.id) {
                            ChartBarColumn(
                                bar = bar,
                                barWidthFraction = barWidthFraction,
                                barShape = barShape,
                                isDenseChart = isDenseChart,
                                onClick = { onBarClick(bar) },
                                modifier = Modifier
                                    .weight(1f)
                                    .fillMaxHeight()
                            )
                        }
                    }
                }

                if (!state.hasAnyData) {
                    Text(
                        text = stringResource(R.string.empty_transactions),
                        style = MaterialTheme.typography.bodyMedium,
                        color = Theme.custom.secondaryTextColor,
                        modifier = Modifier
                            .align(Alignment.Center)
                    )
                }
            }
        }
    }
}

@Composable
fun BarChartCardPlaceholder() {
    Surface(
        shape = RoundedCornerShape(20.dp),
        color = MaterialTheme.colorScheme.surfaceContainer,
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier
                .padding(16.dp)
        ) {
            // Card Header
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                Text(
                    text = "",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Transparent,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier
                        .weight(1f, fill = false)
                        .clearAndSetSemantics {}
                        .fillMaxWidth(0.6f)
                        .clip(RoundedCornerShape(50))
                        .shimmerEffect(
                            shimmerColor = Theme.custom.shimmerColor
                        )
                )

                Text(
                    text = "",
                    style = MaterialTheme.typography.bodySmall,
                    color = Theme.custom.secondaryTextColor,
                    maxLines = 1,
                    softWrap = false,
                    modifier = Modifier
                        .clearAndSetSemantics {}
                        .fillMaxWidth(0.3f)
                        .clip(RoundedCornerShape(50))
                        .shimmerEffect(
                            shimmerColor = Theme.custom.shimmerColor
                        )
                )
            }

            // Chart Body
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(164.dp)
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.Bottom,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(164.dp)
                ) {
                    repeat(5) {
                        ChartBarColumnPlaceholder(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight()
                        )
                    }
                }
            }
        }
    }
}

@Preview(
    showBackground = false
)
@Preview(
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    showBackground = false
)
@Composable
fun BarChartCardMonthlyPreview() {
    JetSpendingTheme {
        var selectedId by remember { mutableStateOf<String?>("8-14") }

        val baseBars = listOf(
            ChartBarItemState(
                id = "1-7",
                label = "1–7",
                heightFraction = 0.45f,
                formattedAmount = "450K",
                hasValue = true,
                isHighlighted = false
            ),
            ChartBarItemState(
                id = "8-14",
                label = "8–14",
                heightFraction = 1.0f,
                formattedAmount = "1M",
                hasValue = true,
                isHighlighted = true
            ),
            ChartBarItemState(
                id = "15-21",
                label = "15–21",
                heightFraction = 0.62f,
                formattedAmount = "620K",
                hasValue = true,
                isHighlighted = false
            ),
            ChartBarItemState(
                id = "22-28",
                label = "22–28",
                heightFraction = 0.28f,
                formattedAmount = "280K",
                hasValue = true,
                isHighlighted = false
            ),
            ChartBarItemState(
                id = "29-30",
                label = "29–30",
                heightFraction = 0.0f,
                formattedAmount = "0",
                hasValue = false,
                isHighlighted = false
            )
        )

        val bars = baseBars.map { bar ->
            bar.copy(isHighlighted = bar.id == selectedId)
        }

        BarChartCard(
            state = ChartUiState(
                titleRes = R.string.label_chart_title_monthly_expense,
                maxAmount = "1.000.000",
                bars = bars,
                selectedCurrency = AppCurrency.IDR,
                hasAnyData = true
            ),
            onBarClick = { clicked ->
                selectedId = if (selectedId == clicked.id) null else clicked.id
            },
            onClearSelection = {
                selectedId = null
            }
        )
    }
}

@Preview(
    showBackground = false
)
@Composable
fun BarChartCardEmptyPreview() {
    JetSpendingTheme {
        val baseBars = listOf(
            ChartBarItemState(
                id = "1-7",
                label = "1–7",
                heightFraction = 0f,
                formattedAmount = "",
                hasValue = false,
                isHighlighted = false
            ),
            ChartBarItemState(
                id = "8-14",
                label = "8–14",
                heightFraction = 0f,
                formattedAmount = "",
                hasValue = false,
                isHighlighted = false
            ),
            ChartBarItemState(
                id = "15-21",
                label = "15–21",
                heightFraction = 0f,
                formattedAmount = "",
                hasValue = false,
                isHighlighted = false
            ),
            ChartBarItemState(
                id = "22-28",
                label = "22–28",
                heightFraction = 0f,
                formattedAmount = "",
                hasValue = false,
                isHighlighted = false
            ),
            ChartBarItemState(
                id = "29-30",
                label = "29–30",
                heightFraction = 0f,
                formattedAmount = "",
                hasValue = false,
                isHighlighted = false
            )
        )

        BarChartCard(
            state = ChartUiState(
                titleRes = R.string.label_chart_title_monthly_expense,
                maxAmount = "Max 0",
                hasAnyData = false,
                bars = baseBars
            ),
            onBarClick = {},
            onClearSelection = {}
        )
    }
}

@Preview(
    showBackground = false
)
@Composable
fun BarChartCardPlaceholderPreview() {
    JetSpendingTheme {
        BarChartCardPlaceholder()
    }
}