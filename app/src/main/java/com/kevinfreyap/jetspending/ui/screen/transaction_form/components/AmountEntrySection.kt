package com.kevinfreyap.jetspending.ui.screen.transaction_form.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.kevinfreyap.domain.model.AppCurrency
import com.kevinfreyap.jetspending.R
import com.kevinfreyap.jetspending.ui.components.v2.ViewDropdownSelection
import com.kevinfreyap.jetspending.ui.screen.transaction_form.state.AmountEntryState
import com.kevinfreyap.jetspending.ui.theme.JetSpendingTheme
import com.kevinfreyap.jetspending.ui.theme.Theme
import com.kevinfreyap.jetspending.utils.CurrencyVisualTransformation
import com.kevinfreyap.jetspending.utils.getLocale
import com.kevinfreyap.jetspending.utils.shimmerEffect
import java.text.DecimalFormatSymbols
import java.time.Instant

@Composable
fun AmountEntrySection(
    state: AmountEntryState,
    onAmountChange: (String) -> Unit,
    onCurrencySelected: (AppCurrency) -> Unit,
    modifier: Modifier = Modifier,
    isError: Boolean = false,
    errorMessage: String = ""
) {
    val convertedAmount = state.convertedAmount
        ?: if (state.convertedCurrency.isFraction) "0.00" else "0"

    val conversionRate = state.conversionRate
        ?: stringResource(R.string.rate_unavailable)

    val textColor = if (isError) MaterialTheme.colorScheme.error else Theme.custom.primaryTextColor

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = modifier
            .fillMaxWidth()
            .padding(
                vertical = 8.dp
            )
    ) {
        Text(
            text = stringResource(R.string.amount),
            style = MaterialTheme.typography.bodySmall,
            color = Theme.custom.secondaryTextColor
        )

        BasicTextField(
            value = state.originalAmount,
            onValueChange = onAmountChange,
            singleLine = true,
            textStyle = MaterialTheme.typography.displayLarge.copy(
                color = textColor,
                textAlign = TextAlign.Center
            ),
            cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Decimal,
                imeAction = ImeAction.Done
            ),
            visualTransformation = CurrencyVisualTransformation(state.originalCurrency),
            decorationBox = { innerTextField ->
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    if (state.originalAmount.isBlank()) {
                        val decimalSep = remember(state.originalCurrency) {
                            DecimalFormatSymbols(state.originalCurrency.getLocale()).decimalSeparator
                        }

                        Text(
                            text = if (state.originalCurrency.isFraction) "0${decimalSep}00" else "0",
                            style = MaterialTheme.typography.displayLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f)
                        )
                    }
                    innerTextField()
                }
            },
            modifier = Modifier
                .fillMaxWidth()
        )

        AnimatedVisibility(
            visible = isError && errorMessage.isNotBlank(),
            enter = fadeIn() + expandVertically(),
            exit = fadeOut() + shrinkVertically()
        ) {
            Text(
                text = errorMessage,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.error,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        }

        ViewDropdownSelection(
            options = AppCurrency.entries,
            selectedOption = state.originalCurrency,
            onSelectOption = onCurrencySelected,
            optionLabel = {
                it.displayCodeName
            }
        )

        AnimatedVisibility(
            visible = state.originalCurrency != state.convertedCurrency,
            enter = fadeIn() + expandVertically(),
            exit = fadeOut() + shrinkVertically()
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(4.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "≈ ${state.convertedCurrency.name} $convertedAmount",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary
                )

                Text(
                    text = "$conversionRate • ${state.formattedRateDate}",
                    style = MaterialTheme.typography.bodySmall,
                    color = Theme.custom.secondaryTextColor
                )
            }
        }
    }
}

@Composable
fun AmountEntrySectionPlaceholder(
    isConversionRateVisible: Boolean = false
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                vertical = 8.dp
            )
    ) {
        Text(
            text = "",
            style = MaterialTheme.typography.bodySmall,
            color = Color.Transparent,
            modifier = Modifier
                .clearAndSetSemantics {}
                .fillMaxWidth(0.3f)
                .clip(RoundedCornerShape(50))
                .shimmerEffect(
                    shimmerColor = Theme.custom.shimmerColor
                )
        )

        Text(
            text = "",
            style = MaterialTheme.typography.displayLarge,
            color = Color.Transparent,
            modifier = Modifier
                .clearAndSetSemantics {}
                .fillMaxWidth(0.7f)
                .clip(RoundedCornerShape(50))
                .shimmerEffect(
                    shimmerColor = Theme.custom.shimmerColor
                )
        )

        Box(
            modifier = Modifier
                .defaultMinSize(minHeight = 40.dp)
                .fillMaxWidth(0.5f)
                .padding(vertical = 4.dp)
                .clip(RoundedCornerShape(50))
                .shimmerEffect(
                    shimmerColor = Theme.custom.shimmerColor
                )
        )

        if (isConversionRateVisible) {
            Column(
                verticalArrangement = Arrangement.spacedBy(4.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Transparent,
                    modifier = Modifier
                        .clearAndSetSemantics {}
                        .fillMaxWidth(0.2f)
                        .clip(RoundedCornerShape(50))
                        .shimmerEffect(
                            shimmerColor = Theme.custom.shimmerColor
                        )
                )

                Text(
                    text = "",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Transparent,
                    modifier = Modifier
                        .clearAndSetSemantics {}
                        .fillMaxWidth(0.7f)
                        .clip(RoundedCornerShape(50))
                        .shimmerEffect(
                            shimmerColor = Theme.custom.shimmerColor
                        )
                )
            }
        }
    }
}

@Preview (
    showBackground = true
)
@Composable
fun AmountEntrySectionPreview() {
    JetSpendingTheme {
        AmountEntrySection(
            state = AmountEntryState(
                originalAmount = "100000",
                originalCurrency = AppCurrency.IDR,
                convertedAmount = "5.55",
                convertedCurrency = AppCurrency.USD,
                conversionRate = "1 IDR = 0,000055 USD",
                rateDate = Instant.now()
            ),
            onAmountChange = {},
            onCurrencySelected = {}
        )
    }
}

@Preview(
    showBackground = true
)
@Composable
fun AmountEntrySectionPlaceholderPreview() {
    JetSpendingTheme {
        AmountEntrySectionPlaceholder(
            isConversionRateVisible = true
        )
    }
}