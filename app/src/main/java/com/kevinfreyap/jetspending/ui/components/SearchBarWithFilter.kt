package com.kevinfreyap.jetspending.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.kevinfreyap.jetspending.R
import com.kevinfreyap.jetspending.ui.theme.JetSpendingTheme
import com.kevinfreyap.jetspending.ui.theme.Red500
import com.kevinfreyap.jetspending.ui.theme.Theme

@Composable
fun SearchBarWithFilter(
    searchQuery: String,
    onQueryChange: (String) -> Unit,
    onFilterClick: () -> Unit,
    modifier: Modifier = Modifier,
    showBadge: Boolean = false,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .fillMaxWidth()
    ) {
        ViewTextField(
            value = searchQuery,
            onValueChange = onQueryChange,
            label = "",
            placeholder = stringResource(R.string.search_transactions),
            shape = RoundedCornerShape(50),
            modifier = Modifier
                .weight(1f)
        )

        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(48.dp)
                .clickable(
                    onClick = onFilterClick,
                    indication = ripple(bounded = false, radius = 24.dp),
                    interactionSource = remember { MutableInteractionSource() }
                )
        ) {
            BadgedBox(
                badge = {
                    if (showBadge) {
                        Badge(
                            containerColor = Red500,
                            contentColor = Theme.custom.textColor,
                            modifier = Modifier
                                .size(8.dp)
                                .offset(
                                    x = 3.dp,
                                    y = (-3).dp
                                )
                        )
                    }
                }
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_filter_list_24),
                    contentDescription = "Filter",
                    tint = Theme.custom.iconColor,
                    modifier = Modifier
                        .size(32.dp)
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
fun SearchBarWithFilterPreview() {
    JetSpendingTheme {
        SearchBarWithFilter(
            searchQuery = "",
            onQueryChange = {},
            onFilterClick = {},
            showBadge = true
        )
    }
}