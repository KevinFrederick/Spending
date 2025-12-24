package com.kevinfreyap.jetspending.ui.components

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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.kevinfreyap.jetspending.R
import com.kevinfreyap.jetspending.ui.theme.JetSpendingTheme
import com.kevinfreyap.jetspending.ui.theme.Orange700
import com.kevinfreyap.jetspending.ui.theme.Theme
import com.kevinfreyap.jetspending.utils.rememberShimmerBrush

@Composable
fun ViewCategoryReportItem (
    categoryIcon: Int,
    categoryName: String,
    categoryAmount: String,
    percentage: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card (
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Theme.custom.nestedCardColor
        ),
        modifier = modifier
            .fillMaxWidth()
    ) {
        Row (
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp)
                .height(IntrinsicSize.Min)
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(color)
            ) {
                Icon(
                    painter = painterResource(categoryIcon),
                    contentDescription = "Transaction Icon",
                    tint = Color.White,
                    modifier = Modifier
                        .size(28.dp)
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            Box (
                modifier = Modifier
                    .weight(1f)
            ) {
                Column(
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier
                        .fillMaxHeight()
                        .padding(
                            end = 8.dp,
                        )
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(IntrinsicSize.Min)
                    ) {
                        Text(
                            text = categoryName,
                            style = MaterialTheme.typography.titleMedium,
                            color = Theme.custom.textColor,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )

                        Text(
                            text = categoryAmount,
                            style = MaterialTheme.typography.titleSmall,
                            color = Theme.custom.textColor
                        )
                    }

                    Spacer(
                        modifier = Modifier
                            .height(4.dp)
                    )

                    LinearProgressIndicator(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                    )
                }

                Text(
                    text = percentage,
                    style = MaterialTheme.typography.labelMedium,
                    color = Theme.custom.hintColor,
                    modifier = Modifier
                        .padding(end = 8.dp)
                        .align(Alignment.BottomEnd)
                        .offset(
                            y = 8.dp
                        )
                )
            }
        }
    }
}

@Composable
fun ViewCategoryReportItemPlaceholder() {
    val brush = rememberShimmerBrush()

    val placeholderColor = Theme.custom.hintColor

    Card (
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Theme.custom.nestedCardColor
        ),
        modifier = Modifier
            .fillMaxWidth()
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
                    .padding(
                        end = 8.dp
                    )
            ) {
                Row (
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(IntrinsicSize.Min)
                ) {
                    Box(
                        modifier = Modifier
                            .width(120.dp)
                            .height(18.dp) // Match approx height of Title text
                            .clip(RoundedCornerShape(4.dp))
                            .background(placeholderColor)
                            .background(brush)
                    )

                    Box(
                        modifier = Modifier
                            .width(64.dp)
                            .height(18.dp) // Match approx height of Title text
                            .clip(RoundedCornerShape(4.dp))
                            .background(placeholderColor)
                            .background(brush)
                    )
                }

                Spacer(
                    modifier = Modifier
                        .height(4.dp)
                )

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp) // Match approx height of Date text
                        .clip(RoundedCornerShape(50))
                        .background(placeholderColor)
                        .background(brush)
                )
            }
        }
    }
}

@Preview(
    showBackground = true,
    device = Devices.PIXEL_9_PRO
)
@Composable
fun ViewCategoryReportItemPreview() {
    JetSpendingTheme {
        ViewCategoryReportItem(
            categoryIcon = R.drawable.ic_salary_icon,
            categoryName = "Salary",
            categoryAmount = "Rp 100.000",
            color = Orange700,
            percentage = "97%"
        )
    }
}

@Preview(
    showBackground = true,
    device = Devices.PIXEL_9_PRO,
)
@Composable
fun ViewCategoryReportItemPlaceholderPreview() {
    JetSpendingTheme {
        ViewCategoryReportItemPlaceholder()
    }
}