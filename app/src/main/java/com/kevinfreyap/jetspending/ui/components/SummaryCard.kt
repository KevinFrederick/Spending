package com.kevinfreyap.jetspending.ui.components

import android.content.res.Configuration
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
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
import com.kevinfreyap.jetspending.ui.model.MonthlyBalanceUi
import com.kevinfreyap.jetspending.ui.theme.Green500
import com.kevinfreyap.jetspending.ui.theme.Grey400
import com.kevinfreyap.jetspending.ui.theme.JetSpendingTheme
import com.kevinfreyap.jetspending.ui.theme.Orange700
import com.kevinfreyap.jetspending.ui.theme.Red500
import com.kevinfreyap.jetspending.ui.theme.Theme

@Composable
fun SummaryCard(
    totalBalance: String,
    monthlyBalanceUi: MonthlyBalanceUi,
    dateSelectorSlot: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    isIncomplete: Boolean? = null,
) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = Theme.custom.cardColor
        ),
        shape = RoundedCornerShape(16.dp),
        modifier = modifier.fillMaxWidth()
    ){
        Column(
            modifier = Modifier
                .padding(12.dp)
        ) {
            Text(
                color = Theme.custom.textColor,
                text = stringResource(R.string.balance_info),
                style = MaterialTheme.typography.titleLarge
            )

            if (isIncomplete == true){
                Spacer(modifier = Modifier.height(8.dp))
            } else {
                Spacer(modifier = Modifier.height(24.dp))
            }

            if (isIncomplete == true) {
                Text(
                    text = stringResource(R.string.error_some_transaction_rate_unavailable),
                    style = MaterialTheme.typography.labelMedium,
                    color = Red500,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 4.dp)
                )
            }

            Text(
                text = stringResource(R.string.total_balance),
                color = Theme.custom.textColor,
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.titleSmall,
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.CenterHorizontally)
            )
            Text(
                text = totalBalance,
                color = Theme.custom.textColor,
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        top = 4.dp
                    )
            )

            Spacer(modifier = Modifier.height(16.dp))

            HorizontalDivider(
                thickness = 1.dp,
                color = Grey400
            )

            Spacer(modifier = Modifier.height(8.dp))

            dateSelectorSlot()
            
            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(IntrinsicSize.Min)
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .weight(1f)
                ) {
                    Text(
                        text = stringResource(R.string.income),
                        color = Theme.custom.textColor,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = monthlyBalanceUi.monthlyIncome,
                        fontWeight = FontWeight.SemiBold,
                        color = Green500
                    )
                }

                VerticalDivider(
                    thickness = 1.dp,
                    color = Grey400
                )

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .weight(1f)
                ) {
                    Text(
                        text = stringResource(R.string.spending),
                        color = Theme.custom.textColor,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = monthlyBalanceUi.monthlySpending,
                        fontWeight = FontWeight.SemiBold,
                        color = Orange700
                    )
                }
            }
        }
    }
}

@Preview (
    name = "Light Mode",
    showBackground = true,
    device = Devices.PIXEL_9_PRO,
    uiMode = Configuration.UI_MODE_NIGHT_NO
)
@Composable
fun SummaryCardLightPreview() {
    JetSpendingTheme {
        SummaryCard(
            totalBalance = "Rp 100.000",
            dateSelectorSlot = {
                ViewDateSelector(
                    centerText = "December",
                    onPreviousClick = {  },
                    onPreviousBtnEnabled = true,
                    onNextClick = {  },
                    onNextBtnEnabled = true,
                )
            },
            isIncomplete = true,
            monthlyBalanceUi = MonthlyBalanceUi()
        )
    }
}

@Preview (
    name = "Dark Mode",
    showBackground = true,
    device = Devices.PIXEL_9_PRO,
    uiMode = Configuration.UI_MODE_NIGHT_YES
)
@Composable
fun SummaryCardDarkPreview() {
    JetSpendingTheme {
        SummaryCard(
            totalBalance = "Rp 100.000",
            dateSelectorSlot = {
                ViewDateSelector(
                    centerText = "December",
                    onPreviousClick = {  },
                    onPreviousBtnEnabled = true,
                    onNextClick = {  },
                    onNextBtnEnabled = true,
                )
            },
            monthlyBalanceUi = MonthlyBalanceUi(
                monthlyIncome = "Rp 100.000",
                monthlySpending = "Rp 120.000"
            )
        )
    }
}