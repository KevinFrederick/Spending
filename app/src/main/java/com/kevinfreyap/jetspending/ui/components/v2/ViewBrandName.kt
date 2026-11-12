package com.kevinfreyap.jetspending.ui.components.v2

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.kevinfreyap.jetspending.R
import com.kevinfreyap.jetspending.ui.theme.JetSpendingTheme
import com.kevinfreyap.jetspending.ui.theme.Theme

@Composable
fun ViewBrandName(
    modifier: Modifier = Modifier
) {
    Row (
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = modifier
    ) {
        Icon(
            painter = painterResource(R.drawable.app_icon_wallet),
            contentDescription = null,
            tint = Color.Unspecified,
            modifier = Modifier
                .size(32.dp)
        )

        Text(
            text = stringResource(R.string.app_name),
            style = MaterialTheme.typography.titleLarge,
            color = Theme.custom.primaryTextColor
        )
    }
}

@Preview(
    showBackground = true
)
@Composable
fun ViewBrandNamePreview() {
    JetSpendingTheme {
        ViewBrandName()
    }
}