package com.kevinfreyap.jetspending.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.kevinfreyap.domain.model.TransactionType
import com.kevinfreyap.jetspending.R
import com.kevinfreyap.jetspending.ui.theme.Theme

@Composable
fun ViewTransactionTypeSelector(
    selectedOption: TransactionType,
    onSelectOption: (TransactionType) -> Unit,
    modifier: Modifier = Modifier
) {
    Card (
        shape = RoundedCornerShape(50),
        colors = CardDefaults.cardColors(
            containerColor = Theme.custom.nestedCardColor
        ),
        modifier = modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min)
    ) {
        Row (
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxWidth()
        ) {

            TransactionType.entries.forEachIndexed { index, option ->
                val selected = option == selectedOption
                val optionDisplayRes = when (option) {
                    TransactionType.INCOME -> R.string.income
                    TransactionType.SPENDING -> R.string.spending
                }

                Text(
                    text = stringResource(optionDisplayRes),
                    fontWeight = FontWeight.SemiBold,
                    style = MaterialTheme.typography.titleMedium,
                    textAlign = TextAlign.Center,
                    color = if (selected) MaterialTheme.colorScheme.onPrimary else Theme.custom.textColor,
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .clip(
                            RoundedCornerShape(50)
                        )
                        .clickable(
                            onClick = {
                                onSelectOption(option)
                            }
                        )
                        .then(
                            if (selected) {
                                Modifier
                                    .background(MaterialTheme.colorScheme.primary, RoundedCornerShape(50))
                            } else {
                                Modifier
                            }
                        )
                        .padding(
                            vertical = 8.dp
                        )
                )
            }
        }
    }
}