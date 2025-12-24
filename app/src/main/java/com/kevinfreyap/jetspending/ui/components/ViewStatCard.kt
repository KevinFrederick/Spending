package com.kevinfreyap.jetspending.ui.components

import androidx.compose.foundation.background
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.kevinfreyap.jetspending.ui.theme.Green500
import com.kevinfreyap.jetspending.ui.theme.JetSpendingTheme
import com.kevinfreyap.jetspending.ui.theme.Theme
import com.kevinfreyap.jetspending.utils.rememberShimmerBrush

@Composable
fun ViewStatCard(
    cardLabel: String,
    amount: String,
    color: Color,
    modifier: Modifier = Modifier,
    isLoading: Boolean = false
) {
    Card (
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Theme.custom.cardColor
        ),
        modifier = modifier
    ) {
        Column (
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    vertical = 24.dp,
                    horizontal = 16.dp
                )
        ) {
            Text(
                text = cardLabel,
                style = MaterialTheme.typography.titleMedium,
                color = Theme.custom.textColor,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(
                modifier = Modifier
                    .height(8.dp)
            )

            Text(
                text = amount,
                fontWeight = FontWeight.ExtraBold,
                style = MaterialTheme.typography.headlineSmall,
                color = color,
                maxLines = 1,
                softWrap = false,
                modifier = Modifier
                    .horizontalScroll(rememberScrollState())
                    .then(
                        if (isLoading) {
                            Modifier
                                .fillMaxWidth()
                                .background(Theme.custom.nestedCardColor, RoundedCornerShape(8.dp))
                                .background(rememberShimmerBrush())
                        }
                        else Modifier
                    )
            )
        }
    }
}

@Preview(
    showBackground = true,
    device = Devices.PIXEL_9_PRO
)
@Composable
fun ViewStatCardPreview() {
    JetSpendingTheme {
        ViewStatCard(
            cardLabel = "Total Income",
            amount = "Rp 1.000.000.000",
            color = Green500,
        )
    }
}