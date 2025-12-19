package com.kevinfreyap.jetspending.ui.screen.add_transaction

import android.content.res.Configuration
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.kevinfreyap.domain.error.Field
import com.kevinfreyap.domain.model.AppCurrency
import com.kevinfreyap.domain.model.TransactionType
import com.kevinfreyap.jetspending.R
import com.kevinfreyap.jetspending.ui.components.BottomSheetInputAmount
import com.kevinfreyap.jetspending.ui.components.ViewAmountCard
import com.kevinfreyap.jetspending.ui.components.ViewCategoryItem
import com.kevinfreyap.jetspending.ui.components.ViewCustomDialog
import com.kevinfreyap.jetspending.ui.components.ViewDatePickerField
import com.kevinfreyap.jetspending.ui.components.ViewErrorTooltip
import com.kevinfreyap.jetspending.ui.components.ViewTextField
import com.kevinfreyap.jetspending.ui.components.ViewTopBar
import com.kevinfreyap.jetspending.ui.components.ViewTypeSelector
import com.kevinfreyap.jetspending.ui.model.CategoryUI
import com.kevinfreyap.jetspending.ui.state.TransactionAction
import com.kevinfreyap.jetspending.ui.state.TransactionState
import com.kevinfreyap.jetspending.ui.state.UiState
import com.kevinfreyap.jetspending.ui.theme.Green500
import com.kevinfreyap.jetspending.ui.theme.JetSpendingTheme
import com.kevinfreyap.jetspending.ui.theme.Theme
import com.kevinfreyap.jetspending.utils.CurrencyVisualTransformation
import kotlinx.coroutines.delay
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneOffset
import kotlin.collections.forEach

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTransactionContent(
    currencyCode: AppCurrency,
    transactionState: TransactionState,
    transactionAction: TransactionAction,
    showSuccessDialog: Boolean,
    uiState: UiState<Unit>,
    amountSheetState: SheetState,
    showAmountSheet: Boolean,
    onBackClick: () -> Unit,
    onSelectCurrency: (AppCurrency) -> Unit,
    onShowAmountSheet: () -> Unit,
    onDismissAmountSheet: () -> Unit,
    modifier: Modifier = Modifier
) {
    val focusManager = LocalFocusManager.current

    val nameError = if (uiState is UiState.ValidationErrors){
        uiState.errors[Field.TRANSACTION_NAME]
    } else null

    val amountError = if (uiState is UiState.ValidationErrors){
        uiState.errors[Field.TRANSACTION_AMOUNT]
    } else null

    val categoryError = if (uiState is UiState.ValidationErrors){
        uiState.errors[Field.TRANSACTION_CATEGORY]
    } else null

    Scaffold(
        topBar = {
            ViewTopBar(
                title = stringResource(R.string.add_transaction),
                showActionButton = true,
                onBackClick = { onBackClick() },
                selectedCurrency = currencyCode,
                onSelectCurrency = onSelectCurrency,
                isLoading = uiState is UiState.Loading
            )
        }
    ) { innerPadding ->
        Column(
            modifier = modifier
                .verticalScroll(rememberScrollState())
                .padding(innerPadding)
                .padding(
                    top = 16.dp,
                    start = 16.dp,
                    end = 16.dp
                )
        ) {
            Text(
                text = stringResource(R.string.transaction_name),
                color = Theme.custom.textColor,
                style = MaterialTheme.typography.titleMedium
            )

            ViewTextField(
                value = transactionState.transactionName,
                onValueChange = transactionAction::onNameChange,
                isError = nameError != null,
                errorMessage = nameError?.let { stringResource(it) } ?: "",
                label = stringResource(R.string.transaction_name),
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Done
                )
            )

            Spacer(
                modifier = Modifier
                    .height(8.dp)
            )

            ViewAmountCard(
                onTransactionAmountClick = {
                    focusManager.clearFocus()
                    transactionAction.initializeAmount()
                    onShowAmountSheet()
                },
                transactionAmount = transactionState.transactionAmountDisplay,
                isError = amountError != null,
                errorMessage = amountError?.let { stringResource(it) } ?: "",
                cardTitle = stringResource(R.string.amount),
            )

            Spacer(
                modifier = Modifier
                    .height(16.dp)
            )

            ViewTypeSelector(
                selectedOption = transactionState.transactionType,
                onSelectOption = transactionAction::onSelectType,
            )

            Spacer(
                modifier = Modifier
                    .height(24.dp)
            )

            Box {
                if (categoryError != null) {
                    ViewErrorTooltip(
                        errorMessage = stringResource(categoryError),
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .offset(y = (-12).dp)
                    )
                }
                
                Column {
                    Text(
                        text = stringResource(R.string.category),
                        color = if (categoryError != null) MaterialTheme.colorScheme.error else Theme.custom.textColor,
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier
                            .padding(start = 8.dp)
                    )

                    Spacer(
                        modifier = Modifier
                            .height(16.dp)
                    )

                    FlowRow(
                        maxItemsInEachRow = 3,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp)
                    ) {
                        transactionState.transactionCategories.forEach { item ->
                            val isSelected = item.id == transactionState.transactionCategoryId

                            ViewCategoryItem(
                                categoryImage = item.iconRes,
                                categoryName = stringResource(item.name),
                                isSelected = isSelected,
                                modifier = Modifier
                                    .clickable(
                                        onClick = {
                                            focusManager.clearFocus()
                                            transactionAction.onSelectCategory(item.id)
                                        }
                                    )
                            )
                        }

                        if (transactionState.transactionCategories.size == 2 || (transactionState.transactionCategories.size % 3 == 2)) {
                            Spacer(
                                modifier.size(100.dp)
                            )
                        }
                    }
                }
            }

            Spacer(
                modifier = Modifier
                    .height(24.dp)
            )

            Text(
                text = stringResource(R.string.date),
                color = Theme.custom.textColor,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier
                    .padding(start = 8.dp)
            )

            Spacer(
                modifier = Modifier
                    .height(8.dp)
            )

            ViewDatePickerField(
                value = transactionState.transactionDateDisplay,
                rawValue = transactionState.transactionDate,
                earliestYear = LocalDate.now().minusYears(5).year,
                selectableDates = object : SelectableDates {
                    override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                        val dayToCheck = Instant.ofEpochMilli(utcTimeMillis)
                            .atZone(ZoneOffset.UTC)
                            .toLocalDate()

                        val today = LocalDate.now()
                        val startYear = today.minusYears(5)

                        return !dayToCheck.isBefore(startYear) && !dayToCheck.isAfter(today)
                    }
                },
                onDateSelected = transactionAction::onDateSelected,
                placeholder = ""
            )

            Spacer(
                modifier = Modifier
                    .height(32.dp)
            )

            Button (
                onClick = transactionAction::onSaveTransaction,
                colors = ButtonDefaults.elevatedButtonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                ),
                contentPadding = PaddingValues(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                Text(
                    text = stringResource(R.string.save_transaction),
                    color = MaterialTheme.colorScheme.onPrimary,
                    style = MaterialTheme.typography.titleLarge
                )
            }
        }

        if (showAmountSheet) {
            ModalBottomSheet(
                containerColor = MaterialTheme.colorScheme.background,
                onDismissRequest = onDismissAmountSheet,
                sheetState = amountSheetState
            ) {
                BottomSheetInputAmount(
                    amountInputSlot = {
                        ViewTextField(
                            value = transactionState.transactionAmountInput,
                            onValueChange = transactionAction::onAmountChange,
                            label = stringResource(R.string.amount),
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Decimal,
                                imeAction = ImeAction.Done
                            ),
                            visualTransformation = CurrencyVisualTransformation(currencyCode),
                            modifier = Modifier
                                .padding(
                                    top = 4.dp
                                )
                        )
                    },
                    onPositiveClick = transactionAction::onSetAmount,
                    onNegativeClick = onDismissAmountSheet,
                    currencyCode = currencyCode,
                    onSelectCurrency = onSelectCurrency
                )
            }
        }

        if (showSuccessDialog) {
            LaunchedEffect(Unit) {
                delay(2000)
                transactionAction.onDismissSuccessDialog()
            }

            ViewCustomDialog(
                onDismissRequest = transactionAction::onDismissSuccessDialog,
                icon = R.drawable.ic_check_circle_outline_24,
                iconColor = Green500,
                title = stringResource(R.string.success),
                message = stringResource(R.string.success_message_transaction_saved)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(
    showBackground = true,
    device = Devices.PIXEL_9_PRO,
    uiMode = Configuration.UI_MODE_NIGHT_YES
)
@Composable
fun AddTransactionContentPreview() {
    val listCategory = listOf(
        CategoryUI(
            id = "CAT_FOOD",
            name = R.string.category_food,
            sortOrder = 1,
            iconRes = R.drawable.ic_food_icon
        ),
        CategoryUI(
            id = "CAT_FOOD",
            name = R.string.category_food,
            sortOrder = 2,
            iconRes = R.drawable.ic_food_icon
        ),
        CategoryUI(
            id = "CAT_FOOD",
            name = R.string.category_food,
            sortOrder = 3,
            iconRes = R.drawable.ic_food_icon
        ),
        CategoryUI(
            id = "CAT_FOOD",
            name = R.string.category_food,
            sortOrder = 4,
            iconRes = R.drawable.ic_food_icon
        ),
        CategoryUI(
            id = "CAT_FOOD",
            name = R.string.category_food,
            sortOrder = 5,
            iconRes = R.drawable.ic_food_icon
        ),
    )
    JetSpendingTheme {
        AddTransactionContent(
            onBackClick = {},
            currencyCode = AppCurrency.IDR,
            showSuccessDialog = false,
            uiState = UiState.Idle,
            amountSheetState = rememberModalBottomSheetState(),
            showAmountSheet = false,
            onShowAmountSheet = {},
            onDismissAmountSheet = {},
            onSelectCurrency = {},
            transactionState = TransactionState(
                transactionAmountDisplay = "Rp 0",
                transactionCategories = listCategory,
                transactionDateDisplay = "Today, 16 December 2025"
            ),
            transactionAction = object : TransactionAction {
                override fun onNameChange(name: String) {}

                override fun onAmountChange(amount: String) {}

                override fun onSetAmount() {}

                override fun initializeAmount() {}

                override fun onSelectType(type: TransactionType) {}

                override fun onSelectCategory(categoryId: String) {}

                override fun onDateSelected(millis: Long?) {}

                override fun onSaveTransaction() {}

                override fun onDismissSuccessDialog() {}
            },
        )
    }
}