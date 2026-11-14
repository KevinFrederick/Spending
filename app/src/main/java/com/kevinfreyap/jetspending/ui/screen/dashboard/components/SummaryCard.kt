package com.kevinfreyap.jetspending.ui.screen.dashboard.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.kevinfreyap.domain.model.TransactionType
import com.kevinfreyap.jetspending.R
import com.kevinfreyap.jetspending.ui.state.SummaryState
import com.kevinfreyap.jetspending.ui.theme.JetSpendingTheme
import com.kevinfreyap.jetspending.ui.theme.Theme
import com.kevinfreyap.jetspending.utils.shimmerEffect

@Composable
fun SummaryCard(
    state: SummaryState,
    modifier: Modifier = Modifier
) {
    val icon: Painter
    val typeText: String
    val formattedValue: String
    val color: Color

    when(state.transactionType) {
        TransactionType.INCOME -> {
            icon = painterResource(R.drawable.ic_income_24)
            typeText = stringResource(R.string.income)
            formattedValue = "+ ${state.value}"
            color = Theme.custom.statusPositive
        }
        TransactionType.SPENDING -> {
            icon = painterResource(R.drawable.ic_spending_24)
            typeText = stringResource(R.string.spending)
            formattedValue = "- ${state.value}"
            color = Theme.custom.statusNegative
        }
    }

    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
        ),
        modifier = modifier
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier
                .padding(16.dp)
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painter = icon,
                    contentDescription = null,
                    tint = color,
                    modifier = Modifier
                        .size(16.dp)
                )

                Text(
                    text = typeText,
                    style = MaterialTheme.typography.bodySmall,
                    color = Theme.custom.secondaryTextColor
                )
            }

            Text(
                text = formattedValue,
                style = MaterialTheme.typography.titleLarge,
                color = color,
                maxLines = 1,
                softWrap = false,
                overflow = TextOverflow.Ellipsis
            )

            Text(
                text = "${state.currency} · ${state.month}",
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
fun SummaryCardPlaceholder(
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
        ),
        modifier = modifier
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier
                .padding(16.dp)
        ) {
            Text(
                text = "",
                style = MaterialTheme.typography.bodySmall,
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
                style = MaterialTheme.typography.titleLarge,
                color = Color.Transparent,
                maxLines = 1,
                softWrap = false,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .clearAndSetSemantics {}
                    .fillMaxWidth(0.9f)
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
                    .fillMaxWidth(0.7f)
                    .clip(RoundedCornerShape(50))
                    .shimmerEffect(
                        shimmerColor = Theme.custom.shimmerColor
                    )
            )
        }
    }
}

@Preview
@Composable
fun SummaryCardPreview() {
    JetSpendingTheme {
        SummaryCard(
            state = SummaryState(
                transactionType = TransactionType.INCOME,
                value = "1,800.00",
                currency = "USD",
                month = "This Month"
            )
        )
    }
}

@Preview
@Composable
fun SummaryCardPlaceholderPreview() {
    JetSpendingTheme {
        SummaryCardPlaceholder(
            modifier = Modifier
                .fillMaxWidth()
        )
    }
}