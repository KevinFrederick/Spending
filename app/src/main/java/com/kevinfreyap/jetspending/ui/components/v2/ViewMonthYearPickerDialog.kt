package com.kevinfreyap.jetspending.ui.components.v2

import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.kevinfreyap.jetspending.R
import com.kevinfreyap.jetspending.ui.theme.JetSpendingTheme
import com.kevinfreyap.jetspending.ui.theme.Theme
import java.time.LocalDate
import java.time.Month
import java.time.format.TextStyle
import java.util.Locale
import androidx.compose.ui.platform.LocalLocale
import kotlin.collections.toList

@Composable
fun ViewMonthYearPickerDialog(
    initialDate: LocalDate,
    onDismiss: () -> Unit,
    onReset: () -> Unit,
    modifier: Modifier = Modifier,
    onMonthYearSelected: (month: Int, year: Int) -> Unit = {_, _ -> },
    onYearSelected: (year: Int) -> Unit = {},
    isYearOnly: Boolean = false,
    maxDate: LocalDate = LocalDate.now(),
    locale: Locale = Locale.getDefault()
) {

    var selectedYear by rememberSaveable (initialDate.year) {
        mutableIntStateOf(initialDate.year)
    }

    val initialPageOffset = remember(initialDate.year, maxDate.year) {
        (maxDate.year - initialDate.year).coerceAtLeast(0) / 9
    }
    var yearPageOffset by rememberSaveable (initialDate.year) {
        mutableIntStateOf(initialPageOffset)
    }

    val pageEndYear = maxDate.year - (9 * yearPageOffset)
    val pageStartYear = pageEndYear - 8
    val yearList = remember(pageStartYear, pageEndYear) {
        (pageStartYear..pageEndYear)
            .toList()
    }

    val months = remember(locale) {
        Month.entries.map { it.getDisplayName(TextStyle.SHORT, locale) }
    }

    val options = if (isYearOnly) yearList.map { it.toString() } else months
    val isNextEnabled = if (isYearOnly) yearPageOffset > 0 else selectedYear < maxDate.year

    Dialog(
        onDismissRequest = onDismiss,
    ) {
        ViewMonthYearPickerContent(
            initialDate = initialDate,
            selectedYear = selectedYear,
            isYearOnly = isYearOnly,
            isNextEnabled = isNextEnabled,
            maxDate = maxDate,
            options = options,
            onDismiss = onDismiss,
            onReset = onReset,
            onNext = {
                if (isYearOnly) {
                    if (yearPageOffset > 0) {
                        yearPageOffset--
                    }
                } else {
                    selectedYear++
                }
            },
            onPrevious = {
                if (isYearOnly) {
                    yearPageOffset++
                } else {
                    selectedYear--
                }
            },
            onMonthYearSelected = onMonthYearSelected,
            onYearSelected = onYearSelected,
            modifier = modifier
        )
    }
}

@Composable
fun ViewMonthYearPickerContent(
    initialDate: LocalDate,
    selectedYear: Int,
    isYearOnly: Boolean,
    isNextEnabled: Boolean,
    maxDate: LocalDate,
    options: List<String>,
    onDismiss: () -> Unit,
    onReset: () -> Unit,
    onNext: () -> Unit,
    onPrevious: () -> Unit,
    modifier: Modifier = Modifier,
    onMonthYearSelected: (month: Int, year: Int) -> Unit = {_, _ -> },
    onYearSelected: (year: Int) -> Unit = {}
) {
    @StringRes val titleRes: Int
    val labelText: String

    if (isYearOnly) {
        titleRes = R.string.dialog_title_select_year
        labelText = "${options.first()} - ${options.last()}"
    } else {
        titleRes = R.string.dialog_title_select_month
        labelText = selectedYear.toString()
    }

    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        modifier = modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
        ) {
            Text(
                text = stringResource(titleRes),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold,
                color = Theme.custom.primaryTextColor
            )

            Spacer(Modifier.height(8.dp))

            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                IconButton(
                    onClick = onPrevious,
                    modifier = Modifier
                        .size(32.dp)
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_chevron_left),
                        contentDescription = null,
                        tint = Theme.custom.secondaryTextColor,
                        modifier = Modifier.size(16.dp)
                    )
                }

                Text(
                    text = labelText,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = Theme.custom.primaryTextColor
                )

                IconButton(
                    onClick = onNext,
                    enabled = isNextEnabled,
                    modifier = Modifier
                        .size(32.dp)
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_chevron_right),
                        contentDescription = null,
                        tint = if (isNextEnabled) {
                            Theme.custom.secondaryTextColor
                        } else {
                            Theme.custom.secondaryTextColor.copy(0.3f)
                        },
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // 3x4 Month or 3x3 Year Grid
            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                itemsIndexed(options) { index, optionName ->
                    var isSelected: Boolean
                    var isFuture: Boolean

                    if (isYearOnly) {
                        val yearNum = optionName.toInt()
                        isSelected = yearNum == selectedYear
                        isFuture = yearNum > maxDate.year
                    } else {
                        val monthNum = index + 1
                        isSelected = selectedYear == initialDate.year && monthNum == initialDate.monthValue
                        isFuture = selectedYear == maxDate.year && monthNum > maxDate.monthValue
                    }

                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(
                                if (isSelected) MaterialTheme.colorScheme.primary
                                else MaterialTheme.colorScheme.surfaceContainer
                            )
                            .clickable(enabled = !isFuture) {
                                if (isYearOnly) {
                                    onYearSelected(optionName.toInt())
                                } else {
                                    onMonthYearSelected(
                                        index + 1,
                                        selectedYear
                                    )
                                }

                                onDismiss()
                            }
                            .padding(vertical = 12.dp)
                    ) {
                        Text(
                            text = optionName,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                            color = when {
                                isSelected -> MaterialTheme.colorScheme.onPrimary
                                isFuture -> Theme.custom.secondaryTextColor.copy(0.3f)
                                else -> Theme.custom.primaryTextColor
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                horizontalArrangement = Arrangement.End,
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                TextButton(
                    onClick = {
                        onReset()
                        onDismiss()
                    },
                ) {
                    Text(
                        text = stringResource(R.string.reset),
                        style = MaterialTheme.typography.labelMedium,
                        color = Theme.custom.secondaryTextColor.copy(0.6f)
                    )
                }

                TextButton(
                    onClick = onDismiss,
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
}

@Preview
@Composable
fun ViewMonthYearPickerDialogPreview_Month() {
    JetSpendingTheme {
        ViewMonthYearPickerContent(
            initialDate = LocalDate.now(),
            isYearOnly = false,
            isNextEnabled = false,
            onMonthYearSelected = { _, _ -> },
            onDismiss = { },
            onReset = { },
            selectedYear = LocalDate.now().year,
            maxDate = LocalDate.now(),
            options = Month.entries.map { it.getDisplayName(TextStyle.SHORT, LocalLocale.current.platformLocale) },
            onNext = {  },
            onPrevious = {  },
        )
    }
}

@Preview
@Composable
fun ViewMonthYearPickerDialogPreview_Year() {
    JetSpendingTheme {
        val maxDate = LocalDate.now()

        val pageEndYear = maxDate.year - (9 * 0)
        val pageStartYear = pageEndYear - 8
        val yearList = remember(pageStartYear, pageEndYear) {
            (pageStartYear..pageEndYear)
                .map { it.toString() }
                .toList()
        }

        ViewMonthYearPickerContent(
            initialDate = LocalDate.now(),
            isYearOnly = true,
            isNextEnabled = false,
            onMonthYearSelected = { _, _ -> },
            onDismiss = { },
            onReset = { },
            selectedYear = LocalDate.now().year,
            maxDate = maxDate,
            options = yearList,
            onNext = {  },
            onPrevious = {  },
        )
    }
}