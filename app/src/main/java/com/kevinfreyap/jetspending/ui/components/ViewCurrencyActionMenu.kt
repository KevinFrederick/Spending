package com.kevinfreyap.jetspending.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.kevinfreyap.domain.model.AppCurrency
import com.kevinfreyap.jetspending.R
import com.kevinfreyap.jetspending.ui.theme.Theme

@Composable
fun ViewCurrencyActionMenu (
    currentCurrency: AppCurrency?,
    onCurrencySelected: (AppCurrency) -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }

    Box {
        IconButton(
            onClick = { expanded = true },
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_currency_exchange_24),
                tint = Theme.custom.iconColor,
                contentDescription = "Currency Exchange"
            )
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            containerColor = MaterialTheme.colorScheme.background,
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .background(Color.Transparent)
        ) {
            AppCurrency.entries.forEach { currency ->
                val selected = currency == currentCurrency

                DropdownMenuItem(
                    text = {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier
                                .fillMaxWidth()
                        ) {
                            Text(
                                text = currency.displayName,
                                color = Theme.custom.textColor,
                                fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
                                modifier = Modifier
                                    .weight(1f)
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
                        onCurrencySelected(currency)
                        expanded = false
                    },
                    modifier = Modifier
                        .padding(
                            horizontal = 16.dp
                        )
                )
            }
        }
    }
}