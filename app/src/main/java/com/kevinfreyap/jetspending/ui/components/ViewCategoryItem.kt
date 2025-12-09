package com.kevinfreyap.jetspending.ui.components

import android.content.res.Configuration
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.BiasAlignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.kevinfreyap.jetspending.R
import com.kevinfreyap.jetspending.ui.theme.JetSpendingTheme
import com.kevinfreyap.jetspending.ui.theme.Theme

@Composable
fun ViewCategoryItem(
    categoryImage: Int,
    categoryName: String,
    isSelected: Boolean,
    modifier: Modifier = Modifier
) {
    val backgroundColor = if (isSelected) MaterialTheme.colorScheme.primary else Theme.custom.cardColor
    val iconColor = if (isSelected) MaterialTheme.colorScheme.onPrimary else Theme.custom.iconColor
    val textColor = if (isSelected) MaterialTheme.colorScheme.onPrimary else Theme.custom.textColor

    Card(
        colors = CardDefaults.cardColors(
            containerColor = backgroundColor
        ),
        modifier = modifier
            .size(100.dp)
            .clip(RoundedCornerShape(16.dp))
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
        ) {
            Image(
                painter = painterResource(categoryImage),
                colorFilter = ColorFilter.tint(iconColor),
                contentDescription = "Category Image",
                modifier = Modifier
                    .size(40.dp)
                    .align(
                        BiasAlignment(
                            horizontalBias = 0f,
                            verticalBias = -0.4f
                        )
                    )
            )
            Text(
                text = categoryName,
                fontWeight = FontWeight.SemiBold,
                color = textColor,
                style = MaterialTheme.typography.bodySmall,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .padding(
                        top = 32.dp
                    )
                    .align(
                        BiasAlignment(
                            horizontalBias = 0f,
                            verticalBias = 0.6f
                        )
                    )
            )
        }
    }
}

@Preview(
    showBackground = true,
    device = Devices.PIXEL_9_PRO,
    uiMode = Configuration.UI_MODE_NIGHT_NO
)
@Composable
fun ViewCategoryItemPreview(){
    JetSpendingTheme {
        ViewCategoryItem(
            categoryImage = R.drawable.ic_salary_icon,
            categoryName = "Salary",
            isSelected = false
        )
    }
}