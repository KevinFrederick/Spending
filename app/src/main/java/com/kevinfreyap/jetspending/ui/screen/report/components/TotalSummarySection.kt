package com.kevinfreyap.jetspending.ui.screen.report.components

import androidx.annotation.StringRes
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.TextAutoSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kevinfreyap.domain.model.AppCurrency
import com.kevinfreyap.domain.model.TransactionType
import com.kevinfreyap.jetspending.R
import com.kevinfreyap.jetspending.ui.components.v2.ViewSegmentedPill
import com.kevinfreyap.jetspending.ui.helper.filter.toUiNameRes
import com.kevinfreyap.jetspending.ui.screen.report.state.ReportTotalSummaryState
import com.kevinfreyap.jetspending.ui.theme.JetSpendingTheme
import com.kevinfreyap.jetspending.ui.theme.Theme
import com.kevinfreyap.jetspending.utils.shimmerEffect

@Composable
fun TotalSummarySection(
    state: ReportTotalSummaryState,
    selectedType: TransactionType,
    onSelectType: (TransactionType) -> Unit,
    modifier: Modifier = Modifier
) {
    @StringRes val heroLabel: Int
    @StringRes val subLabel: Int
    val heroAmount: String
    val subAmount: String

    when(selectedType) {
        TransactionType.SPENDING -> {
            heroLabel = R.string.label_total_expense
            subLabel = R.string.label_income
            heroAmount = state.totalSpending
            subAmount = state.totalIncome
        }
        TransactionType.INCOME -> {
            heroLabel = R.string.label_total_income
            subLabel = R.string.label_expense
            heroAmount = state.totalIncome
            subAmount = state.totalSpending
        }
    }

    val netColor = when {
        state.isNetPositive -> Theme.custom.statusPositive
        state.isNetNegative -> Theme.custom.statusNegative
        else -> Theme.custom.primaryTextColor
    }

    val showSubAmount = when (selectedType) {
        TransactionType.SPENDING -> state.hasSpending
        TransactionType.INCOME -> state.hasIncome
    }

    Column (
        modifier = modifier
            .fillMaxWidth()
    ) {
        Column {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                Text(
                    text = stringResource(heroLabel),
                    style = MaterialTheme.typography.bodyMedium,
                    color = Theme.custom.secondaryTextColor,
                    modifier = Modifier
                        .align(Alignment.Bottom)
                )

                ViewSegmentedPill(
                    options = TransactionType.entries,
                    selectedOption = selectedType,
                    optionLabel = { stringResource(it.toUiNameRes()) },
                    onSelectOption = onSelectType,
                    modifier = Modifier
                        .padding(
                            bottom = 12.dp
                        )
                )
            }

            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                Text(
                    text = heroAmount,
                    style = MaterialTheme.typography.displaySmall,
                    color = Theme.custom.primaryTextColor,
                    maxLines = 1,
                    softWrap = false,
                    overflow = TextOverflow.Ellipsis,
                    autoSize = TextAutoSize.StepBased(
                        minFontSize = 20.sp,
                        maxFontSize = MaterialTheme.typography.displaySmall.fontSize
                    ),
                    modifier = Modifier
                        .weight(1f, fill = false)
                        .alignByBaseline()
                )

                Text(
                    text = state.selectedCurrency.name,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Theme.custom.secondaryTextColor,
                    maxLines = 1,
                    softWrap = false,
                    modifier = Modifier.alignByBaseline()
                )
            }

            AnimatedVisibility(
                visible = showSubAmount,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                Spacer(Modifier.height(4.dp))

                FlowRow (
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(2.dp)
                ) {

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                    ) {
                        Text(
                            text = stringResource(subLabel),
                            style = MaterialTheme.typography.labelMedium,
                            color = Theme.custom.secondaryTextColor,
                            modifier = Modifier
                                .alignByBaseline()
                        )

                        Text(
                            text = buildAnnotatedString {
                                append(subAmount)
                                append(" ")
                                withStyle(style = SpanStyle(
                                    fontSize = MaterialTheme.typography.labelSmall.fontSize,
                                    color = Theme.custom.secondaryTextColor,
                                )) {
                                    append(state.selectedCurrency.name)
                                }
                            },
                            style = MaterialTheme.typography.bodyMedium,
                            color = Theme.custom.primaryTextColor,
                            modifier = Modifier
                                .alignByBaseline()
                        )
                    }

                    Text(
                        text = "•",
                        style = MaterialTheme.typography.labelMedium,
                        color = Theme.custom.secondaryTextColor,
                        modifier = Modifier
                            .align(Alignment.CenterVertically)
                    )

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                    ) {
                        Text(
                            text = stringResource(R.string.label_net),
                            style = MaterialTheme.typography.labelMedium,
                            color = Theme.custom.secondaryTextColor,
                            modifier = Modifier
                                .alignByBaseline()
                        )

                        Text(
                            text = buildAnnotatedString {
                                append(state.totalNet)
                                append(" ")
                                withStyle(style = SpanStyle(
                                    fontSize = MaterialTheme.typography.labelSmall.fontSize,
                                    color = Theme.custom.secondaryTextColor,
                                )) {
                                    append(state.selectedCurrency.name)
                                }
                            },
                            style = MaterialTheme.typography.bodyMedium,
                            color = netColor,
                            modifier = Modifier
                                .alignByBaseline()
                        )
                    }
                }
            }

        }
    }
}

@Composable
fun TotalSummarySectionPlaceholder() {
    Column (
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Column (
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                Text(
                    text = "",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Transparent,
                    modifier = Modifier
                        .align(Alignment.Bottom)
                        .clearAndSetSemantics {}
                        .fillMaxWidth(0.3f)
                        .clip(RoundedCornerShape(50))
                        .shimmerEffect(
                            shimmerColor = Theme.custom.shimmerColor
                        )
                )

                ViewSegmentedPill(
                    options = TransactionType.entries,
                    selectedOption = TransactionType.SPENDING,
                    optionLabel = { stringResource(it.toUiNameRes()) },
                    onSelectOption = {},
                    modifier = Modifier
                        .padding(
                            bottom = 12.dp
                        )
                )
            }

            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                Text(
                    text = "",
                    style = MaterialTheme.typography.displaySmall,
                    color = Color.Transparent,
                    maxLines = 1,
                    softWrap = false,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier
                        .weight(1f, fill = false)
                        .alignByBaseline()
                        .clearAndSetSemantics {}
                        .fillMaxWidth(0.6f)
                        .clip(RoundedCornerShape(50))
                        .shimmerEffect(
                            shimmerColor = Theme.custom.shimmerColor
                        )
                )

                Text(
                    text = "",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Transparent,
                    maxLines = 1,
                    softWrap = false,
                    modifier = Modifier
                        .alignByBaseline()
                        .clearAndSetSemantics {}
                        .fillMaxWidth(0.1f)
                        .clip(RoundedCornerShape(50))
                        .shimmerEffect(
                            shimmerColor = Theme.custom.shimmerColor
                        )
                )
            }

            Text(
                text = "",
                style = MaterialTheme.typography.labelMedium,
                color = Color.Transparent,
                modifier = Modifier
                    .clearAndSetSemantics {}
                    .fillMaxWidth(0.7f)
                    .clip(RoundedCornerShape(50))
                    .shimmerEffect(
                        shimmerColor = Theme.custom.shimmerColor
                    )
            )
        }
    }
}

@Preview (
    showBackground = true
)
@Composable
fun TotalSummarySectionPreview() {
    JetSpendingTheme {
        TotalSummarySection(
            state = ReportTotalSummaryState(
                totalNet = "–90.000.000.000.000",
                totalIncome = "100.000.000.000.000.000",
                totalSpending = "100.000.000",
                selectedCurrency = AppCurrency.IDR,
                hasSpending = true
            ),
            selectedType = TransactionType.SPENDING,
            onSelectType = {},
        )
    }
}

@Preview(
    showBackground = true
)
@Composable
fun TotalSummarySectionPlaceholderPreview() {
    JetSpendingTheme {
        TotalSummarySectionPlaceholder()
    }
}