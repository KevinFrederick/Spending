package com.kevinfreyap.jetspending.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.kevinfreyap.jetspending.R
import com.kevinfreyap.jetspending.ui.model.SpendingIncomeBalanceUi
import com.kevinfreyap.jetspending.ui.theme.Green500
import com.kevinfreyap.jetspending.ui.theme.JetSpendingTheme
import com.kevinfreyap.jetspending.ui.theme.Orange700

@Composable
fun IncomeSpendingRow (
    spendingIncomeStats: SpendingIncomeBalanceUi,
    modifier: Modifier = Modifier,
    isLoading: Boolean = false
) {
    Row (
        horizontalArrangement = Arrangement.SpaceAround,
        modifier = modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min)
    ) {
        // Income
        ViewStatCard(
            cardLabel = stringResource(R.string.income),
            amount = spendingIncomeStats.income,
            color = Green500,
            isLoading = isLoading,
            modifier = Modifier
                .weight(1f)
        )

        Spacer(
            modifier = Modifier
                .width(16.dp)
        )

        // Spending
        ViewStatCard(
            cardLabel = stringResource(R.string.spending),
            amount = spendingIncomeStats.spending,
            color = Orange700,
            isLoading = isLoading,
            modifier = Modifier
                .weight(1f)
        )
    }
}

@Preview(
    showBackground = true,
    device = Devices.PIXEL_9_PRO
)
@Composable
fun IncomeSpendingRowPreview() {
    JetSpendingTheme {
        IncomeSpendingRow(
            spendingIncomeStats = SpendingIncomeBalanceUi(
                income = "Rp 1.000.000.000",
                spending = "Rp 1.000.000"
            )
        )
    }
}