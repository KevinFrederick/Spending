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
import com.kevinfreyap.jetspending.ui.model.TransactionItemUi
import com.kevinfreyap.jetspending.ui.theme.Green500
import com.kevinfreyap.jetspending.ui.theme.Grey600
import com.kevinfreyap.jetspending.ui.theme.JetSpendingTheme
import com.kevinfreyap.jetspending.ui.theme.Theme
import java.time.Instant

@Composable
fun RecentTransactions(
    transactions: List<TransactionItemUi>,
    navigateToTransactionList: () -> Unit,
    navigateToDetail: (String) -> Unit,
    onCheckRate: (Instant) -> Unit,
    modifier: Modifier = Modifier,
    isLoading: Boolean = false
){
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Theme.custom.cardColor,
        ),
        onClick = navigateToTransactionList,
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
                if (isLoading) {
                    repeat(3) {
                        ViewTransactionItemPlaceholder(isNestedCard = true)
                    }
                } else {
                    transactions.forEach { transaction ->
                        // Key -> transaction.id
                        key(transaction.transactionId) {
                            ViewTransactionItem(
                                transaction = transaction,
                                navigateToDetail = {
                                    navigateToDetail(transaction.transactionId)
                                },
                                onCheckRate = onCheckRate
                            )
                        }
                    }

                    if (transactions.isEmpty()) {
                        Text(
                            text = stringResource(R.string.error_no_transaction),
                            style = MaterialTheme.typography.headlineSmall,
                            textAlign = TextAlign.Center,
                            color = Theme.custom.textColor,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(
                                    top = 8.dp,
                                    start = 16.dp,
                                    bottom = 16.dp,
                                    end = 16.dp
                                )
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
            isLoading = false,
            transactions = listOf(
                TransactionItemUi(
                    transactionId = "1",
                    transactionName = "Salary",
                    transactionAmount = "+ Rp 1.000.000",
                    transactionDate = "24 November 2025",
                    transactionDateRaw = Instant.now(),
                    transactionTypeBackground = Green500,
                    transactionCategoryIcon = R.drawable.ic_salary_icon,
                    isConversionPending = false
                ),
                TransactionItemUi(
                    transactionId = "2",
                    transactionName = "Salary",
                    transactionAmount = "+ Rp 1.000.000",
                    transactionDate = "24 November 2025",
                    transactionDateRaw = Instant.now(),
                    transactionTypeBackground = Green500,
                    transactionCategoryIcon = R.drawable.ic_salary_icon,
                    isConversionPending = false
                ),
                TransactionItemUi(
                    transactionId = "3",
                    transactionName = "Salary",
                    transactionAmount = "+ Rp 1.000.000",
                    transactionDate = "24 November 2025",
                    transactionDateRaw = Instant.now(),
                    transactionTypeBackground = Green500,
                    transactionCategoryIcon = R.drawable.ic_salary_icon,
                    isConversionPending = false
                )
            ),
            navigateToDetail = {},
            navigateToTransactionList = {},
            onCheckRate = {}
        )
    }
}