package com.kevinfreyap.jetspending.ui.components

import android.content.res.Configuration
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.kevinfreyap.jetspending.R
import com.kevinfreyap.jetspending.ui.theme.JetSpendingTheme
import com.kevinfreyap.jetspending.ui.theme.Theme

@Composable
fun ViewAmountCard(
    onTransactionAmountClick: () -> Unit,
    transactionAmount: String,
    modifier: Modifier = Modifier
) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = Theme.custom.cardColor
        ),
        onClick = {
            onTransactionAmountClick()
        },
        modifier = modifier
            .fillMaxWidth()
    ) {
        Column (
            modifier = Modifier
                .fillMaxWidth()
                .height(IntrinsicSize.Min)
        ) {
            Text(
                text = stringResource(R.string.amount),
                color = Theme.custom.textColor,
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
                color = Theme.custom.textColor,
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
            transactionAmount = "Rp 1.000.000"
        )
    }
}

@Preview(
    showBackground = true,
    device = Devices.PIXEL_9_PRO,
    uiMode = Configuration.UI_MODE_NIGHT_YES
)
@Composable
fun ViewAmountCardDarkPreview() {
    JetSpendingTheme {
        ViewAmountCard(
            onTransactionAmountClick = {},
            transactionAmount = "Rp 1.000.000"
        )
    }
}