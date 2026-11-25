package com.kevinfreyap.jetspending.ui.screen.detail.components

import android.content.res.Configuration.UI_MODE_NIGHT_NO
import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.kevinfreyap.domain.model.AppCurrency
import com.kevinfreyap.domain.model.TransactionType
import com.kevinfreyap.jetspending.R
import com.kevinfreyap.jetspending.ui.helper.filter.toUiNameRes
import com.kevinfreyap.jetspending.ui.screen.detail.state.TransactionDetailSummaryState
import com.kevinfreyap.jetspending.ui.theme.JetSpendingTheme
import com.kevinfreyap.jetspending.ui.theme.Theme
import com.kevinfreyap.jetspending.utils.shimmerEffect

@Composable
fun TransactionDetailSummary(
    state: TransactionDetailSummaryState,
    modifier: Modifier = Modifier
) {
    val formattedOriginalAmount: String
    val textColor: Color
    val containerColor: Color

    when(state.type) {
        TransactionType.INCOME -> {
            formattedOriginalAmount = "+ ${state.originalAmountWithCurrency}"
            textColor = Theme.custom.statusPositive
            containerColor = Theme.custom.highlightContainer
        }
        TransactionType.SPENDING -> {
            formattedOriginalAmount = "- ${state.originalAmountWithCurrency}"
            textColor = Theme.custom.statusNegative
            containerColor = Theme.custom.negativeContainer
        }
    }

    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp)
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(64.dp)
                .clip(
                    shape = RoundedCornerShape(22.dp)
                )
                .background(
                    MaterialTheme.colorScheme.surfaceContainer
                )
        ) {
            Icon(
                painter = painterResource(state.categoryIconRes),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(32.dp)
            )
        }

        Text(
            text = state.name,
            style = MaterialTheme.typography.titleLarge,
            color = Theme.custom.primaryTextColor
        )

        Text(
            text = formattedOriginalAmount,
            style = MaterialTheme.typography.displaySmall,
            color = textColor
        )

        if (!state.isSameCurrency){
            Text(
                text = state.convertedAmountWithCurrency?.let {
                    "≈ $it"
                } ?: stringResource(R.string.label_rate_unavailable),
                style = MaterialTheme.typography.bodyMedium,
                color = Theme.custom.secondaryTextColor
            )
        }

        Surface(
            color = containerColor,
            contentColor = textColor,
            shape = CircleShape,
        ) {
            Text(
                text = stringResource(state.type.toUiNameRes()),
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
            )
        }
    }
}

@Composable
fun TransactionDetailSummaryPlaceholder() {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp)
    ) {
        Box(
            modifier = Modifier
                .size(64.dp)
                .clip(
                    shape = RoundedCornerShape(22.dp)
                )
                .shimmerEffect(
                    shimmerColor = Theme.custom.shimmerColor
                )
        )

        Text(
            text = "",
            style = MaterialTheme.typography.titleLarge,
            color = Color.Transparent,
            modifier = Modifier
                .clearAndSetSemantics {}
                .fillMaxWidth(0.4f)
                .clip(RoundedCornerShape(50))
                .shimmerEffect(
                    shimmerColor = Theme.custom.shimmerColor
                )
        )

        Text(
            text = "",
            style = MaterialTheme.typography.displaySmall,
            color = Color.Transparent,
            modifier = Modifier
                .clearAndSetSemantics {}
                .fillMaxWidth(0.6f)
                .clip(RoundedCornerShape(50))
                .shimmerEffect(
                    shimmerColor = Theme.custom.shimmerColor
                )
        )

        Text(
            text = "",
            style = MaterialTheme.typography.bodyMedium,
            color = Color.Transparent,
            modifier = Modifier
                .clearAndSetSemantics {}
                .fillMaxWidth(0.2f)
                .clip(RoundedCornerShape(50))
                .shimmerEffect(
                    shimmerColor = Theme.custom.shimmerColor
                )
        )

        Box(
            modifier = Modifier
                .clip(
                    shape = RoundedCornerShape(100)
                )
                .shimmerEffect(
                    shimmerColor = Theme.custom.shimmerColor
                )
        ) {
            Text(
                text = "",
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Medium,
                modifier = Modifier
                    .fillMaxWidth(0.3f)
                    .padding(
                        horizontal = 14.dp,
                        vertical = 6.dp
                    )
            )
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
private fun TransactionDetailSummaryPreview() {
    JetSpendingTheme {
        TransactionDetailSummary(
            state = TransactionDetailSummaryState(
                categoryIconRes = R.drawable.ic_food_icon,
                name = "Lunch",
                type = TransactionType.SPENDING,
                originalAmountWithCurrency = "100.000 IDR",
                originalCurrency = AppCurrency.IDR,
                convertedAmountWithCurrency = "5 USD",
                convertedCurrency = AppCurrency.IDR
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
private fun TransactionDetailSummaryPlaceholderPreview() {
    JetSpendingTheme {
        TransactionDetailSummaryPlaceholder()
    }
}