package com.kevinfreyap.jetspending.ui.components

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.kevinfreyap.jetspending.R
import com.kevinfreyap.jetspending.ui.theme.JetSpendingTheme
import com.kevinfreyap.jetspending.ui.theme.Theme
import com.kevinfreyap.jetspending.utils.rememberShimmerBrush

@Composable
fun ViewDateSelector(
    centerText: String,
    onPreviousClick: () -> Unit,
    onPreviousBtnEnabled: Boolean,
    onNextClick: () -> Unit,
    onNextBtnEnabled: Boolean,
    modifier: Modifier = Modifier,
    isLoading: Boolean = false
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceAround,
        modifier = modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min)
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .clickable(
                    enabled = onPreviousBtnEnabled
                ) {
                    onPreviousClick()
                },
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_chevron_left),
                tint = if (onPreviousBtnEnabled) Theme.custom.iconColor else Theme.custom.hintColor,
                contentDescription = null
            )
        }
        Text(
            text = centerText,
            color = Theme.custom.textColor,
            fontWeight = FontWeight.SemiBold,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .padding(
                    horizontal = 8.dp
                )
                .then(
                    if (isLoading) {
                        Modifier
                            .width(128.dp)
                            .background(Theme.custom.nestedCardColor, RoundedCornerShape(8.dp))
                            .background(rememberShimmerBrush())
                    }
                    else Modifier
                )
        )
        Box (
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .clickable(
                    enabled = onNextBtnEnabled
                ) {
                    onNextClick()
                }
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_chevron_right),
                tint = if (onNextBtnEnabled) Theme.custom.iconColor else Theme.custom.hintColor,
                contentDescription = null,
            )
        }
    }
}

@Preview (
    showBackground = true,
    device = Devices.PIXEL_9_PRO,
    uiMode = Configuration.UI_MODE_NIGHT_NO
)
@Composable
fun ViewDateSelectorPreview(){
    JetSpendingTheme {
        ViewDateSelector(
            centerText = "December",
            onPreviousClick = {},
            onNextClick = {},
            onPreviousBtnEnabled = false,
            onNextBtnEnabled = true,
            isLoading = false
        )
    }
}