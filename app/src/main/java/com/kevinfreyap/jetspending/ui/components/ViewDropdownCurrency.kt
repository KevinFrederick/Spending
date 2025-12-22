package com.kevinfreyap.jetspending.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
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
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.kevinfreyap.domain.model.AppCurrency
import com.kevinfreyap.jetspending.R
import com.kevinfreyap.jetspending.ui.theme.JetSpendingTheme
import com.kevinfreyap.jetspending.ui.theme.Theme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ViewDropdownCurrency(
    selectedCurrency: AppCurrency,
    onSelectCurrency: (AppCurrency) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
        modifier = modifier
    ) {
        Card (
            colors = CardDefaults.cardColors(
                containerColor = Theme.custom.nestedCardColor
            ),
            onClick = {
                expanded = true
            },
            shape = RoundedCornerShape(50),
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .padding(
                        vertical = 8.dp,
                        horizontal = 16.dp
                    )
            ) {
                Text(
                    text = selectedCurrency.displayName,
                    style = MaterialTheme.typography.bodyLarge,
                    color = Theme.custom.textColor,
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

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = {
                expanded = false
            },
            containerColor = MaterialTheme.colorScheme.background,
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .exposedDropdownSize()
                .heightIn(max = 200.dp)
                .background(Color.Transparent)
        ) {
            AppCurrency.entries.forEach { option ->
                val selected = selectedCurrency == option

                DropdownMenuItem(
                    text = {
                        Row (
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier
                                .fillMaxWidth()
                        ){
                            Text(
                                text = option.displayName,
                                fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
                                color = Theme.custom.textColor
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
}

@Preview(
    showBackground = true,
    device = Devices.PIXEL_9_PRO
)
@Composable
fun ViewDropdownCurrencyPreview() {
    JetSpendingTheme {
        ViewDropdownCurrency(
            selectedCurrency = AppCurrency.IDR,
            onSelectCurrency = {}
        )
    }
}