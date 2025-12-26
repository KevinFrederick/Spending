package com.kevinfreyap.jetspending.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.kevinfreyap.jetspending.R
import com.kevinfreyap.jetspending.ui.model.SpendingIncomeBalanceUi
import com.kevinfreyap.jetspending.ui.theme.JetSpendingTheme
import com.kevinfreyap.jetspending.ui.theme.Red500
import com.kevinfreyap.jetspending.ui.theme.Theme
import com.kevinfreyap.jetspending.utils.rememberShimmerBrush

@Composable
fun SummaryInformation (
    totalBalance: String,
    monthlyBalanceUi: SpendingIncomeBalanceUi,
    dateSelectorSlot: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    isIncomplete: Boolean = false,
    isLoading: Boolean = false
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .fillMaxWidth()
    ) {
        Card (
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = Theme.custom.cardColor
            )
        ) {
            Box {
                if (isIncomplete) {
                    Text(
                        text = stringResource(R.string.error_some_transaction_rate_unavailable),
                        style = MaterialTheme.typography.labelMedium,
                        color = Red500,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .align(Alignment.TopCenter)
                            .offset(
                                y = 12.dp
                            )
                    )
                }

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            vertical = 32.dp,
                            horizontal = 16.dp
                        )
                ) {

                    Text(
                        text = stringResource(R.string.total_balance),
                        style = MaterialTheme.typography.headlineSmall,
                        color = Theme.custom.textColor,
                    )

                    Spacer(
                        modifier = Modifier
                            .height(4.dp)
                    )

                    Text(
                        text = totalBalance,
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.displaySmall,
                        color = Theme.custom.textColor,
                        textAlign = TextAlign.Center,
                        maxLines = 1,
                        softWrap = false,
                        modifier = Modifier
                            .horizontalScroll(rememberScrollState())
                            .then(
                                if (isLoading) {
                                    Modifier
                                        .width(200.dp)
                                        .background(
                                            Theme.custom.nestedCardColor,
                                            RoundedCornerShape(8.dp)
                                        )
                                        .background(
                                            rememberShimmerBrush(),
                                            RoundedCornerShape(8.dp)
                                        )
                                } else Modifier
                            )
                    )
                }
            }
        }

        Spacer(
            modifier = Modifier
                .height(16.dp)
        )

        dateSelectorSlot()

        Spacer(
            modifier = Modifier
                .height(16.dp)
        )

        IncomeSpendingRow(
            spendingIncomeStats = monthlyBalanceUi,
            isLoading = isLoading
        )
    }


}

@Preview(
    showBackground = true,
    device = Devices.PIXEL_9_PRO
)
@Composable
fun SummaryInformationPreview() {
    JetSpendingTheme {
        SummaryInformation(
            totalBalance = "Rp 1.000.000",
            monthlyBalanceUi = SpendingIncomeBalanceUi(
                income = "Rp 1.000.000.000",
                spending = "Rp 2.000.000",
                ratesIncomplete = false
            ),
            isIncomplete = true,
            isLoading = true,
            dateSelectorSlot = {
                Card (
                    shape = RoundedCornerShape(50),
                    colors = CardDefaults.cardColors(
                        containerColor = Theme.custom.cardColor
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(36.dp)
                ) {
                    ViewDateSelector(
                        centerText = "Dec 2025",
                        onPreviousClick = {  },
                        onPreviousBtnEnabled = true,
                        onNextClick = {  },
                        onNextBtnEnabled = true,
                        isLoading = true,
                        modifier = Modifier
                            .fillMaxHeight()
                    )
                }
            }
        )
    }
}