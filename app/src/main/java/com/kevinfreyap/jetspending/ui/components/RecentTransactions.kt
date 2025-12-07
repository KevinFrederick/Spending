package com.kevinfreyap.jetspending.ui.components

import android.content.res.Configuration
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.kevinfreyap.jetspending.R
import com.kevinfreyap.jetspending.ui.theme.Green500
import com.kevinfreyap.jetspending.ui.theme.Grey600
import com.kevinfreyap.jetspending.ui.theme.JetSpendingTheme
import com.kevinfreyap.jetspending.ui.theme.Theme

@Composable
fun RecentTransactions(
    transactions: List<Int>,
    modifier: Modifier = Modifier
){
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Theme.custom.cardColor,
        ),
        modifier = modifier
            .fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .padding(12.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                Text(
                    text = stringResource(R.string.recent_transaction),
                    color = Theme.custom.textColor,
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier
                        .weight(3f)
                )

                Text(
                    text = stringResource(R.string.see_all),
                    style = MaterialTheme.typography.titleSmall,
                    color = Grey600,
                    textAlign = TextAlign.End,
                    modifier = Modifier
                        .weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Column (
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier
                    .animateContentSize()
            ) {
                transactions.forEach { transaction ->
                    // Key -> transaction.id
                    key(transaction) {
                        ViewTransactionItem(
                            transactionName = "Salary",
                            transactionDate = "24 Oct 2025",
                            transactionAmount = "+ Rp 1.000.000",
                            transactionIcon = R.drawable.ic_salary_icon,
                            transactionTypeColor = Green500,
                        )
                    }
                }
            }
        }
    }
}

@Preview(
    showBackground = true,
    device = Devices.PIXEL_9_PRO,
    uiMode = Configuration.UI_MODE_NIGHT_NO
)
@Composable
fun RecentTransactionsPreview() {
    JetSpendingTheme {
        RecentTransactions(
            transactions = listOf(1,2,3)
        )
    }
}

@Preview(
    showBackground = true,
    device = Devices.PIXEL_9_PRO,
    uiMode = Configuration.UI_MODE_NIGHT_YES
)
@Composable
fun RecentTransactionsDarkPreview() {
    JetSpendingTheme {
        RecentTransactions(
            transactions = listOf(1,2,3)
        )
    }
}