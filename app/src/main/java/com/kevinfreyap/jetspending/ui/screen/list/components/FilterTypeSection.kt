package com.kevinfreyap.jetspending.ui.screen.list.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.kevinfreyap.domain.model.TransactionType
import com.kevinfreyap.jetspending.R
import com.kevinfreyap.jetspending.ui.components.v2.ViewChipOption
import com.kevinfreyap.jetspending.ui.helper.filter.toUiNameRes
import com.kevinfreyap.jetspending.ui.theme.JetSpendingTheme
import com.kevinfreyap.jetspending.ui.theme.Theme

private val TypeFilterOptions: List<TransactionType?> = listOf(null) + TransactionType.entries

@Composable
fun FilterTypeSection(
    selectedType: TransactionType?,
    onSelectType: (TransactionType?) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = modifier
            .fillMaxWidth()
    ) {
        Text(
            text = stringResource(R.string.filter_type),
            style = MaterialTheme.typography.labelMedium,
            color = Theme.custom.primaryTextColor
        )

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier
                .padding(
                    horizontal = 2.dp
                )
        ) {
            items(
                items = TypeFilterOptions,
                key = { it?.name ?: "ALL" }
            ) { option ->
                val isSelected = selectedType == option

                ViewChipOption(
                    text = stringResource(option.toUiNameRes()),
                    isSelected = isSelected,
                    onClick = {
                        onSelectType(option)
                    }
                )
            }
        }
    }
}

@Preview(
    showBackground = true
)
@Composable
fun FilterTypeSectionPreview() {
    JetSpendingTheme {
        FilterTypeSection(
            selectedType = null,
            onSelectType = {}
        )
    }
}