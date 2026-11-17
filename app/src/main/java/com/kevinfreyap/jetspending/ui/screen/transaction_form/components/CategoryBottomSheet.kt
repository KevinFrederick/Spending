package com.kevinfreyap.jetspending.ui.screen.transaction_form.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.kevinfreyap.jetspending.R
import com.kevinfreyap.jetspending.ui.model.CategoryUI
import com.kevinfreyap.jetspending.ui.theme.JetSpendingTheme
import com.kevinfreyap.jetspending.ui.theme.Theme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryBottomSheet(
    categories: List<CategoryUI>,
    selectedCategory: CategoryUI?,
    onSelectCategory: (CategoryUI) -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true
    )

    ModalBottomSheet(
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surface,
        onDismissRequest = onDismiss,
        modifier = modifier
    ) {
        CategoryBottomSheetContent(
            categories = categories,
            selectedCategory = selectedCategory,
            onSelectCategory = onSelectCategory,
        )
    }
}

@Composable
fun CategoryBottomSheetContent(
    categories: List<CategoryUI>,
    selectedCategory: CategoryUI?,
    onSelectCategory: (CategoryUI) -> Unit,
    modifier: Modifier = Modifier
) {
    val density = LocalDensity.current
    val containerHeightPx = LocalWindowInfo.current.containerSize.height
    val maxGridHeight = with(density) { (containerHeightPx * 0.6f).toDp() }

    Column(
        verticalArrangement = Arrangement.spacedBy(20.dp),
        modifier = modifier
            .fillMaxWidth()
            .padding(
                top = 12.dp,
                bottom = 32.dp,
                start = 24.dp,
                end = 24.dp
            )
            .navigationBarsPadding()
    ) {
        Text(
            text = stringResource(R.string.placeholder_select_a_category),
            style = MaterialTheme.typography.headlineSmall,
            color = Theme.custom.primaryTextColor
        )

        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(max = maxGridHeight)
        ) {
            items(
                items = categories,
                key = { it.id }
            ) { category ->
                CategoryOptionCard(
                    category = category,
                    isSelected = category.id == selectedCategory?.id,
                    onClick = { onSelectCategory(category) }
                )
            }
        }
    }
}

@Preview(
    showBackground = true
)
@Composable
fun CategoryBottomSheetPreview() {
    JetSpendingTheme {
        CategoryBottomSheetContent(
            categories = listOf(
                CategoryUI(
                    id = "1",
                    sortOrder = 1,
                    name = R.string.category_food,
                    iconRes = R.drawable.ic_food_icon
                ),
                CategoryUI(
                    id = "2",
                    sortOrder = 2,
                    name = R.string.category_food,
                    iconRes = R.drawable.ic_food_icon
                ),
                CategoryUI(
                    id = "3",
                    sortOrder = 3,
                    name = R.string.category_payment_transfer,
                    iconRes = R.drawable.ic_food_icon
                ),
                CategoryUI(
                    id = "4",
                    sortOrder = 4,
                    name = R.string.category_food,
                    iconRes = R.drawable.ic_food_icon
                )
            ),
            selectedCategory = CategoryUI(
                id = "1",
                sortOrder = 1,
                name = R.string.category_food,
                iconRes = R.drawable.ic_food_icon
            ),
            onSelectCategory = {  },
        )
    }
}