package com.kevinfreyap.jetspending.ui.components

import android.content.res.Configuration.UI_MODE_NIGHT_NO
import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.kevinfreyap.jetspending.R
import com.kevinfreyap.jetspending.ui.theme.JetSpendingTheme
import com.kevinfreyap.jetspending.ui.theme.Theme

@Composable
fun ViewPeriodNavigator(
    label: String,
    onPreviousClick: () -> Unit,
    onNextClick: () -> Unit,
    modifier: Modifier = Modifier,
    canGoPrevious: Boolean = true,
    canGoNext: Boolean = true,
    onLabelClick: (() -> Unit)? = null
) {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min)
    ) {
        Box(
            contentAlignment = Alignment.CenterStart,
            modifier = Modifier
                .weight(1f)
                .clip(
                    RoundedCornerShape(
                        topStartPercent = 50,
                        bottomStartPercent = 50
                    )
                )
                .clickable(
                    enabled = canGoPrevious,
                    role = Role.Button,
                    onClick = onPreviousClick
                )
                .padding(
                    vertical = 4.dp
                )
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_chevron_left),
                contentDescription = stringResource(R.string.btn_previous),
                tint = if (canGoPrevious) {
                    Theme.custom.secondaryTextColor
                } else {
                    Theme.custom.secondaryTextColor.copy(0.4f)
                },
                modifier = Modifier
                    .fillMaxWidth(0.7f)
                    .size(20.dp)
            )
        }

        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .then(
                    if (onLabelClick != null) {
                        Modifier.clickable(
                            role = Role.Button,
                            onClick = onLabelClick
                        )
                    } else Modifier
                )
                .padding(
                    horizontal = 16.dp,
                    vertical = 8.dp
                )
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium,
                color = Theme.custom.primaryTextColor
            )
        }

        Box(
            contentAlignment = Alignment.CenterEnd,
            modifier = Modifier
                .weight(1f)
                .clip(
                    RoundedCornerShape(
                        topEndPercent = 50,
                        bottomEndPercent = 50,
                    )
                )
                .clickable(
                    enabled = canGoNext,
                    role = Role.Button,
                    onClick = onNextClick
                )
                .padding(
                    vertical = 4.dp
                )
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_chevron_right),
                contentDescription = stringResource(R.string.btn_previous),
                tint = if (canGoNext) {
                    Theme.custom.secondaryTextColor
                } else {
                    Theme.custom.secondaryTextColor.copy(0.4f)
                },
                modifier = Modifier
                    .fillMaxWidth(0.7f)
                    .size(20.dp)
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
private fun ViewPeriodNavigatorPreview() {
    JetSpendingTheme {
        ViewPeriodNavigator(
            label = "September 2026",
            onPreviousClick = {},
            onNextClick = {},
            canGoNext = false
        )
    }
}