package com.kevinfreyap.jetspending.ui.screen.transaction_form

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.repeatOnLifecycle
import com.kevinfreyap.domain.error.Field
import com.kevinfreyap.domain.model.TransactionType
import com.kevinfreyap.jetspending.R
import com.kevinfreyap.jetspending.ui.action.TransactionFormAction
import com.kevinfreyap.jetspending.ui.components.v2.ViewCustomDateDialog
import com.kevinfreyap.jetspending.ui.components.v2.ViewTopBar
import com.kevinfreyap.jetspending.ui.components.v2.ViewAppDialog
import com.kevinfreyap.jetspending.ui.components.v2.ViewPrimaryButton
import com.kevinfreyap.jetspending.ui.components.v2.ViewSelectionBar
import com.kevinfreyap.jetspending.ui.components.v2.ViewTextInputField
import com.kevinfreyap.jetspending.ui.model.DialogType
import com.kevinfreyap.jetspending.ui.navigation.TransactionFormNavigation
import com.kevinfreyap.jetspending.ui.screen.transaction_form.components.AmountEntrySection
import com.kevinfreyap.jetspending.ui.screen.transaction_form.components.AmountEntrySectionPlaceholder
import com.kevinfreyap.jetspending.ui.screen.transaction_form.components.CategoryBottomSheet
import com.kevinfreyap.jetspending.ui.screen.transaction_form.components.CategorySelectorCard
import com.kevinfreyap.jetspending.ui.state.TransactionFormState
import com.kevinfreyap.jetspending.ui.state.UiState
import com.kevinfreyap.jetspending.ui.theme.JetSpendingTheme
import com.kevinfreyap.jetspending.ui.theme.Theme
import com.kevinfreyap.jetspending.utils.UiEvent

@Composable
fun TransactionFormScreen(
    onNavigate: (TransactionFormNavigation) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: TransactionFormViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    val state by viewModel.state.collectAsStateWithLifecycle()

    val handleBackAttempt = {
        if (state.hasUnsavedChanges) {
            viewModel.onAction(
                TransactionFormAction.TransactionOverlayAction.OnToggleDiscardDialog(
                    isVisible = true
                )
            )
        } else {
            onNavigate(TransactionFormNavigation.NavigateUp)
        }
    }

    LaunchedEffect(viewModel.uiEvent, lifecycleOwner) {
        lifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
            viewModel.uiEvent.collect { event ->
                when(event) {
                    is UiEvent.Navigate -> {
                        onNavigate(event.destination)
                    }
                    is UiEvent.ShowToast -> {
                        Toast.makeText(
                            context,
                            event.asString(context),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }
    }

    BackHandler(enabled = state.hasUnsavedChanges) {
        viewModel.onAction(
            TransactionFormAction.TransactionOverlayAction.OnToggleDiscardDialog(
                isVisible = true
            )
        )
    }

    TransactionFormContent(
        state = state,
        onAction = viewModel::onAction,
        onBackAttempt = handleBackAttempt,
        modifier = modifier
    )
}

@Composable
fun TransactionFormContent(
    state: TransactionFormState,
    onAction: (TransactionFormAction) -> Unit,
    onBackAttempt: () -> Unit,
    modifier: Modifier = Modifier
) {
    val focusManager = LocalFocusManager.current

    val titleRes = if (state.isEditMode) R.string.edit_transaction else R.string.add_transaction
    val buttonTextRes = if (state.isEditMode) R.string.update_transaction else R.string.add_transaction

    val nameError = if (state.uiState is UiState.ValidationErrors){
        state.uiState.errors[Field.TRANSACTION_NAME]
    } else null

    val amountError = if (state.uiState is UiState.ValidationErrors){
        state.uiState.errors[Field.TRANSACTION_AMOUNT]
    } else null

    val categoryError = if (state.uiState is UiState.ValidationErrors){
        state.uiState.errors[Field.TRANSACTION_CATEGORY]
    } else null

    val notesError = if (state.uiState is UiState.ValidationErrors) {
        state.uiState.errors[Field.TRANSACTION_NOTES]
    } else null

    Scaffold(
        topBar = {
            ViewTopBar(
                title = stringResource(titleRes),
                onBackClick = onBackAttempt,
                isLoading = false
            )
        },
        bottomBar = {
            ViewPrimaryButton(
                text = stringResource(buttonTextRes),
                onClick = {
                    focusManager.clearFocus()
                    onAction(TransactionFormAction.SubmitTransaction)
                },
                enabled = state.hasUnsavedChanges,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        bottom = 32.dp,
                        start = 24.dp,
                        end = 24.dp
                    )
                    .navigationBarsPadding()
            )
        }
    ) { innerPadding ->
        Column (
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
                .consumeWindowInsets(innerPadding)
                .imePadding()
                .pointerInput(Unit) {
                    detectTapGestures(
                        onTap = {
                            focusManager.clearFocus()
                        }
                    )
                }
                .verticalScroll(rememberScrollState())
                .padding(
                    top = 12.dp,
                    bottom = 20.dp,
                    start = 24.dp,
                    end = 24.dp
                )
        ) {
            ViewSelectionBar(
                options = TransactionType.entries,
                selectedOption = state.transactionType,
                onOptionSelected = {
                    onAction(
                        TransactionFormAction.TransactionMainAction.OnSelectTransactionType(
                            it
                        )
                    )
                },
                optionLabel = {
                    stringResource(
                        when(it) {
                            TransactionType.SPENDING -> R.string.expense
                            TransactionType.INCOME -> R.string.income
                        }
                    )
                }
            )

            if (state.isInitialLoading) {
                AmountEntrySectionPlaceholder()
            } else {
                AmountEntrySection(
                    state = state.amountEntry,
                    onAmountChange = {
                        onAction(
                            TransactionFormAction.TransactionMainAction.OnAmountChange(
                                it
                            )
                        )
                    },
                    onCurrencySelected = {
                        onAction(
                            TransactionFormAction.TransactionMainAction.OnCurrencySelected(
                                it
                            )
                        )
                    },
                    isError = amountError != null,
                    errorMessage = amountError?.let { stringResource(it) } ?: "",
                )
            }

            CategorySelectorCard(
                selectedCategory = state.selectedCategory,
                onClick = {
                    onAction(
                        TransactionFormAction.TransactionOverlayAction.OnToggleCategoryBottomSheet(
                            isVisible = true
                        )
                    )
                },
                isError = categoryError != null,
                errorMessage = categoryError?.let { stringResource(it) } ?: ""
            )

            ViewTextInputField(
                value = state.name,
                label = stringResource(R.string.transaction_name),
                onValueChange = {
                    onAction(
                        TransactionFormAction.TransactionMainAction.OnNameChange(
                            it
                        )
                    )
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Next
                ),
                isError = nameError != null,
                errorMessage = nameError?.let { stringResource(it) } ?: "",
            )

            ViewTextInputField(
                value = state.notes ?: "",
                label = stringResource(R.string.transaction_notes_optional),
                onValueChange = {
                    onAction(
                        TransactionFormAction.TransactionMainAction.OnNotesChange(
                            it
                        )
                    )
                },
                singleLine = false,
                minLines = 3,
                maxLines = 5,
                isError = notesError != null,
                errorMessage = notesError?.let { stringResource(it) } ?: ""
            )

            ViewTextInputField(
                value = state.formattedDate,
                label = stringResource(R.string.date),
                onValueChange = {},
                onClick = {
                    focusManager.clearFocus()
                    onAction(
                        TransactionFormAction.TransactionOverlayAction.OnToggleDateDialog(
                            isVisible = true
                        )
                    )
                },
                trailingIcon = {
                    Icon(
                        painter = painterResource(R.drawable.ic_calendar_month_24),
                        contentDescription = null,
                        tint = Theme.custom.secondaryTextColor,
                        modifier = Modifier.size(20.dp)
                    )
                }
            )
        }
    }

    if (state.overlayState.isCategorySheetVisible) {
        CategoryBottomSheet(
            categories = state.categories.sortedBy { it.sortOrder },
            selectedCategory = state.selectedCategory,
            onSelectCategory = {
                onAction(
                    TransactionFormAction.TransactionOverlayAction.OnToggleCategoryBottomSheet(
                        isVisible = false
                    )
                )
                onAction(
                    TransactionFormAction.TransactionMainAction.OnCategorySelected(
                        it
                    )
                )
            },
            onDismiss = {
                onAction(
                    TransactionFormAction.TransactionOverlayAction.OnToggleCategoryBottomSheet(
                        isVisible = false
                    )
                )
            }
        )
    }

    if (state.overlayState.isDateDialogVisible) {
        ViewCustomDateDialog(
            initialDateMillis = state.date.toEpochMilli(),
            onDateSelected = { selectedMillis ->
                onAction(
                    TransactionFormAction.TransactionMainAction.OnDateSelected(
                        selectedMillis
                    )
                )
            },
            onDismiss = {
                onAction(
                    TransactionFormAction.TransactionOverlayAction.OnToggleDateDialog(
                        isVisible = false
                    )
                )
            }
        )
    }

    if (state.overlayState.isDiscardDialogVisible) {
        ViewAppDialog(
            dialogType = DialogType.WARNING,
            title = stringResource(R.string.dialog_discard_changes),
            message = stringResource(R.string.description_discard_change),
            confirmText = stringResource(R.string.discard),
            onConfirm = {
                onAction(
                    TransactionFormAction.TransactionOverlayAction.OnConfirmDiscard
                )
            },
            dismissText = stringResource(R.string.btn_keep_editing),
            onDismissRequest = {
                onAction(
                    TransactionFormAction.TransactionOverlayAction.OnToggleDiscardDialog(
                        isVisible = false
                    )
                )
            },
        )
    }
}

@Preview
@Composable
fun TransactionFormScreenPreview() {
    JetSpendingTheme {
        TransactionFormContent(
            state = TransactionFormState(
                isInitialLoading = false
            ),
            onAction = {},
            onBackAttempt = {}
        )
    }
}