package com.kevinfreyap.jetspending.ui.components.v2

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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.kevinfreyap.jetspending.R
import com.kevinfreyap.jetspending.ui.theme.JetSpendingTheme
import com.kevinfreyap.jetspending.ui.theme.Theme

@Composable
fun ViewCurrencyCard(
    topLabel: @Composable () -> Unit,
    centerValue: @Composable () -> Unit,
    bottomInformation: @Composable () -> Unit,
    containerColor: Color,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = containerColor,
        ),
        modifier = modifier
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
        ) {
            topLabel()
            centerValue()
            bottomInformation()
        }
    }
}

@Preview
@Composable
fun ViewCurrencyCardPreview_OnBoardScreen() {
    JetSpendingTheme {
        ViewCurrencyCard(
            topLabel = {
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    Text(
                        text = "Lunch in Paris",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Theme.custom.secondaryTextColor
                    )

                    Icon(
                        painter = painterResource(R.drawable.ic_food_icon),
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier
                            .size(24.dp)
                    )
                }
            },
            centerValue = {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    Text(
                        text = "18.40",
                        style = MaterialTheme.typography.displaySmall,
                        color = Theme.custom.primaryTextColor,
                        modifier = Modifier.alignByBaseline()
                    )

                    Text(
                        text = "EUR",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.alignByBaseline()
                    )
                }
            },
            bottomInformation = {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_currency_exchange_24),
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )

                    Text(
                        text = "USD 20.24",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.primary
                    )

                    Text(
                        text = "1 EUR = 1.10 USD",
                        style = MaterialTheme.typography.bodySmall,
                        color = Theme.custom.secondaryTextColor,
                    )
                }
            },
            containerColor = MaterialTheme.colorScheme.surfaceContainer
        )
    }
}

@Preview
@Composable
fun ViewCurrencyCardPreview_DashboardScreen() {
    JetSpendingTheme {
        ViewCurrencyCard(
            topLabel = {
                Text(
                    text = "Total Balance",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            },
            centerValue = {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    Text(
                        text = "3,160.00",
                        style = MaterialTheme.typography.displaySmall,
                        color = Theme.custom.primaryTextColor,
                        modifier = Modifier.alignByBaseline()
                    )

                    Text(
                        text = "USD",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.alignByBaseline()
                    )
                }
            },
            bottomInformation = {
                Text(
                    text = "All currencies, one clear balance",
                    style = MaterialTheme.typography.bodySmall,
                    color = Theme.custom.secondaryTextColor,
                )
            },
            containerColor = Theme.custom.highlightContainer
        )
    }
}