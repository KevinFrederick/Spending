package com.kevinfreyap.jetspending.ui.screen.list.components

import android.content.res.Configuration.UI_MODE_NIGHT_NO
import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.kevinfreyap.domain.model.AppCurrency
import com.kevinfreyap.domain.model.TimeFilterOption
import com.kevinfreyap.jetspending.R
import com.kevinfreyap.jetspending.ui.components.ViewCustomDateDialog
import com.kevinfreyap.jetspending.ui.components.ViewDropdownField
import com.kevinfreyap.jetspending.ui.components.ViewPrimaryButton
import com.kevinfreyap.jetspending.ui.model.CategoryUI
import com.kevinfreyap.jetspending.ui.screen.list.action.FilterLocalAction
import com.kevinfreyap.jetspending.ui.screen.list.state.DatePickerTarget
import com.kevinfreyap.jetspending.ui.screen.list.state.FilterDateState
import com.kevinfreyap.jetspending.ui.screen.list.state.FilterState
import com.kevinfreyap.jetspending.ui.theme.JetSpendingTheme
import com.kevinfreyap.jetspending.ui.theme.Theme
import java.time.Instant

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilterBottomSheet(
    state: FilterState,
    categories: List<CategoryUI>,
    onDismiss: () -> Unit,
    onApply: (FilterState) -> Unit,
    modifier: Modifier = Modifier
) {
    var draftState by remember(state) { mutableStateOf(state) }
    var activeDatePicker by remember { mutableStateOf<DatePickerTarget?>(null) }

    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true
    )

    ModalBottomSheet(
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surface,
        onDismissRequest = onDismiss,
        modifier = modifier
    ) {
        FilterBottomSheetContent(
            state = draftState,
            categories = categories,
            onAction = { action ->
                when(action) {
                    is FilterLocalAction.OnSelectDateFilter -> {
                        draftState = draftState.copy(
                            filterDateState = draftState.filterDateState.copy(
                                selectedOption = action.filter
                            )
                        )
                    }
                    is FilterLocalAction.OnSelectCustomStartDate -> {
                        draftState = draftState.copy(
                            filterDateState = draftState.filterDateState.copy(
                                customStartDate = if (action.dateMillis != null) {
                                    Instant.ofEpochMilli(action.dateMillis)
                                } else null
                            )
                        )
                    }
                    is FilterLocalAction.OnSelectCustomEndDate -> {
                        draftState = draftState.copy(
                            filterDateState = draftState.filterDateState.copy(
                                customEndDate = if (action.dateMillis != null) {
                                    Instant.ofEpochMilli(action.dateMillis)
                                } else null
                            )
                        )
                    }
                    is FilterLocalAction.OnToggleDateDialog -> {
                        activeDatePicker = action.target
                    }
                    is FilterLocalAction.OnSelectType -> {
                        val currentCategory = draftState.filterCategory
                        val validCategory = when {
                            action.type == null -> currentCategory
                            currentCategory == null -> null
                            action.type in currentCategory.types -> currentCategory
                            else -> null
                        }

                        draftState = draftState.copy(
                            filterType = action.type,
                            filterCategory = validCategory
                        )
                    }
                    is FilterLocalAction.OnSelectCurrency -> {
                        draftState = draftState.copy(
                            filterCurrency = action.currency
                        )
                    }
                    is FilterLocalAction.OnSelectCategory -> {
                        draftState = draftState.copy(
                            filterCategory = action.category
                        )
                    }
                    FilterLocalAction.ApplyFilter -> {
                        activeDatePicker = null

                        if (
                            draftState.filterDateState.selectedOption == TimeFilterOption.PICK_DATE &&
                            draftState.filterDateState.customStartDate == null &&
                            draftState.filterDateState.customEndDate == null
                        ) {
                            draftState = draftState.copy(
                                filterDateState = FilterDateState()
                            )
                        }

                        onApply(draftState)
                    }
                    FilterLocalAction.ResetFilter -> {
                        draftState = FilterState()
                    }
                }
            }
        )
    }

    activeDatePicker?.let { target ->
        val nowMillis = remember { System.currentTimeMillis() }
        val startMillis = draftState.filterDateState.customStartDate?.toEpochMilli()
        val endMillis = draftState.filterDateState.customEndDate?.toEpochMilli()

        val (initialMillis, minMillis, maxMillis) = when (target) {
            DatePickerTarget.START -> Triple(
                startMillis,
                null,
                endMillis ?: nowMillis
            )
            DatePickerTarget.END -> Triple(
                endMillis,
                startMillis,
                nowMillis
            )
        }

        ViewCustomDateDialog(
            initialDateMillis = initialMillis,
            minDateMillis = minMillis,
            maxDateMillis = maxMillis,
            onDateSelected = { selectedMillis ->
                draftState = when(target) {
                    DatePickerTarget.START -> draftState.copy(
                        filterDateState = draftState.filterDateState.copy(
                            customStartDate = Instant.ofEpochMilli(selectedMillis)
                        )
                    )
                    DatePickerTarget.END -> draftState.copy(
                        filterDateState = draftState.filterDateState.copy(
                            customEndDate = Instant.ofEpochMilli(selectedMillis)
                        )
                    )
                }
            },
            onDismiss = {
                activeDatePicker = null
            }
        )
    }
}

@Composable
private fun FilterBottomSheetContent(
    state: FilterState,
    categories: List<CategoryUI>,
    onAction: (FilterLocalAction) -> Unit,
    modifier: Modifier = Modifier
) {
    val currencyOptions = remember { listOf(null) + AppCurrency.entries }

    val categoryOptions = remember(categories, state.filterType) {
        val filtered = when (val type = state.filterType) {
            null -> categories
            else -> categories.filter { type in it.types }
        }.sortedBy { it.sortOrder }

        listOf(null) + filtered
    }

    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .padding(
                top = 12.dp,
                bottom = 32.dp,
                start = 16.dp,
                end = 16.dp
            )
            .navigationBarsPadding()
    ) {
        Text(
            text = stringResource(R.string.section_header_filter_transaction),
            style = MaterialTheme.typography.headlineSmall,
            color = Theme.custom.primaryTextColor
        )

        FilterDateSection(
            state = state.filterDateState,
            onAction = onAction
        )

        FilterTypeSection(
            selectedType = state.filterType,
            onSelectType = {
                onAction(
                    FilterLocalAction.OnSelectType(it)
                )
            }
        )

        ViewDropdownField(
            options = currencyOptions,
            selectedOption = state.filterCurrency,
            onOptionSelected = {
                onAction(
                    FilterLocalAction.OnSelectCurrency(it)
                )
            },
            label = stringResource(R.string.label_currency),
            optionLabel = { it?.displayCodeName ?: stringResource(R.string.placeholder_all_currency) },
        )

        ViewDropdownField(
            options = categoryOptions,
            selectedOption = state.filterCategory,
            onOptionSelected = {
                onAction(
                    FilterLocalAction.OnSelectCategory(it)
                )
            },
            label = stringResource(R.string.label_category),
            optionLabel = { stringResource(it?.name ?: R.string.placeholder_all_category) }
        )

        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Reset
            ViewPrimaryButton(
                text = stringResource(R.string.btn_reset),
                onClick = {
                    onAction(
                        FilterLocalAction.ResetFilter
                    )
                },
                containerColor = MaterialTheme.colorScheme.surfaceContainer,
                contentColor = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .weight(1f)
            )

            // Apply
            ViewPrimaryButton(
                text = stringResource(R.string.btn_apply_filters),
                onClick = {
                    onAction(
                        FilterLocalAction.ApplyFilter
                    )
                },
                enabled = state.isValid,
                modifier = Modifier
                    .weight(1f)
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
private fun FilterBottomSheetPreview() {
    JetSpendingTheme {
        FilterBottomSheetContent(
            state = FilterState(),
            categories = emptyList(),
            onAction = {}
        )
    }
}