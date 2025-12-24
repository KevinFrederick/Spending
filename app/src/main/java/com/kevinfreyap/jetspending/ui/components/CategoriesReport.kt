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
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.kevinfreyap.jetspending.R
import com.kevinfreyap.jetspending.ui.theme.Green500
import com.kevinfreyap.jetspending.ui.theme.JetSpendingTheme
import com.kevinfreyap.jetspending.ui.theme.Theme

@Composable
fun CategoriesReport(
    modifier: Modifier = Modifier
) {
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
                .padding(16.dp)
        ) {
            Text(
                text = stringResource(R.string.category),
                style = MaterialTheme.typography.titleLarge,
                color = Theme.custom.textColor
            )

            Spacer(
                modifier = Modifier
                    .height(8.dp)
            )

            Column (
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                repeat(4) {
                    ViewCategoryReportItem(
                        categoryIcon = R.drawable.ic_salary_icon,
                        categoryName = "Salary",
                        categoryAmount = "Rp 100.000",
                        color = Green500,
                        percentage = "97%"
                    )
                }
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

        )
    }
}