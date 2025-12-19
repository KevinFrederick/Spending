package com.kevinfreyap.jetspending.ui.components

import android.content.res.Configuration
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import com.kevinfreyap.jetspending.R
import com.kevinfreyap.jetspending.ui.theme.JetSpendingTheme
import com.kevinfreyap.jetspending.ui.theme.Theme

@Composable
fun ViewDateSelector(
    centerText: String,
    onPreviousClick: () -> Unit,
    onPreviousBtnEnabled: Boolean,
    onNextClick: () -> Unit,
    onNextBtnEnabled: Boolean,
    modifier: Modifier = Modifier
) {
    Row(
        horizontalArrangement = Arrangement.SpaceAround,
        modifier = modifier
            .fillMaxWidth()
    ) {
        Icon(
            painter = painterResource(R.drawable.ic_chevron_left),
            tint = if (onPreviousBtnEnabled) Theme.custom.iconColor else Theme.custom.hintColor,
            contentDescription = null,
            modifier = Modifier
                .weight(1f)
                .clickable (
                    enabled = onPreviousBtnEnabled
                ) {
                    onPreviousClick()
                }
        )
        Text(
            text = centerText,
            color = Theme.custom.textColor,
            fontWeight = FontWeight.SemiBold
        )
        Icon(
            painter = painterResource(R.drawable.ic_chevron_right),
            tint = if (onNextBtnEnabled) Theme.custom.iconColor else Theme.custom.hintColor,
            contentDescription = null,
            modifier = Modifier
                .weight(1f)
                .clickable (
                    enabled = onNextBtnEnabled
                ) {
                    onNextClick()
                }
        )
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
        )
    }
}