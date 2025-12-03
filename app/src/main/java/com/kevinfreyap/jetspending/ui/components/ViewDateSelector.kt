package com.kevinfreyap.jetspending.ui.components

import android.content.res.Configuration
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import com.kevinfreyap.jetspending.ui.theme.JetSpendingTheme

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
            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
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
            fontWeight = FontWeight.SemiBold
        )
        Icon(
            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
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
    name = "Light Mode",
    showBackground = true,
    device = Devices.PIXEL_9_PRO,
    uiMode = Configuration.UI_MODE_NIGHT_NO
)
@Composable
fun ViewDateSelectorLightPreview(){
    JetSpendingTheme {
        ViewDateSelector(
            centerText = "December",
            onPreviousClick = {},
            onNextClick = {},
            onPreviousBtnEnabled = true,
            onNextBtnEnabled = true,
        )
    }
}

@Preview (
    name = "Dark Mode",
    showBackground = true,
    device = Devices.PIXEL_9_PRO,
    uiMode = Configuration.UI_MODE_NIGHT_YES
)
@Composable
fun ViewDateSelectorDarkPreview(){
    JetSpendingTheme {
        ViewDateSelector(
            centerText = "December",
            onPreviousClick = {},
            onNextClick = {},
            onPreviousBtnEnabled = true,
            onNextBtnEnabled = true
        )
    }
}