package com.kevinfreyap.jetspending.ui.screen.report.components

import android.content.res.Configuration.UI_MODE_NIGHT_NO
import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
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
fun CategoryPercentageItem(
    state: CategoryPercentageItemState,
    modifier: Modifier = Modifier
) {
    val animatedProgress by animateFloatAsState(
        targetValue = state.percentage.coerceIn(0f, 1f),
        animationSpec = tween(
            durationMillis = 350,
            easing = FastOutSlowInEasing
        ),
        label = "category_progress_${state.categoryUI.id}"
    )

    Row(
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .padding(
                vertical = 4.dp,
                horizontal = 4.dp
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
                    MaterialTheme.colorScheme.surfaceContainer
                )
        ) {
            Icon(
                painter = painterResource(state.categoryUI.iconRes),
                contentDescription = null,
                tint = Theme.custom.secondaryTextColor,
                modifier = Modifier
                    .size(18.dp)
            )
        }

        Column(
            modifier = Modifier
                .weight(1f)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                Text(
                    text = stringResource(state.categoryUI.name),
                    style = MaterialTheme.typography.bodyMedium,
                    color = Theme.custom.primaryTextColor,
                    maxLines = 1,
                    softWrap = false,
                    overflow = TextOverflow.Ellipsis
                )

                Text(
                    text = buildAnnotatedString {
                        append(state.amount)
                        append(" ")
                        withStyle(style = SpanStyle(
                            fontSize = MaterialTheme.typography.labelSmall.fontSize,
                            color = Theme.custom.secondaryTextColor
                        )) {
                            append(state.selectedCurrency.name)
                        }
                    },
                    style = MaterialTheme.typography.bodySmall,
                    color = Theme.custom.primaryTextColor,
                    maxLines = 1,
                    softWrap = false,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(4.dp)
                        .clip(CircleShape)
                        .background(
                            Theme.custom.highlightContainer.copy(0.6f)
                        )
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(animatedProgress)
                            .fillMaxHeight()
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primary)
                    )
                }

                Text(
                    text = state.percentageText,
                    style = MaterialTheme.typography.bodySmall,
                    color = Theme.custom.secondaryTextColor,
                    textAlign = TextAlign.End,
                    maxLines = 1,
                    softWrap = false,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier
                        .width(40.dp)
                )
            }
        }
    }
}

@Composable
fun CategoryPercentageItemPlaceholder() {
    Row(
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .padding(
                vertical = 4.dp,
                horizontal = 4.dp
            )
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(
                    shape = RoundedCornerShape(12.dp)
                )
                .shimmerEffect(
                    shimmerColor = Theme.custom.shimmerColor
                )
        )

        Column(
            modifier = Modifier
                .weight(1f)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                Text(
                    text = "",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Transparent,
                    modifier = Modifier
                        .clearAndSetSemantics {}
                        .fillMaxWidth(0.5f)
                        .clip(RoundedCornerShape(50))
                        .shimmerEffect(
                            shimmerColor = Theme.custom.shimmerColor
                        )
                )

                Text(
                    text = "",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Transparent,
                    modifier = Modifier
                        .clearAndSetSemantics {}
                        .fillMaxWidth(0.5f)
                        .clip(RoundedCornerShape(50))
                        .shimmerEffect(
                            shimmerColor = Theme.custom.shimmerColor
                        )
                )
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(8.dp)
                        .clip(CircleShape)
                        .background(
                            Theme.custom.shimmerColor
                        )
                )

                Text(
                    text = "",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Transparent,
                    textAlign = TextAlign.End,
                    maxLines = 1,
                    softWrap = false,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier
                        .width(40.dp)
                        .clearAndSetSemantics {}
                        .fillMaxWidth(0.5f)
                        .clip(RoundedCornerShape(50))
                        .shimmerEffect(
                            shimmerColor = Theme.custom.shimmerColor
                        )
                )
            }
        }
    }
}

@Preview (
    showBackground = true,
    backgroundColor = 0xFFFAFCFB,
    uiMode = UI_MODE_NIGHT_NO,
)
@Preview (
    showBackground = true,
    backgroundColor = 0xFF121214,
    uiMode = UI_MODE_NIGHT_YES,
)
@Composable
fun CategoryPercentageItemPreview() {
    JetSpendingTheme {
        CategoryPercentageItem(
            state = CategoryPercentageItemState(
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
fun CategoryPercentageItemPlaceholderPreview() {
    JetSpendingTheme {
        CategoryPercentageItemPlaceholder()
    }
}