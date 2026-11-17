package com.kevinfreyap.jetspending.ui.screen.transaction_form.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.kevinfreyap.jetspending.R
import com.kevinfreyap.jetspending.ui.model.CategoryUI
import com.kevinfreyap.jetspending.ui.theme.JetSpendingTheme
import com.kevinfreyap.jetspending.ui.theme.Theme

@Composable
fun CategorySelectorCard(
    selectedCategory: CategoryUI?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    isError: Boolean = false,
    errorMessage: String = ""
) {
    val iconBackground: Color
    val iconColor: Color

    if (isError) {
        iconBackground = Theme.custom.negativeContainer
        iconColor = Theme.custom.statusNegative
    } else {
        iconBackground = Theme.custom.highlightContainer
        iconColor = MaterialTheme.colorScheme.primary
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .animateContentSize()
    ) {
        Surface(
            shape = RoundedCornerShape(12.dp),
            color = MaterialTheme.colorScheme.surfaceContainer,
            border = if (isError) {
                BorderStroke(1.dp, MaterialTheme.colorScheme.error)
            } else {
                null
            },
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .clickable(onClick = onClick)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier
                    .padding(
                        top = 8.dp,
                        bottom = 8.dp,
                        start = 12.dp,
                        end = 16.dp
                    )
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(40.dp)
                        .clip(
                            shape = RoundedCornerShape(12.dp)
                        )
                        .background(
                            if (selectedCategory != null) iconBackground else iconBackground.copy(0.6f)
                        )
                ) {
                    Icon(
                        painter = painterResource(
                            selectedCategory?.iconRes ?: R.drawable.ic_question_mark_24
                        ),
                        contentDescription = null,
                        tint = if (selectedCategory != null) iconColor else iconColor.copy(0.6f),
                        modifier = Modifier
                            .size(18.dp)
                    )
                }

                Column(
                    verticalArrangement = Arrangement.spacedBy(2.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = stringResource(R.string.category),
                        style = MaterialTheme.typography.labelMedium,
                        color = Theme.custom.secondaryTextColor
                    )

                    Text(
                        text = stringResource(selectedCategory?.name ?: R.string.placeholder_select_a_category),
                        style = MaterialTheme.typography.bodyLarge,
                        color = if (selectedCategory != null) {
                            Theme.custom.primaryTextColor
                        } else {
                            Theme.custom.secondaryTextColor.copy(alpha = 0.6f)
                        },
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Icon(
                    painter = painterResource(id = R.drawable.ic_keyboard_arrow_down_24),
                    contentDescription = null,
                    tint = Theme.custom.secondaryTextColor,
                    modifier = Modifier.size(20.dp)
                )
            }
        }

        AnimatedVisibility(
            visible = isError && errorMessage.isNotBlank(),
            enter = fadeIn() + expandVertically(),
            exit = fadeOut() + shrinkVertically()
        ) {
            Text(
                text = errorMessage,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier
                    .padding(
                        top = 4.dp,
                        start = 4.dp
                    )
            )
        }
    }
}

@Preview
@Composable
fun CategorySelectorCardPreview() {
    JetSpendingTheme {
        CategorySelectorCard(
            selectedCategory = CategoryUI(
                id = "1",
                sortOrder = 1,
                name = R.string.category_food,
                iconRes = R.drawable.ic_food_icon
            ),
            onClick = {  },
            isError = false,
            errorMessage = ""
        )
    }
}