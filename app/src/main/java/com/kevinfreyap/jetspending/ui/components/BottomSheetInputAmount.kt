package com.kevinfreyap.jetspending.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.min
import com.kevinfreyap.domain.model.AppCurrency
import com.kevinfreyap.jetspending.R
import com.kevinfreyap.jetspending.ui.theme.Blue500
import com.kevinfreyap.jetspending.ui.theme.Grey500
import com.kevinfreyap.jetspending.ui.theme.JetSpendingTheme
import com.kevinfreyap.jetspending.ui.theme.Theme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BottomSheetInputAmount(
    currencyCode: AppCurrency,
    amountInputSlot: @Composable () -> Unit,
    onPositiveClick: () -> Unit,
    onNegativeClick: () -> Unit,
    onSelectCurrency: (AppCurrency) -> Unit,
    modifier: Modifier = Modifier,
    onBackButtonClick: (() -> Unit)? = null,
) {
    var expanded by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(
                vertical = 16.dp
            )
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = if (onBackButtonClick == null) 16.dp else 0.dp
                )
        ) {
            if (onBackButtonClick != null) {
                IconButton(
                    onClick = onBackButtonClick
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_arrow_back_24),
                        contentDescription = stringResource(R.string.back),
                        tint = Theme.custom.iconColor
                    )
                }

                Spacer(
                    modifier = Modifier
                        .width(8.dp)
                )
            }

            Text(
                text = stringResource(R.string.transaction_amount),
                color = Theme.custom.textColor,
                style = MaterialTheme.typography.titleLarge,
            )
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .padding(horizontal = 16.dp)
        ) {
            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded }
            ) {
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = Theme.custom.cardColor
                    ),
                    onClick = { expanded = true },
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .padding(16.dp)
                    ) {
                        Text(
                            text = currencyCode.symbol,
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier
                                .padding(end = 4.dp)
                        )

                        ExposedDropdownMenuDefaults.TrailingIcon(
                            expanded = expanded,
                            modifier = Modifier
                                .size(18.dp)
                        )
                    }
                }

                ExposedDropdownMenu (
                    expanded = expanded,
                    onDismissRequest = { expanded = false },
                    containerColor = MaterialTheme.colorScheme.background,
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .exposedDropdownSize()
                        .heightIn(max = 200.dp)
                        .background(Color.Transparent)
                ) {
                    AppCurrency.entries.forEach { option ->
                        val selected = currencyCode == option

                        DropdownMenuItem(
                            text = {
                                Row (
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                ){
                                    Text(
                                        text = option.symbol,
                                        fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal
                                    )

                                    if (selected) {
                                        Icon(
                                            painter = painterResource(R.drawable.ic_check_24),
                                            contentDescription = null,
                                            tint = Theme.custom.iconColor,
                                            modifier = Modifier
                                                .padding(start = 8.dp)
                                        )
                                    }
                                }
                            },
                            onClick = {
                                onSelectCurrency(option)
                                expanded = false
                            },
                            modifier = Modifier
                                .padding(
                                    horizontal = 4.dp
                                )
                        )
                    }
                }
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
                .padding(horizontal = 16.dp)
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
            onNegativeClick = {},
            onBackButtonClick = {},
            onSelectCurrency = {}
        )
    }
}