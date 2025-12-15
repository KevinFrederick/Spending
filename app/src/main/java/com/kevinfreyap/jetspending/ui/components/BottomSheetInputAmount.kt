package com.kevinfreyap.jetspending.ui.components

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.kevinfreyap.domain.model.AppCurrency
import com.kevinfreyap.jetspending.R
import com.kevinfreyap.jetspending.ui.theme.Blue500
import com.kevinfreyap.jetspending.ui.theme.Grey500
import com.kevinfreyap.jetspending.ui.theme.JetSpendingTheme
import com.kevinfreyap.jetspending.ui.theme.Theme

@Composable
fun BottomSheetInputAmount(
    amountInputSlot: @Composable () -> Unit,
    onPositiveClick: () -> Unit,
    onNegativeClick: () -> Unit,
    currencyCode: AppCurrency,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .padding(16.dp)
    ) {
        Text(
            text = stringResource(R.string.transaction_amount),
            color = Theme.custom.textColor,
            style = MaterialTheme.typography.titleLarge,
        )

        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = Theme.custom.cardColor
                ),
            ) {
                Text(
                    text = currencyCode.symbol,
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier
                        .padding(16.dp)
                )
            }

            Spacer(
                modifier = Modifier
                    .width(8.dp)
            )
            amountInputSlot()
        }


        Row(
            horizontalArrangement = Arrangement.End,
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Button(
                colors = ButtonDefaults.buttonColors(
                    Grey500
                ),
                onClick = onNegativeClick
            ) {
                Text(
                    text = stringResource(R.string.cancel)
                )
            }

            Spacer(
                modifier = Modifier
                    .width(16.dp)
            )

            Button(
                colors = ButtonDefaults.buttonColors(
                    Blue500
                ),
                onClick = onPositiveClick
            ) {
                Text(
                    text = stringResource(R.string.confirm)
                )
            }
        }
    }
}

@Preview(
    showBackground = true,
    device = Devices.PIXEL_9_PRO,
)
@Composable
fun BottomSheetInputAmountPreview(){
    JetSpendingTheme {
        BottomSheetInputAmount(
            amountInputSlot = {
                ViewTextField(
                    value = "100.000",
                    onValueChange = {  },
                    label = stringResource(R.string.amount),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number,
                        imeAction = ImeAction.Done
                    ),
                )
            },
            currencyCode = AppCurrency.IDR,
            onPositiveClick = {},
            onNegativeClick = {}
        )
    }
}