package com.kevinfreyap.jetspending.ui.screen.detail.components

import android.content.res.Configuration.UI_MODE_NIGHT_NO
import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.kevinfreyap.domain.model.AppCurrency
import com.kevinfreyap.domain.model.TransactionType
import com.kevinfreyap.jetspending.R
import com.kevinfreyap.jetspending.ui.action.TransactionDetailAction
import com.kevinfreyap.jetspending.ui.model.CategoryUI
import com.kevinfreyap.jetspending.ui.screen.detail.state.TransactionDetailSummaryState
import com.kevinfreyap.jetspending.ui.state.TransactionDetailState
import com.kevinfreyap.jetspending.ui.theme.JetSpendingTheme
import com.kevinfreyap.jetspending.ui.theme.Theme
import com.kevinfreyap.jetspending.utils.shimmerEffect

@Composable
fun TransactionDetailsSection(
    transaction: TransactionDetailState,
    onAction: (TransactionDetailAction) -> Unit,
    modifier: Modifier = Modifier
) {
    val exchangeRateLabel = stringResource(R.string.label_exchange_rate)

    val rows = buildList {
        add(stringResource(R.string.label_category) to stringResource(transaction.category.name))
        add(stringResource(R.string.label_date) to transaction.dateText)

        if (transaction.isCurrencyDifferent) {
            add(stringResource(R.string.label_original_currency) to transaction.originalCurrency.displayCodeName)
            add(stringResource(R.string.label_converted_amount) to (transaction.summary.convertedAmountWithCurrency ?: stringResource(R.string.label_rate_unavailable)))
            add(exchangeRateLabel to (transaction.exchangeRate ?: stringResource(R.string.label_rate_unavailable)))
        } else {
            add(stringResource(R.string.label_currency) to transaction.originalCurrency.displayCodeName)
        }
    }

    val hasNotes = !transaction.notes.isNullOrBlank()

    Column(
        modifier = modifier
            .fillMaxWidth()
    ) {
        rows.forEachIndexed { index, (label, value) ->
            val isExchangeRateRow = label == exchangeRateLabel && transaction.exchangeRate != null

            TransactionDetailRow(
                label = label,
                value = value,
                showDivider = index < rows.lastIndex || hasNotes,
                valueTrailingIcon = if (isExchangeRateRow) {
                    {
                        IconButton(
                            onClick = {
                                onAction(
                                    TransactionDetailAction.OnSwapExchangeRate
                                )
                            },
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.ic_swap_horizontal_24),
                                contentDescription = stringResource(R.string.cd_swap_exchange_rates),
                                tint = Theme.custom.secondaryTextColor,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                } else null
            )
        }

        if (hasNotes) {
            Spacer(Modifier.height(8.dp))

            Column(
                verticalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp)
            ) {
                Text(
                    text = stringResource(R.string.label_notes),
                    style = MaterialTheme.typography.bodySmall,
                    color = Theme.custom.secondaryTextColor
                )
                Text(
                    text = transaction.notes,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Theme.custom.primaryTextColor
                )
            }
        }
    }
}

@Composable
fun TransactionDetailSectionPlaceholder() {
    Column{
        TransactionDetailRowPlaceholder(
            labelLength = 0.3f,
            valueLength = 0.4f
        )

        TransactionDetailRowPlaceholder(
            labelLength = 0.2f,
            valueLength = 0.4f
        )

        TransactionDetailRowPlaceholder(
            labelLength = 0.3f,
            valueLength = 0.6f
        )

        TransactionDetailRowPlaceholder(
            labelLength = 0.4f,
            valueLength = 0.4f
        )

        TransactionDetailRowPlaceholder(
            labelLength = 0.3f,
            valueLength = 0.4f,
        )

        Spacer(Modifier.height(8.dp))

        Column(
            verticalArrangement = Arrangement.spacedBy(6.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp)
        ) {
            Text(
                text = "",
                style = MaterialTheme.typography.bodySmall,
                color = Color.Transparent,
                modifier = Modifier
                    .clearAndSetSemantics {}
                    .fillMaxWidth(0.15f)
                    .clip(RoundedCornerShape(50))
                    .shimmerEffect(
                        shimmerColor = Theme.custom.shimmerColor
                    )
            )
            Text(
                text = "",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Transparent,
                modifier = Modifier
                    .clearAndSetSemantics {}
                    .fillMaxWidth(1f)
                    .clip(RoundedCornerShape(50))
                    .shimmerEffect(
                        shimmerColor = Theme.custom.shimmerColor
                    )
            )

            Text(
                text = "",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Transparent,
                modifier = Modifier
                    .clearAndSetSemantics {}
                    .fillMaxWidth(0.8f)
                    .clip(RoundedCornerShape(50))
                    .shimmerEffect(
                        shimmerColor = Theme.custom.shimmerColor
                    )
            )
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
private fun TransactionDetailSectionPreview() {
    JetSpendingTheme {
        TransactionDetailsSection(
            transaction = TransactionDetailState(
                summary = TransactionDetailSummaryState(
                    categoryIconRes = R.drawable.ic_food_icon,
                    name = "Lunch",
                    type = TransactionType.SPENDING,
                    originalAmountWithCurrency = "100.000 IDR",
                    originalCurrency = AppCurrency.IDR,
                    convertedAmountWithCurrency = "5 USD",
                    convertedCurrency = AppCurrency.USD
                ),
                category = CategoryUI(
                    id = "1",
                    sortOrder = 1,
                    name = R.string.category_food,
                    iconRes = R.drawable.ic_food_icon,
                    types = listOf(TransactionType.SPENDING)
                ),
                dateText = "18 September 2026",
                originalCurrency = AppCurrency.IDR,
                convertedCurrency = AppCurrency.USD,
                exchangeRate = "1 IDR = 0.000655 USD",
                notes = null
            ),
            onAction = {}
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
private fun TransactionDetailSectionPlaceholderPreview() {
    JetSpendingTheme {
        TransactionDetailSectionPlaceholder()
    }
}