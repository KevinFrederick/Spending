package com.kevinfreyap.jetspending.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.kevinfreyap.domain.model.TransactionType
import com.kevinfreyap.jetspending.R
import com.kevinfreyap.jetspending.ui.model.CategoryPercentageUi
import com.kevinfreyap.jetspending.ui.theme.JetSpendingTheme
import com.kevinfreyap.jetspending.ui.theme.Orange700
import com.kevinfreyap.jetspending.ui.theme.Theme

@Composable
fun CategoriesReport(
    selectedOption: TransactionType,
    categories: List<CategoryPercentageUi>,
    onSelectOption: (TransactionType) -> Unit,
    modifier: Modifier = Modifier,
    isLoading: Boolean = false
) {
    var expanded by remember { mutableStateOf(false) }

    val visibleItems = if (expanded) categories else categories.take(3)

    Card (
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Theme.custom.cardColor
        ),
        modifier = modifier
            .fillMaxWidth()
    ) {
        Column (
            modifier = Modifier
                .padding(
                    top = 16.dp,
                    start = 16.dp,
                    end = 16.dp,
                )
        ) {
            Text(
                text = stringResource(R.string.categories),
                style = MaterialTheme.typography.titleLarge,
                color = Theme.custom.textColor
            )

            Spacer(
                modifier = Modifier
                    .height(16.dp)
            )

            ViewTransactionTypeSelector(
                selectedOption = selectedOption,
                onSelectOption = onSelectOption
            )

            Spacer(
                modifier = Modifier
                    .height(16.dp)
            )

            Column (
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (isLoading) {
                    repeat(3) {
                        ViewCategoryReportItemPlaceholder()
                    }
                } else {
                    visibleItems.forEach { category ->
                        ViewCategoryReportItem(
                            categoryIcon = category.iconRes,
                            categoryName = stringResource(category.name),
                            categoryAmount = category.categoryAmount,
                            color = category.categoryColor,
                            percentage = category.percentage
                        )
                    }

                    if (categories.isEmpty()) {
                        Text(
                            text = stringResource(R.string.error_no_data),
                            style = MaterialTheme.typography.headlineSmall,
                            textAlign = TextAlign.Center,
                            color = Theme.custom.textColor,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(
                                    top = 8.dp,
                                    start = 16.dp,
                                    bottom = 16.dp,
                                    end = 16.dp
                                )
                        )
                    }
                }

            }

            if (categories.size > 3) {
                TextButton(
                    onClick = {
                        expanded = !expanded
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.CenterHorizontally)
                ) {
                    Text(
                        text = stringResource(
                            if (expanded) {
                                R.string.show_less
                            } else {
                                R.string.show_all
                            }
                        ),
                        style = MaterialTheme.typography.titleMedium
                    )
                }
            } else {
                Spacer(
                    modifier = Modifier
                        .height(16.dp)
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
fun CategoriesReportPreview() {
    JetSpendingTheme {
        CategoriesReport(
            selectedOption = TransactionType.INCOME,
            categories = listOf(
                CategoryPercentageUi(
                    categoryAmount = "Rp 100.000",
                    categoryColor = Orange700,
                    percentage = 0.3f,
                    name = R.string.category_food,
                    iconRes = R.drawable.ic_food_icon
                ),
                CategoryPercentageUi(
                    categoryAmount = "Rp 100.000",
                    categoryColor = Orange700,
                    percentage = 0.3f,
                    name = R.string.category_food,
                    iconRes = R.drawable.ic_food_icon
                ),
                CategoryPercentageUi(
                    categoryAmount = "Rp 100.000",
                    categoryColor = Orange700,
                    percentage = 0.3f,
                    name = R.string.category_food,
                    iconRes = R.drawable.ic_food_icon
                ),
            ),
            onSelectOption = {},
            isLoading = false
        )
    }
}