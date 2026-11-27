package com.kevinfreyap.jetspending.ui.screen.dashboard.components

import android.content.res.Configuration.UI_MODE_NIGHT_NO
import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LocalMinimumInteractiveComponentSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.kevinfreyap.domain.model.TransactionType
import com.kevinfreyap.jetspending.R
import com.kevinfreyap.jetspending.ui.components.ViewTransactionItem
import com.kevinfreyap.jetspending.ui.components.ViewTransactionItemPlaceholder
import com.kevinfreyap.jetspending.ui.state.TransactionItemState
import com.kevinfreyap.jetspending.ui.theme.JetSpendingTheme
import com.kevinfreyap.jetspending.ui.theme.Theme
import com.kevinfreyap.jetspending.utils.shimmerEffect

@Composable
fun RecentTransactionSection(
    transactions: List<TransactionItemState>,
    onTransactionClicked: (String) -> Unit,
    onSeeAllTransactionClicked: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = modifier
            .fillMaxWidth()
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Text(
                text = stringResource(R.string.section_header_recent_transaction),
                style = MaterialTheme.typography.titleMedium,
                color = Theme.custom.primaryTextColor
            )

            CompositionLocalProvider(
                LocalMinimumInteractiveComponentSize provides 0.dp
            ) {
                TextButton(
                    onClick = onSeeAllTransactionClicked,
                    contentPadding = PaddingValues(
                        horizontal = 8.dp,
                        vertical = 4.dp
                    ),
                    modifier = Modifier
                        .defaultMinSize(
                            minWidth = 1.dp,
                            minHeight = 1.dp
                        )
                ) {
                    Text(
                        text = stringResource(R.string.btn_txt_see_all),
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.primary,
                    )
                }
            }
        }

        transactions.forEach { transaction ->
            ViewTransactionItem(
                state = transaction,
                onClick = onTransactionClicked
            )
        }

        if (transactions.isEmpty()) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 40.dp)
            ) {
                Text(
                    text = stringResource(R.string.empty_transactions),
                    style = MaterialTheme.typography.bodyMedium,
                    color = Theme.custom.secondaryTextColor,
                )
            }
        }
    }
}

@Composable
fun RecentTransactionSectionPlaceholder() {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Text(
                text = "",
                style = MaterialTheme.typography.titleMedium,
                color = Color.Transparent,
                modifier = Modifier
                    .clearAndSetSemantics {}
                    .fillMaxWidth(0.5f)
                    .clip(RoundedCornerShape(50))
                    .shimmerEffect(
                        shimmerColor = Theme.custom.shimmerColor
                    )
            )

            Text(
                text = "",
                style = MaterialTheme.typography.labelLarge,
                color = Color.Transparent,
                modifier = Modifier
                    .clearAndSetSemantics {}
                    .padding(
                        horizontal = 8.dp,
                        vertical = 4.dp
                    )
                    .fillMaxWidth(0.3f)
                    .clip(RoundedCornerShape(50))
                    .shimmerEffect(
                        shimmerColor = Theme.custom.shimmerColor
                    )
            )
        }

        repeat(3) {
            ViewTransactionItemPlaceholder()
        }
    }
}

@Preview(
    showBackground = true,
    backgroundColor = 0xFFFAFCFB,
    uiMode = UI_MODE_NIGHT_NO
)
@Preview(
    showBackground = true,
    backgroundColor = 0xFF121214,
    uiMode = UI_MODE_NIGHT_YES
)
@Composable
private fun RecentTransactionSectionPreview() {
    JetSpendingTheme {
        RecentTransactionSection(
            onTransactionClicked = {},
            onSeeAllTransactionClicked = {},
            transactions = listOf(
                TransactionItemState(
                    transactionId = "1",
                    transactionType = TransactionType.SPENDING,
                    transactionCategoryIcon = R.drawable.ic_food_icon,
                    transactionName = "Lunch",
                    transactionCategoryName = R.string.category_food,
                    transactionDate = "18 Sep 2026",
                    transactionMonthYear = "September 2026",
                    transactionOriginalAmount = "70.00",
                    transactionConvertedAmount = null,
                    transactionSelectedCurrency = "USD",
                    transactionOriginalCurrency = "IDR"
                ),
                TransactionItemState(
                    transactionId = "1",
                    transactionType = TransactionType.SPENDING,
                    transactionCategoryIcon = R.drawable.ic_food_icon,
                    transactionName = "Lunch",
                    transactionCategoryName = R.string.category_food,
                    transactionDate = "18 Sep 2026",
                    transactionMonthYear = "September 2026",
                    transactionOriginalAmount = "70.00",
                    transactionConvertedAmount = null,
                    transactionSelectedCurrency = "USD",
                    transactionOriginalCurrency = "IDR"
                ),
                TransactionItemState(
                    transactionId = "1",
                    transactionType = TransactionType.SPENDING,
                    transactionCategoryIcon = R.drawable.ic_food_icon,
                    transactionName = "Lunch",
                    transactionCategoryName = R.string.category_food,
                    transactionDate = "18 Sep 2026",
                    transactionMonthYear = "September 2026",
                    transactionOriginalAmount = "70.00",
                    transactionConvertedAmount = null,
                    transactionSelectedCurrency = "USD",
                    transactionOriginalCurrency = "IDR"
                ),
            )
        )
    }
}

@Preview(
    showBackground = true,
    backgroundColor = 0xFFFAFCFB,
    uiMode = UI_MODE_NIGHT_NO
)
@Preview(
    showBackground = true,
    backgroundColor = 0xFF121214,
    uiMode = UI_MODE_NIGHT_YES
)
@Composable
private fun RecentTransactionSectionPlaceholderPreview() {
    JetSpendingTheme {
        RecentTransactionSectionPlaceholder()
    }
}