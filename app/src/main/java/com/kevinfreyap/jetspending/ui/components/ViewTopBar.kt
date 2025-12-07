package com.kevinfreyap.jetspending.ui.components

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.kevinfreyap.jetspending.R
import com.kevinfreyap.jetspending.ui.theme.Theme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ViewTopBar(
    title: String,
    onCurrencyIconClick: () -> Unit,
    onBackClick: (() -> Unit)? = null
) {
    CenterAlignedTopAppBar(
        title = {
            Text(
                text = title,
                color = Theme.custom.textColor,
                style = MaterialTheme.typography.headlineSmall
            )
        },
        actions = {
            IconButton(
                onClick = onCurrencyIconClick,
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_currency_exchange_24),
                    tint = Theme.custom.iconColor,
                    contentDescription = "Currency Exchange"
                )
            }
        },
        navigationIcon = {
            if (onBackClick != null){
                IconButton(
                    onClick = onBackClick
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_arrow_back_24),
                        tint = Theme.custom.iconColor,
                        contentDescription = stringResource(R.string.back)
                    )
                }
            }
        },
        windowInsets = WindowInsets(top = 0.dp)
    )
}