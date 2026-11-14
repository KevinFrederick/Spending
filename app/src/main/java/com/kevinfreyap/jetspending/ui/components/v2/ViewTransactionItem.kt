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
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.text.style.TextOverflow
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
    val iconColor: Color
    val iconBackground: Color
    val formattedValue: String
    val amountColor: Color

    when(state.transactionType) {
        TransactionType.INCOME -> {
            iconColor = MaterialTheme.colorScheme.primary
            iconBackground = Theme.custom.highlightContainer
            formattedValue = "+ ${state.transactionAmount}"
            amountColor = Theme.custom.statusPositive
        }
        TransactionType.SPENDING -> {
            iconColor = Theme.custom.secondaryTextColor
            iconBackground = MaterialTheme.colorScheme.surfaceContainer
            formattedValue = "- ${state.transactionAmount}"
            amountColor = Theme.custom.statusNegative
        }
    }

    Row(
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .clickable{
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
                    iconBackground
                )
        ) {
            Icon(
                painter = state.transactionCategoryIcon,
                contentDescription = null,
                tint = iconColor,
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
                text = state.transactionDesc,
                style = MaterialTheme.typography.bodySmall,
                color = Theme.custom.secondaryTextColor,
                maxLines = 1,
                softWrap = false,
                overflow = TextOverflow.Ellipsis
            )
        }

        Column(
            verticalArrangement = Arrangement.spacedBy(4.dp),
            horizontalAlignment = Alignment.End
        ) {
            Text(
                text = formattedValue,
                style = MaterialTheme.typography.titleSmall,
                color = amountColor,
                maxLines = 1,
                softWrap = false,
                overflow = TextOverflow.Ellipsis
            )

            Text(
                text = state.transactionCurrency,
                style = MaterialTheme.typography.bodySmall,
                color = Theme.custom.secondaryTextColor,
                maxLines = 1,
                softWrap = false,
                overflow = TextOverflow.Ellipsis
            )
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
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(40.dp)
                .clip(
                    shape = RoundedCornerShape(12.dp)
                )
                .shimmerEffect(
                    shimmerColor = Theme.custom.shimmerColor
                )
        ) {}

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
                transactionCategoryIcon = painterResource(R.drawable.ic_food_icon),
                transactionName = "Lunch",
                transactionDesc = "Food & Drink · 18 Sep 2026",
                transactionAmount = "70.00",
                transactionCurrency = "USD"
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