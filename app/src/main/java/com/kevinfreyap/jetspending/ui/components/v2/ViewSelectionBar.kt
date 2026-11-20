package com.kevinfreyap.jetspending.ui.components.v2

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.kevinfreyap.domain.model.TransactionType
import com.kevinfreyap.jetspending.R
import com.kevinfreyap.jetspending.ui.theme.JetSpendingTheme
import com.kevinfreyap.jetspending.ui.theme.Theme

@Composable
fun <T> ViewSelectionBar(
    options: List<T>,
    selectedOption: T,
    onOptionSelected: (T) -> Unit,
    optionLabel:@Composable (T) -> String,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(100),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainer
        ),
        modifier = modifier
            .fillMaxWidth()
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxWidth()
                .padding(4.dp)
        ) {
            options.forEach { option ->
                val selected = option == selectedOption
                val contentColor = if (selected) {
                    MaterialTheme.colorScheme.primary
                } else {
                    Theme.custom.secondaryTextColor
                }

                if (selected) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier
                            .weight(1f)
                            .clip(
                                shape = RoundedCornerShape(100)
                            )
                            .background(
                                color = Theme.custom.highlightContainer
                            )
                            .padding(
                                vertical = 8.dp
                            )
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.ic_check_24),
                            contentDescription = null,
                            tint = contentColor,
                            modifier = Modifier
                                .size(16.dp)
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        Text(
                            text = optionLabel(option),
                            style = MaterialTheme.typography.labelLarge,
                            color = contentColor,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                } else {
                    Text(
                        text = optionLabel(option),
                        style = MaterialTheme.typography.labelLarge,
                        color = contentColor,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .weight(1f)
                            .clip(
                                shape = RoundedCornerShape(100)
                            )
                            .clickable(
                                onClick = { onOptionSelected(option) }
                            )
                            .padding(
                                vertical = 8.dp
                            )
                    )
                }

            }
        }
    }
}

@Preview
@Composable
fun ViewSelectionBarPreview() {
    JetSpendingTheme {
        ViewSelectionBar(
            options = TransactionType.entries,
            selectedOption = TransactionType.SPENDING,
            onOptionSelected = {},
            optionLabel = {
                stringResource(
                    when(it) {
                        TransactionType.SPENDING -> R.string.expense
                        TransactionType.INCOME -> R.string.income
                    }
                )
            }
        )
    }
}