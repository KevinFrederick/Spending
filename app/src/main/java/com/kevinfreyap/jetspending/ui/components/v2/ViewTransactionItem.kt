package com.kevinfreyap.jetspending.ui.components.v2

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.kevinfreyap.domain.model.TransactionType
import com.kevinfreyap.jetspending.R
import com.kevinfreyap.jetspending.ui.state.TransactionItemState
import com.kevinfreyap.jetspending.ui.theme.JetSpendingTheme
import com.kevinfreyap.jetspending.ui.theme.Theme
import com.kevinfreyap.jetspending.utils.shimmerEffect

@Composable
fun ViewTransactionItem(
    state: TransactionItemState,
    onClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val formattedValue: String
    val amountColor: Color

    val currency: String = if (state.transactionConvertedAmount != null) {
        state.transactionSelectedCurrency
    } else {
        state.transactionOriginalCurrency
    }

    when(state.transactionType) {
        TransactionType.INCOME -> {
            formattedValue = "+ ${state.transactionConvertedAmount ?: state.transactionOriginalAmount}"
            amountColor = Theme.custom.statusPositive
        }
        TransactionType.SPENDING -> {
            formattedValue = "- ${state.transactionConvertedAmount ?: state.transactionOriginalAmount}"
            amountColor = Theme.custom.statusNegative
        }
    }

    Row(
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .clickable {
                onClick(state.transactionId)
            }
            .padding(
                vertical = 4.dp
            )
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(40.dp)
                .clip(
                    shape = RoundedCornerShape(12.dp)
                )
                .background(
                    MaterialTheme.colorScheme.surfaceContainer
                )
        ) {
            Icon(
                painter = painterResource(state.transactionCategoryIcon),
                contentDescription = null,
                tint = Theme.custom.secondaryTextColor,
                modifier = Modifier
                    .size(18.dp)
            )
        }

        Column(
            verticalArrangement = Arrangement.spacedBy(4.dp),
            modifier = Modifier
                .weight(1f)
        ) {
            Text(
                text = state.transactionName,
                style = MaterialTheme.typography.titleSmall,
                color = Theme.custom.primaryTextColor,
                maxLines = 1,
                softWrap = false,
                overflow = TextOverflow.Ellipsis
            )

            Text(
                text = "${stringResource(state.transactionCategoryName)} · ${state.transactionDate}",
                style = MaterialTheme.typography.bodySmall,
                color = Theme.custom.secondaryTextColor,
                maxLines = 1,
                softWrap = false,
                overflow = TextOverflow.Ellipsis
            )
        }

        if (state.transactionSelectedCurrency == state.transactionOriginalCurrency) {
            Text(
                text = buildAnnotatedString {
                    append(formattedValue)
                    append(" ")
                    withStyle(style = SpanStyle(
                        fontSize = MaterialTheme.typography.labelSmall.fontSize,
                        fontWeight = MaterialTheme.typography.labelSmall.fontWeight
                    )) {
                        append(currency)
                    }
                },
                style = MaterialTheme.typography.titleSmall,
                color = amountColor,
                maxLines = 1,
                softWrap = false,
                overflow = TextOverflow.Ellipsis
            )
        } else {
            Column(
                verticalArrangement = Arrangement.spacedBy(4.dp),
                horizontalAlignment = Alignment.End
            ) {
                Text(
                    text = buildAnnotatedString {
                        append(formattedValue)
                        append(" ")
                        withStyle(style = SpanStyle(
                            fontSize = MaterialTheme.typography.labelSmall.fontSize,
                            fontWeight = MaterialTheme.typography.labelSmall.fontWeight
                        )) {
                            append(currency)
                        }
                    },
                    style = MaterialTheme.typography.titleSmall,
                    color = amountColor,
                    maxLines = 1,
                    softWrap = false,
                    overflow = TextOverflow.Ellipsis
                )

                Text(
                    text = if (state.transactionConvertedAmount != null) {
                        "${state.transactionOriginalAmount} ${state.transactionOriginalCurrency}"
                    } else {
                        stringResource(R.string.rate_unavailable)
                    },
                    style = MaterialTheme.typography.bodySmall,
                    color = Theme.custom.secondaryTextColor,
                    maxLines = 1,
                    softWrap = false,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }

    }
}

@Composable
fun ViewTransactionItemPlaceholder() {
    Row(
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                vertical = 4.dp
            )
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(
                    shape = RoundedCornerShape(12.dp)
                )
                .shimmerEffect(
                    shimmerColor = Theme.custom.shimmerColor
                )
        )

        Column(
            verticalArrangement = Arrangement.spacedBy(4.dp),
            modifier = Modifier
                .weight(1f)
        ) {
            Text(
                text = "",
                style = MaterialTheme.typography.titleSmall,
                color = Color.Transparent,
                maxLines = 1,
                softWrap = false,
                overflow = TextOverflow.Ellipsis,
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
                style = MaterialTheme.typography.bodySmall,
                color = Color.Transparent,
                maxLines = 1,
                softWrap = false,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .clearAndSetSemantics {}
                    .fillMaxWidth(0.8f)
                    .clip(RoundedCornerShape(50))
                    .shimmerEffect(
                        shimmerColor = Theme.custom.shimmerColor
                    )
            )
        }

        Column(
            verticalArrangement = Arrangement.spacedBy(4.dp),
            horizontalAlignment = Alignment.End
        ) {
            Text(
                text = "",
                style = MaterialTheme.typography.titleSmall,
                color = Color.Transparent,
                maxLines = 1,
                softWrap = false,
                overflow = TextOverflow.Ellipsis,
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
                style = MaterialTheme.typography.bodySmall,
                color = Color.Transparent,
                maxLines = 1,
                softWrap = false,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .clearAndSetSemantics {}
                    .fillMaxWidth(0.25f)
                    .clip(RoundedCornerShape(50))
                    .shimmerEffect(
                        shimmerColor = Theme.custom.shimmerColor
                    )
            )
        }
    }
}

@Preview(
    showBackground = true
)
@Composable
fun ViewTransactionItemPreview() {
    JetSpendingTheme {
        ViewTransactionItem(
            state = TransactionItemState(
                transactionId = "1",
                transactionType = TransactionType.SPENDING,
                transactionCategoryIcon = R.drawable.ic_food_icon,
                transactionName = "Lunch",
                transactionCategoryName = R.string.category_food,
                transactionDate = "18 Sep 2026",
                transactionMonthYear = "September 2026",
                transactionOriginalAmount = "1.260.000",
                transactionConvertedAmount = null,
                transactionSelectedCurrency = "USD",
                transactionOriginalCurrency = "IDR"
            ),
            onClick = {}
        )
    }
}

@Preview(
    showBackground = true
)
@Composable
fun ViewTransactionItemPlaceholderPreview() {
    JetSpendingTheme {
        ViewTransactionItemPlaceholder()
    }
}