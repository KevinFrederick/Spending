package com.kevinfreyap.jetspending.ui.screen.list.components

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import com.kevinfreyap.domain.model.TimeFilterOption
import com.kevinfreyap.jetspending.R
import com.kevinfreyap.jetspending.ui.components.v2.ViewChipOption
import com.kevinfreyap.jetspending.ui.components.v2.ViewTextInputField
import com.kevinfreyap.jetspending.ui.helper.filter.toUINameRes
import com.kevinfreyap.jetspending.ui.screen.list.action.FilterLocalAction
import com.kevinfreyap.jetspending.ui.screen.list.state.DatePickerTarget
import com.kevinfreyap.jetspending.ui.screen.list.state.FilterDateState
import com.kevinfreyap.jetspending.ui.theme.JetSpendingTheme
import com.kevinfreyap.jetspending.ui.theme.Theme
import java.time.Instant

@Composable
fun FilterDateSection(
    state: FilterDateState,
    onAction: (FilterLocalAction) -> Unit,
    modifier: Modifier = Modifier
) {
    @StringRes val customText: Int
    @DrawableRes val customIcon: Int
    val customMessageColor: Color
    if (state.isCustomRangeInvalid) {
        customText = R.string.warning_custom_date_range_invalid
        customIcon = R.drawable.ic_warning_amber_24
        customMessageColor = Theme.custom.statusWarning
    } else {
        customText = R.string.hint_custom_date_filter
        customIcon = R.drawable.ic_info_24
        customMessageColor = Theme.custom.secondaryTextColor.copy(0.6f)
    }

    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = modifier
            .fillMaxWidth()
    ) {
        Text(
            text = stringResource(R.string.filter_date_range),
            style = MaterialTheme.typography.labelMedium,
            color = Theme.custom.primaryTextColor
        )

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier
                .padding(
                    horizontal = 2.dp
                )
        ) {
            items(
                items = TimeFilterOption.chipOptions,
                key = { it.name }
            ) { option ->
                val isSelected = option == state.selectedOption

                ViewChipOption(
                    text = stringResource(option.toUINameRes()),
                    isSelected = isSelected,
                    onClick = {
                        onAction(
                            FilterLocalAction.OnSelectDateFilter(
                                if (isSelected) {
                                    TimeFilterOption.ALL_TIME
                                } else {
                                    option
                                }
                            )
                        )
                    }
                )
            }
        }

        AnimatedVisibility(
            visible = state.selectedOption == TimeFilterOption.PICK_DATE,
            enter = fadeIn() + expandVertically(),
            exit = fadeOut() + shrinkVertically()
        ) {
            Column{
                Spacer(Modifier.height(4.dp))

                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Start Date
                    ViewTextInputField(
                        value = state.customStartDateText ?: stringResource(R.string.placeholder_select_date),
                        label = stringResource(R.string.from),
                        onValueChange = {},
                        readOnly = true,
                        isPlaceholder = state.customStartDateText == null,
                        onClick = {
                            onAction(
                                FilterLocalAction.OnToggleDateDialog(
                                    DatePickerTarget.START
                                )
                            )
                        },
                        trailingIcon = {
                            if(!state.customStartDateText.isNullOrBlank()) {
                                IconButton(
                                    onClick = {
                                        onAction(
                                            FilterLocalAction.OnSelectCustomStartDate(null)
                                        )
                                    },
                                    modifier = Modifier
                                        .size(32.dp)
                                ) {
                                    Icon(
                                        painter = painterResource(R.drawable.ic_close_24),
                                        contentDescription = stringResource(R.string.clear_date),
                                        tint = Theme.custom.secondaryTextColor,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            } else {
                                Icon(
                                    painter = painterResource(R.drawable.ic_calendar_month_24),
                                    contentDescription = null,
                                    tint = Theme.custom.secondaryTextColor,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        },
                        modifier = Modifier
                            .weight(1f)
                    )

                    // End Date
                    ViewTextInputField(
                        value = state.customEndDateText ?: stringResource(R.string.placeholder_select_date),
                        label = stringResource(R.string.to),
                        onValueChange = {},
                        readOnly = true,
                        isPlaceholder = state.customEndDateText == null,
                        onClick = {
                            onAction(
                                FilterLocalAction.OnToggleDateDialog(
                                    DatePickerTarget.END
                                )
                            )
                        },
                        trailingIcon = {
                            if(!state.customEndDateText.isNullOrBlank()) {
                                IconButton(
                                    onClick = {
                                        onAction(
                                            FilterLocalAction.OnSelectCustomEndDate(null)
                                        )
                                    },
                                    modifier = Modifier
                                        .size(32.dp)
                                ) {
                                    Icon(
                                        painter = painterResource(R.drawable.ic_close_24),
                                        contentDescription = stringResource(R.string.clear_date),
                                        tint = Theme.custom.secondaryTextColor,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            } else {
                                Icon(
                                    painter = painterResource(R.drawable.ic_calendar_month_24),
                                    contentDescription = null,
                                    tint = Theme.custom.secondaryTextColor,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        },
                        modifier = Modifier
                            .weight(1f)
                    )
                }
                
                Spacer(Modifier.height(4.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        painter = painterResource(customIcon),
                        contentDescription = null,
                        tint = customMessageColor,
                        modifier = Modifier
                            .size(12.dp)
                    )

                    Text(
                        text = stringResource(customText),
                        style = MaterialTheme.typography.labelSmall,
                        color = customMessageColor
                    )
                }

            }
        }
    }
}

@Preview (
    showBackground = true
)
@Composable
fun FilterDateSectionPreview() {
    JetSpendingTheme {
        FilterDateSection(
            state = FilterDateState(
                selectedOption = TimeFilterOption.PICK_DATE,
                customStartDate = Instant.now()
            ),
            onAction = {}
        )
    }
}