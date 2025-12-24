package com.kevinfreyap.jetspending.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.kevinfreyap.domain.model.PeriodSelectorOption
import com.kevinfreyap.jetspending.R
import com.kevinfreyap.jetspending.ui.theme.JetSpendingTheme
import com.kevinfreyap.jetspending.ui.theme.Theme

@Composable
fun ViewPeriodSelector(
    selectedOption: PeriodSelectorOption,
    onSelectOption: (PeriodSelectorOption) -> Unit,
    modifier: Modifier = Modifier
) {
    Card (
        shape = RoundedCornerShape(50),
        colors = CardDefaults.cardColors(
            containerColor = Theme.custom.cardColor
        ),
        modifier = modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min)
    ) {
        Row (
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxWidth()
        ) {

            PeriodSelectorOption.entries.forEachIndexed { index, option ->
                val selected = option == selectedOption
                val optionDisplayRes = when (option) {
                    PeriodSelectorOption.WEEKLY -> R.string.period_weekly
                    PeriodSelectorOption.MONTHLY -> R.string.period_monthly
                    PeriodSelectorOption.YEARLY -> R.string.period_yearly
                }

                Text(
                    text = stringResource(optionDisplayRes),
                    fontWeight = FontWeight.SemiBold,
                    style = MaterialTheme.typography.titleLarge,
                    textAlign = TextAlign.Center,
                    color = if (selected) MaterialTheme.colorScheme.onPrimary else Theme.custom.textColor,
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .clickable(
                            onClick = {
                                onSelectOption(option)
                            }
                        )
                        .then(
                            if (selected) {
                                Modifier
                                    .background(MaterialTheme.colorScheme.primary, RoundedCornerShape(50))
                            } else {
                                Modifier
                            }
                        )
                        .padding(
                            vertical = 16.dp
                        )
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
fun ViewPeriodSelectorPreview() {
    var selectedOption by remember { mutableStateOf(PeriodSelectorOption.WEEKLY) }

    JetSpendingTheme {
        ViewPeriodSelector(
            selectedOption = selectedOption,
            onSelectOption = {selectedOption = it}
        )
    }
}