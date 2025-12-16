package com.kevinfreyap.jetspending.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.kevinfreyap.jetspending.ui.theme.Theme

@Composable
fun ViewPageError(
    icon: Int,
    text: String,
    modifier: Modifier = Modifier,
    iconColor: Color = Theme.custom.hintColor,
    textColor: Color = Theme.custom.hintColor,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ) {
        Icon(
            painter = painterResource(icon),
            contentDescription = "Page Error",
            tint = iconColor,
            modifier = Modifier
                .size(128.dp)
        )

        Spacer(
            modifier = Modifier
                .height(16.dp)
        )

        Text(
            text = text,
            style = MaterialTheme.typography.headlineSmall,
            color = textColor
        )
    }
}