package com.kevinfreyap.jetspending.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.kevinfreyap.domain.model.AppCurrency
import com.kevinfreyap.jetspending.R
import com.kevinfreyap.jetspending.ui.theme.Theme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ViewTopBar(
    title: String,
    isLoading: Boolean? = null,
    showActionButton: Boolean = false,
    onBackClick: (() -> Unit)? = null,
    selectedCurrency: AppCurrency? = null,
    onSelectCurrency: (AppCurrency) -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxWidth()
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
                if (showActionButton) {
                    ViewCurrencyActionMenu(
                        currentCurrency = selectedCurrency,
                        onCurrencySelected = { newCurrency ->
                            onSelectCurrency(newCurrency)
                        }
                    )
                }
            },
            navigationIcon = {
                if (onBackClick != null) {
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

        if (isLoading != null && isLoading) {
            LinearProgressIndicator(
                color = MaterialTheme.colorScheme.primary,
                trackColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f),
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}