package com.kevinfreyap.jetspending.ui.components.v2

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.kevinfreyap.domain.model.AppCurrency
import com.kevinfreyap.jetspending.R
import com.kevinfreyap.jetspending.ui.components.ViewSelectionItem
import com.kevinfreyap.jetspending.ui.theme.JetSpendingTheme
import com.kevinfreyap.jetspending.ui.theme.Theme
import com.kevinfreyap.jetspending.utils.drawScrollbar

@Composable
fun ViewSelectionDialog(
    @StringRes titleRes: Int,
    options: @Composable () -> Unit,
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
) {
    Dialog(
        onDismissRequest = onDismissRequest,
    ) {
        ViewSelectionDialogContent(
            titleRes = titleRes,
            options = options,
            onDismissRequest = onDismissRequest,
            subtitle = subtitle,
            modifier = modifier
        )
    }
}

@Composable
fun ViewSelectionDialogContent(
    @StringRes titleRes: Int,
    options: @Composable () -> Unit,
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
) {
    val scrollState = rememberScrollState()

    Card (
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
    ) {
        Column(
            modifier = modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Column {
                Text(
                    text = stringResource(titleRes),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.SemiBold,
                    color = Theme.custom.primaryTextColor
                )

                subtitle?.let {
                    Text(
                        text = it,
                        style = MaterialTheme.typography.labelMedium,
                        color = Theme.custom.secondaryTextColor
                    )
                }
            }

            Spacer(
                modifier = Modifier
                    .height(12.dp)
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 300.dp)
                    .drawScrollbar(
                        scrollState = scrollState,
                        color = Theme.custom.secondaryTextColor.copy(0.4f)
                    )
                    .verticalScroll(scrollState)
            ) {
                options()
            }

            TextButton(
                onClick = onDismissRequest,
                modifier = Modifier
                    .align(Alignment.End)
            ) {
                Text(
                    text = stringResource(R.string.cancel),
                    style = MaterialTheme.typography.labelLarge,
                    color = Theme.custom.secondaryTextColor
                )
            }
        }
    }
}

@Preview(
    showBackground = true,
)
@Composable
fun ViewSelectionDialogPreview() {
    JetSpendingTheme {
        ViewSelectionDialogContent(
            titleRes = R.string.select_currency,
            options = {
                AppCurrency.entries.forEach { currency ->
                    ViewSelectionItem(
                        title = currency.name,
                        subtitle = currency.displayName,
                        isSelected = currency == AppCurrency.IDR,
                        onClick = {}
                    )
                }
            },
            onDismissRequest = {},
        )
    }
}