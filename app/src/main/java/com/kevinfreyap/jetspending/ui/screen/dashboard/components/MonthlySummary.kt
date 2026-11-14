package com.kevinfreyap.jetspending.ui.screen.dashboard.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.kevinfreyap.domain.model.TransactionType
import com.kevinfreyap.jetspending.ui.state.MonthlySummaryState
import com.kevinfreyap.jetspending.ui.state.SummaryState
import com.kevinfreyap.jetspending.ui.theme.JetSpendingTheme

@Composable
fun MonthlySummary(
    state: MonthlySummaryState,
    modifier: Modifier = Modifier
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        modifier = modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min)
    ) {
        SummaryCard(
            state = state.incomeSummary,
            modifier = Modifier
                .weight(1f)
        )

        SummaryCard(
            state = state.spendingSummary,
            modifier = Modifier
                .weight(1f)
        )
    }
}

@Composable
fun MonthlySummaryPlaceholder() {
    JetSpendingTheme {
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(IntrinsicSize.Min)
        ) {
            SummaryCardPlaceholder(
                modifier = Modifier
                    .weight(1f)
            )
            SummaryCardPlaceholder(
                modifier = Modifier
                    .weight(1f)
            )
        }
    }
}

@Preview
@Composable
fun MonthlySummaryPreview() {
    JetSpendingTheme {
        MonthlySummary(
            state = MonthlySummaryState(
                incomeSummary = SummaryState(
                    transactionType = TransactionType.INCOME,
                    value = "1,800.00",
                    currency = "USD",
                    month = "This Month"
                ),
                spendingSummary = SummaryState(
                    transactionType = TransactionType.SPENDING,
                    value = "900.00",
                    currency = "USD",
                    month = "This Month"
                )
            )
        )
    }
}

@Preview
@Composable
fun MonthlySummaryPlaceholderPreview() {
    JetSpendingTheme {
        MonthlySummaryPlaceholder()
    }
}