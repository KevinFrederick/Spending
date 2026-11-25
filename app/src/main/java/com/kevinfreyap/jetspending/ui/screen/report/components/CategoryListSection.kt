package com.kevinfreyap.jetspending.ui.screen.report.components

import android.content.res.Configuration.UI_MODE_NIGHT_NO
import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.kevinfreyap.domain.model.AppCurrency
import com.kevinfreyap.jetspending.R
import com.kevinfreyap.jetspending.ui.helper.other.toLocalePercentageText
import com.kevinfreyap.jetspending.ui.model.CategoryUI
import com.kevinfreyap.jetspending.ui.screen.report.state.CategoryPercentageItemState
import com.kevinfreyap.jetspending.ui.theme.JetSpendingTheme
import com.kevinfreyap.jetspending.ui.theme.Theme
import com.kevinfreyap.jetspending.utils.shimmerEffect

@Composable
fun CategoryListSection(
    categories: List<CategoryPercentageItemState>,
    modifier: Modifier = Modifier
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = modifier
    ) {
        Text(
            text = stringResource(R.string.section_header_by_category),
            style = MaterialTheme.typography.titleMedium,
            color = Theme.custom.primaryTextColor
        )

        Column(
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            categories.forEach { category ->
                CategoryPercentageItem(
                    state = category
                )
            }

            if(categories.isEmpty()) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 40.dp)
                ) {
                    Text(
                        text = stringResource(R.string.empty_transactions),
                        style = MaterialTheme.typography.bodyMedium,
                        color = Theme.custom.secondaryTextColor,
                    )
                }
            }
        }
    }
}

@Composable
fun CategoryListSectionPlaceholder() {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = "",
            style = MaterialTheme.typography.titleMedium,
            color = Color.Transparent,
            modifier = Modifier
                .clearAndSetSemantics {}
                .fillMaxWidth(0.3f)
                .clip(RoundedCornerShape(50))
                .shimmerEffect(
                    shimmerColor = Theme.custom.shimmerColor
                )
        )

        Column(
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            repeat(5) {
                CategoryPercentageItemPlaceholder()
            }
        }
    }
}

@Preview(
    showBackground = true,
    backgroundColor = 0xFFFAFCFB,
    uiMode = UI_MODE_NIGHT_NO
)
@Preview(
    showBackground = true,
    backgroundColor = 0xFF121214,
    uiMode = UI_MODE_NIGHT_YES
)
@Composable
private fun CategoryListSectionPreview() {
    JetSpendingTheme {
        CategoryListSection(
            categories = listOf(
                CategoryPercentageItemState(
                    categoryUI = CategoryUI(
                        id = "1",
                        sortOrder = 1,
                        name = R.string.category_food,
                        iconRes = R.drawable.ic_food_icon,
                        types = emptyList()
                    ),
                    percentage = 0.7f,
                    percentageText = 0.7f.toLocalePercentageText(),
                    amount = "1.000.000",
                    selectedCurrency = AppCurrency.IDR
                ),
                CategoryPercentageItemState(
                    categoryUI = CategoryUI(
                        id = "2",
                        sortOrder = 2,
                        name = R.string.category_food,
                        iconRes = R.drawable.ic_food_icon,
                        types = emptyList()
                    ),
                    percentage = 0.7f,
                    percentageText = 0.7f.toLocalePercentageText(),
                    amount = "1.000.000",
                    selectedCurrency = AppCurrency.IDR
                ),
                CategoryPercentageItemState(
                    categoryUI = CategoryUI(
                        id = "3",
                        sortOrder = 3,
                        name = R.string.category_food,
                        iconRes = R.drawable.ic_food_icon,
                        types = emptyList()
                    ),
                    percentage = 0.7f,
                    percentageText = 0.7f.toLocalePercentageText(),
                    amount = "1.000.000",
                    selectedCurrency = AppCurrency.IDR
                )
            )
        )
    }
}

@Preview(
    showBackground = true,
    backgroundColor = 0xFFFAFCFB,
    uiMode = UI_MODE_NIGHT_NO
)
@Preview(
    showBackground = true,
    backgroundColor = 0xFF121214,
    uiMode = UI_MODE_NIGHT_YES
)
@Composable
private fun CategoryListSectionPreview_Empty() {
    JetSpendingTheme {
        CategoryListSection(
            categories = emptyList()
        )
    }
}

@Preview(
    showBackground = true,
    backgroundColor = 0xFFFAFCFB,
    uiMode = UI_MODE_NIGHT_NO
)
@Preview(
    showBackground = true,
    backgroundColor = 0xFF121214,
    uiMode = UI_MODE_NIGHT_YES
)
@Composable
private fun CategoryListSectionPlaceholderPreview() {
    JetSpendingTheme {
        CategoryListSectionPlaceholder()
    }
}