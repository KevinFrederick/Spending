package com.kevinfreyap.jetspending.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.kevinfreyap.jetspending.R
import com.kevinfreyap.jetspending.ui.theme.JetSpendingTheme
import com.kevinfreyap.jetspending.ui.theme.Theme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ViewAmountCard(
    onTransactionAmountClick: () -> Unit,
    cardTitle: String,
    transactionAmount: String,
    modifier: Modifier = Modifier,
    isError: Boolean = false,
    errorMessage: String = ""
) {
    Card(
        border = BorderStroke(
            width = 2.dp,
            color = if (isError) MaterialTheme.colorScheme.error else Color.Transparent
        ),
        colors = CardDefaults.cardColors(
            containerColor = Theme.custom.cardColor
        ),
        onClick = {
            onTransactionAmountClick()
        },
        modifier = modifier
            .fillMaxWidth()
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(IntrinsicSize.Min)
            ) {
                Text(
                    text = cardTitle,
                    color = if (isError) MaterialTheme.colorScheme.error else Theme.custom.textColor,
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier
                        .padding(
                            top = 8.dp,
                            start = 12.dp
                        )
                )

                Spacer(
                    modifier = Modifier.height(16.dp)
                )

                Text(
                    text = transactionAmount,
                    color = if (isError) MaterialTheme.colorScheme.error else Theme.custom.textColor,
                    fontWeight = FontWeight.SemiBold,
                    style = MaterialTheme.typography.headlineMedium,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                )

                Spacer(
                    modifier = Modifier.height(32.dp)
                )
            }

            if (isError) {
                ViewErrorTooltip(
                    errorMessage = errorMessage,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                )
            }
        }
    }


}

@Preview(
    showBackground = true,
    device = Devices.PIXEL_9_PRO
)
@Composable
fun ViewAmountCardPreview() {
    JetSpendingTheme {
        ViewAmountCard(
            onTransactionAmountClick = {},
            transactionAmount = "Rp 1.000.000",
            isError = false,
            cardTitle = stringResource(R.string.amount),
        )
    }
}