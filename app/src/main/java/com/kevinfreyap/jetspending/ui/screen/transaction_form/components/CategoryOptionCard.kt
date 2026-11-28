package com.kevinfreyap.jetspending.ui.screen.transaction_form.components

import android.content.res.Configuration.UI_MODE_NIGHT_NO
import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.kevinfreyap.domain.model.TransactionType
import com.kevinfreyap.jetspending.R
import com.kevinfreyap.jetspending.ui.model.CategoryUI
import com.kevinfreyap.jetspending.ui.theme.JetSpendingTheme
import com.kevinfreyap.jetspending.ui.theme.Theme

@Composable
fun CategoryOptionCard(
    category: CategoryUI,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val containerColor = if (isSelected) {
        Theme.custom.highlightContainer.copy(0.6f)
    } else {
        MaterialTheme.colorScheme.surface
    }

    val borderStroke = if (isSelected) {
        BorderStroke(1.dp, MaterialTheme.colorScheme.primary)
    } else {
        null
    }

    Surface(
        shape = RoundedCornerShape(16.dp),
        color = containerColor,
        border = borderStroke,
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .clip(RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxSize()
                .padding(4.dp)
        ) {
            Spacer(modifier = Modifier.weight(0.85f))

            Icon(
                painter = painterResource(id = category.iconRes),
                contentDescription = null,
                tint = if (isSelected) {
                    MaterialTheme.colorScheme.primary
                } else {
                    Theme.custom.primaryTextColor
                },
                modifier = Modifier
                    .size(24.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Box(
                contentAlignment = Alignment.TopCenter,
                modifier = Modifier.weight(1.15f)
            ) {
                Text(
                    text = stringResource(category.name),
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                    color = if (isSelected) {
                        MaterialTheme.colorScheme.primary
                    } else {
                        Theme.custom.primaryTextColor
                    },
                    textAlign = TextAlign.Center,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
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
private fun CategoryOptionCardPreview_Selected() {
    JetSpendingTheme {
        CategoryOptionCard(
            category = CategoryUI(
                id = "1",
                sortOrder = 1,
                name = R.string.category_food,
                iconRes = R.drawable.ic_food_icon,
                type = TransactionType.SPENDING
            ),
            isSelected = true,
            onClick = {  },
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
private fun CategoryOptionCardPreview_NotSelected() {
    JetSpendingTheme {
        CategoryOptionCard(
            category = CategoryUI(
                id = "1",
                sortOrder = 1,
                name = R.string.category_food,
                iconRes = R.drawable.ic_food_icon,
                type = TransactionType.SPENDING
            ),
            isSelected = false,
            onClick = {  },
        )
    }
}