package com.kevinfreyap.jetspending.ui.components

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.kevinfreyap.jetspending.R
import com.kevinfreyap.jetspending.ui.model.TransactionItemUi
import com.kevinfreyap.jetspending.ui.theme.Green500
import com.kevinfreyap.jetspending.ui.theme.Grey600
import com.kevinfreyap.jetspending.ui.theme.JetSpendingTheme
import com.kevinfreyap.jetspending.ui.theme.Red500
import com.kevinfreyap.jetspending.ui.theme.Theme
import com.kevinfreyap.jetspending.utils.rememberShimmerBrush
import java.time.Instant

@Composable
fun ViewTransactionItem(
    transaction: TransactionItemUi,
    navigateToDetail: () -> Unit,
    onCheckRate: (Instant) -> Unit,
    modifier: Modifier = Modifier,
    isNestedCard: Boolean = true,
) {
    if (transaction.isConversionPending) {
        LaunchedEffect(transaction.transactionId) {
            onCheckRate(transaction.transactionDateRaw)
        }
    }

    Card (
        colors = CardDefaults.cardColors(
            containerColor = if (isNestedCard) {
                Theme.custom.nestedCardColor
            } else {
                Theme.custom.cardColor
            },
        ),
        shape = RoundedCornerShape(16.dp),
        onClick = navigateToDetail,
        modifier = modifier
            .fillMaxWidth()
            .padding(2.dp)
    ) {
        Row (
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
                .height(IntrinsicSize.Min)
        ){
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(transaction.transactionTypeBackground)
            ) {
                Icon(
                    painter = painterResource(transaction.transactionCategoryIcon),
                    contentDescription = "Transaction Icon",
                    tint = Color.White,
                    modifier = Modifier
                        .size(28.dp)
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            Column(
                verticalArrangement = Arrangement.Center,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
            ) {
                Text(
                    text = transaction.transactionName,
                    style = MaterialTheme.typography.titleMedium,
                    color = Theme.custom.textColor,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier
                        .fillMaxWidth()
                )
                Text(
                    text = transaction.transactionDate,
                    fontWeight = FontWeight.SemiBold,
                    style = MaterialTheme.typography.bodySmall,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    color = Grey600,
                    modifier = Modifier
                        .fillMaxWidth()
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = transaction.transactionAmount,
                    fontWeight = FontWeight.Bold,
                    color = transaction.transactionTypeBackground
                )

                if (transaction.isConversionPending) {
                    Text(
                        text = stringResource(R.string.error_rate_unavailable),
                        style = MaterialTheme.typography.bodySmall,
                        color = Red500
                    )
                }
            }
        }
    }
}

@Composable
fun ViewTransactionItemPlaceholder(
    isNestedCard: Boolean = false
) {
    val brush = rememberShimmerBrush()

    val placeholderColor = Theme.custom.hintColor

    Card (
        colors = CardDefaults.cardColors(
            containerColor = if (isNestedCard) {
                Theme.custom.nestedCardColor
            } else {
                Theme.custom.cardColor
            },
        ),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(2.dp)
    ) {
        Row (
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
                .height(IntrinsicSize.Min)
        ){
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(placeholderColor)
                    .background(brush)
            )

            Spacer(modifier = Modifier.width(8.dp))

            Column(
                verticalArrangement = Arrangement.Center,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
            ) {
                Box(
                    modifier = Modifier
                        .width(120.dp)
                        .height(18.dp) // Match approx height of Title text
                        .clip(RoundedCornerShape(4.dp))
                        .background(placeholderColor)
                        .background(brush)
                )

                Spacer(
                    modifier = Modifier
                        .height(4.dp)
                )

                Box(
                    modifier = Modifier
                        .width(80.dp)
                        .height(12.dp) // Match approx height of Date text
                        .clip(RoundedCornerShape(4.dp))
                        .background(placeholderColor)
                        .background(brush)
                )
            }

            Box(
                modifier = Modifier
                    .width(80.dp)
                    .height(18.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(placeholderColor)
                    .background(brush)
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
fun ViewTransactionItemPreview() {
    JetSpendingTheme {
        ViewTransactionItem(
            navigateToDetail = {},
            transaction = TransactionItemUi(
                transactionId = "",
                transactionName = "Salary",
                transactionTypeBackground = Green500,
                transactionCategoryIcon = R.drawable.ic_salary_icon,
                transactionAmount = "+ Rp 1.000.000",
                transactionDate = "24 Oct 2025",
                transactionDateRaw = Instant.now(),
                isConversionPending = true,
            ),
            onCheckRate = {}
        )
    }
}

@Preview(
    showBackground = true,
    device = Devices.PIXEL_9_PRO,
    uiMode = Configuration.UI_MODE_NIGHT_YES
)
@Composable
fun ViewTransactionItemDarkPreview() {
    JetSpendingTheme {
        ViewTransactionItem(
            navigateToDetail = {},
            transaction = TransactionItemUi(
                transactionId = "",
                transactionName = "Salary",
                transactionTypeBackground = Green500,
                transactionCategoryIcon = R.drawable.ic_salary_icon,
                transactionAmount = "+ Rp 1.000.000",
                transactionDate = "24 Oct 2025",
                transactionDateRaw = Instant.now(),
                isConversionPending = false
            ),
            onCheckRate = {}
        )
    }
}

@Preview(
    showBackground = true,
    device = Devices.PIXEL_9_PRO,
    uiMode = Configuration.UI_MODE_NIGHT_YES
)
@Composable
fun ViewTransactionItemPlaceholderPreview() {
    JetSpendingTheme {
        ViewTransactionItemPlaceholder()
    }
}