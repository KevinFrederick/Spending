package com.kevinfreyap.jetspending.ui.screen.list

import android.content.res.Configuration
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.paging.LoadState
import androidx.paging.LoadStates
import androidx.paging.PagingData
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.kevinfreyap.domain.model.AppCurrency
import com.kevinfreyap.domain.model.TimeFilterOption
import com.kevinfreyap.domain.model.TransactionType
import com.kevinfreyap.jetspending.R
import com.kevinfreyap.jetspending.ui.components.BottomSheetDateRange
import com.kevinfreyap.jetspending.ui.components.BottomSheetFilter
import com.kevinfreyap.jetspending.ui.components.BottomSheetInputAmount
import com.kevinfreyap.jetspending.ui.components.SearchBarWithFilter
import com.kevinfreyap.jetspending.ui.components.ViewPageError
import com.kevinfreyap.jetspending.ui.components.ViewTextField
import com.kevinfreyap.jetspending.ui.components.ViewTopBar
import com.kevinfreyap.jetspending.ui.components.ViewTransactionItem
import com.kevinfreyap.jetspending.ui.components.ViewTransactionItemPlaceholder
import com.kevinfreyap.jetspending.ui.main.MainViewModel
import com.kevinfreyap.jetspending.ui.model.FilterBottomSheetType
import com.kevinfreyap.jetspending.ui.model.FilterTimeOptionUI
import com.kevinfreyap.jetspending.ui.model.TransactionItemUi
import com.kevinfreyap.jetspending.ui.model.TransactionsUi
import com.kevinfreyap.jetspending.ui.state.TransactionFilterAction
import com.kevinfreyap.jetspending.ui.state.TransactionFilterState
import com.kevinfreyap.jetspending.ui.state.UiState
import com.kevinfreyap.jetspending.ui.theme.Green500
import com.kevinfreyap.jetspending.ui.theme.JetSpendingTheme
import com.kevinfreyap.jetspending.ui.theme.Orange700
import com.kevinfreyap.jetspending.ui.theme.Theme
import com.kevinfreyap.jetspending.utils.CurrencyVisualTransformation
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import java.time.Instant

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionListScreen(
    onBackClick: () -> Unit,
    navigateToDetail: (String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: TransactionListViewModel = hiltViewModel(),
    mainViewModel: MainViewModel = hiltViewModel()
) {
    val transactions = viewModel.transactions.collectAsLazyPagingItems()

    val currencyCode by viewModel.currencyCode.collectAsState()
    val query by viewModel.query.collectAsState()
    val filter by viewModel.filter.collectAsState()
    val filterState by viewModel.filterUiState.collectAsState()
    val uiState by viewModel.uiState.collectAsState()

    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true
    )
    val scope = rememberCoroutineScope()
    val activeSheet by viewModel.activeSheetContent.collectAsState()

    fun closeBottomSheet() {
        scope.launch {
            sheetState.hide()
        }.invokeOnCompletion {
            if (!sheetState.isVisible) {
                viewModel.navigateTo(FilterBottomSheetType.None)
            }
        }
    }

    val filterActions = remember(viewModel) {

        object : TransactionFilterAction {

            override fun onFilterOptionClicked(option: TimeFilterOption) {
                viewModel.onTimeFilterOptionClicked(option)
                if (option == TimeFilterOption.PICK_DATE){
                    viewModel.navigateTo(FilterBottomSheetType.DateFilter)
                }
            }

            override fun onDateSelected(millis: Long?, isFrom: Boolean) {
                viewModel.onDateSelected(millis, isFrom)
            }

            override fun onSetSelectedDate() {
                viewModel.onSetDate()
                viewModel.navigateTo(FilterBottomSheetType.Filter)
            }

            override fun onResetSelectedDate() {
                viewModel.onResetDate()
            }

            override fun onNavigateToFilter() {
                viewModel.checkSelectedDate()
                viewModel.navigateTo(FilterBottomSheetType.Filter)
            }

            override fun onNavigateToAmount(isFrom: Boolean) {
                if (isFrom) {
                    viewModel.navigateTo(FilterBottomSheetType.AmountFrom)
                } else {
                    viewModel.navigateTo(FilterBottomSheetType.AmountTo)
                }
            }

            override fun onAmountCardClicked(isFrom: Boolean) {
                viewModel.prepareAmountInput(isFrom)
            }

            override fun onAmountChanged(amount: String, isFrom: Boolean) {
                viewModel.onAmountChange(amount, isFrom)
            }

            override fun onTypeChange(type: TransactionType) {
                viewModel.onTypeChange(type)
            }

            override fun onCategorySelected(categoryId: String) {
                viewModel.onCategoryChange(categoryId)
            }

            override fun onSetAmount(isFrom: Boolean) {
                viewModel.onSetAmount(isFrom)
                viewModel.navigateTo(FilterBottomSheetType.Filter)
            }

            override fun onApplyFilter() {
                viewModel.applyFilter()
                closeBottomSheet()
            }

            override fun onResetFilter() {
                viewModel.resetFilter()
            }
        }
    }


    TransactionListContent(
        currencyCode = currencyCode,
        transactionFilterState = filterState,
        transactionFilterAction = filterActions,
        transactions = transactions,
        searchQuery = query,
        sheetState = sheetState,
        activeSheet = activeSheet,
        onSelectCurrency = {
            mainViewModel.onSelectCurrency(it)
        },
        onQueryChange = {
            viewModel.onQueryChange(it)
        },
        onFilterBtnClicked = {
            viewModel.initializeFilter()
            viewModel.navigateTo(FilterBottomSheetType.Filter)
        },
        onSelectSheet = { sheet ->
            viewModel.navigateTo(sheet)
        },
        onCheckRate = {
            mainViewModel.onRateMissing(it)
        },
        navigateToDetail = navigateToDetail,
        onBackClick = onBackClick,
        uiState = uiState,
        hasActiveFilters = filter.hasActiveFilters,
        modifier = modifier,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionListContent(
    currencyCode: AppCurrency,
    transactionFilterState: TransactionFilterState,
    transactionFilterAction: TransactionFilterAction,
    transactions: LazyPagingItems<TransactionsUi>,
    searchQuery: String,
    sheetState: SheetState,
    activeSheet: FilterBottomSheetType,
    onSelectCurrency: (AppCurrency) -> Unit,
    onQueryChange: (String) -> Unit,
    onFilterBtnClicked: () -> Unit,
    onSelectSheet: (FilterBottomSheetType) -> Unit,
    navigateToDetail: (String) -> Unit,
    onBackClick: () -> Unit,
    onCheckRate: (Instant) -> Unit,
    uiState: UiState<Unit>,
    hasActiveFilters: Boolean,
    modifier: Modifier = Modifier
) {
    Scaffold(
        topBar = {
            ViewTopBar(
                title = stringResource(R.string.transactions),
                onBackClick = onBackClick,
                showActionButton = true,
                selectedCurrency = currencyCode,
                onSelectCurrency = onSelectCurrency,
                isLoading = transactions.loadState.refresh is LoadState.Loading
            )
        }
    ) { innerPadding ->
        Column (
            modifier = modifier
                .padding(innerPadding)
                .padding(
                    start = 16.dp,
                    end = 16.dp,
                    bottom = 16.dp
                )
                .fillMaxSize()
        ) {
            SearchBarWithFilter(
                searchQuery = searchQuery,
                onQueryChange = onQueryChange,
                onFilterClick = onFilterBtnClicked,
                showBadge = hasActiveFilters
            )

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f)
            ) {
                when (transactions.loadState.refresh) {
                    is LoadState.Error -> {
                        ViewPageError(
                            icon = R.drawable.ic_error_outline_24,
                            text = stringResource(R.string.error_loading_data),
                            modifier = Modifier
                                .align(Alignment.Center)
                        )
                    }

                    is LoadState.NotLoading -> {
                        if (transactions.itemCount == 0 && (hasActiveFilters || searchQuery.isNotBlank())){
                            ViewPageError(
                                icon = R.drawable.ic_search_24,
                                text = stringResource(R.string.error_transaction_not_found),
                                modifier = Modifier
                                    .align(Alignment.Center)
                            )
                        }

                        else if (transactions.itemCount == 0) {
                            ViewPageError(
                                icon = R.drawable.ic_no_transactions,
                                text = stringResource(R.string.error_no_transaction),
                                modifier = Modifier
                                    .align(Alignment.Center)
                            )
                        }

                        else {
                            LazyColumn(
                                verticalArrangement = Arrangement.spacedBy(8.dp),
                            ) {
                                items(
                                    count = transactions.itemCount,
                                    key = { index ->
                                        val item = transactions[index]
                                        when (item) {
                                            is TransactionsUi.Header -> item.header
                                            is TransactionsUi.Item -> item.transaction.transactionId
                                            null -> index
                                        }
                                    },
                                    contentType = { index ->
                                        transactions[index]?.javaClass
                                    }
                                ) { index ->
                                    val item = transactions[index]
                                    when (item) {
                                        is TransactionsUi.Header -> {
                                            Spacer(
                                                modifier = Modifier
                                                    .height(8.dp)
                                            )

                                            Text(
                                                text = item.header,
                                                style = MaterialTheme.typography.titleLarge,
                                                color = Theme.custom.textColor,
                                            )
                                        }

                                        is TransactionsUi.Item -> {
                                            ViewTransactionItem(
                                                transaction = item.transaction,
                                                navigateToDetail = {
                                                    navigateToDetail(item.transaction.transactionId)
                                                },
                                                isNestedCard = false,
                                                onCheckRate = onCheckRate
                                            )
                                        }

                                        null -> {
                                            ViewTransactionItemPlaceholder()
                                        }
                                    }
                                }
                            }
                        }
                    }

                    LoadState.Loading -> {}
                }
            }
        }

        if (activeSheet != FilterBottomSheetType.None) {
            ModalBottomSheet(
                containerColor = MaterialTheme.colorScheme.background,
                onDismissRequest = {
                    onSelectSheet(FilterBottomSheetType.None)
                },
                sheetState = sheetState
            ) {
                AnimatedContent(
                    targetState = activeSheet,
                    label = "Sheet Navigation",
                    transitionSpec = {
                        (slideInVertically { it } + fadeIn()) togetherWith
                        (slideOutVertically { it } + fadeOut())
                    }
                ) { targetSheet ->
                    Box (
                        modifier = Modifier
                            .animateContentSize()
                    ) {
                        when (targetSheet) {
                            FilterBottomSheetType.Filter -> {
                                BottomSheetFilter(
                                    transactionFilterState = transactionFilterState,
                                    transactionFilterAction = transactionFilterAction,
                                    uiState = uiState
                                )
                            }

                            FilterBottomSheetType.DateFilter -> {
                                BottomSheetDateRange(
                                    transactionFilterState = transactionFilterState,
                                    transactionFilterAction = transactionFilterAction
                                )
                            }

                            FilterBottomSheetType.AmountFrom -> {
                                BottomSheetInputAmount(
                                    amountInputSlot = {
                                        ViewTextField(
                                            value = transactionFilterState.fromAmountInput,
                                            onValueChange = { amount ->
                                                transactionFilterAction.onAmountChanged(amount = amount, isFrom = true)
                                            },
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
                                    onPositiveClick = {
                                        transactionFilterAction.onSetAmount(isFrom = true)
                                    },
                                    onNegativeClick = {
                                        transactionFilterAction.onNavigateToFilter()
                                    },
                                    onBackButtonClick = {
                                        transactionFilterAction.onNavigateToFilter()
                                    },
                                    currencyCode = currencyCode
                                )
                            }

                            FilterBottomSheetType.AmountTo -> {
                                BottomSheetInputAmount(
                                    amountInputSlot = {
                                        ViewTextField(
                                            value = transactionFilterState.toAmountInput,
                                            onValueChange = { amount ->
                                                transactionFilterAction.onAmountChanged(amount = amount, isFrom = false)
                                            },
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
                                    onPositiveClick = {
                                        transactionFilterAction.onSetAmount(isFrom = false)
                                    },
                                    onNegativeClick = {
                                        transactionFilterAction.onNavigateToFilter()
                                    },
                                    onBackButtonClick = {
                                        transactionFilterAction.onNavigateToFilter()
                                    },
                                    currencyCode = currencyCode
                                )
                            }

                            FilterBottomSheetType.None -> {}
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(
    showBackground = true,
    device = Devices.PIXEL_9_PRO,
    uiMode = Configuration.UI_MODE_NIGHT_NO
)
@Composable
fun TransactionListContentPreview() {
    val sampleItems = listOf(
        TransactionsUi.Header("December 2025"),
        TransactionsUi.Item(
            TransactionItemUi(
                transactionId = "1",
                transactionName = "Salary",
                transactionTypeBackground = Green500,
                transactionCategoryIcon = R.drawable.ic_salary_icon,
                transactionAmount = "+ Rp 1.000.000",
                transactionDate = "24 Oct 2025",
                transactionDateRaw = Instant.now(),
                isConversionPending = false
            ),
        ),
        TransactionsUi.Item(
            TransactionItemUi(
                transactionId = "2",
                transactionName = "Salary",
                transactionTypeBackground = Green500,
                transactionCategoryIcon = R.drawable.ic_salary_icon,
                transactionAmount = "+ Rp 1.000.000",
                transactionDate = "24 Oct 2025",
                transactionDateRaw = Instant.now(),
                isConversionPending = true
            ),
        ),
        TransactionsUi.Header("November 2025"),
        TransactionsUi.Item(
            TransactionItemUi(
                transactionId = "3",
                transactionName = "Salary",
                transactionTypeBackground = Green500,
                transactionCategoryIcon = R.drawable.ic_salary_icon,
                transactionAmount = "+ Rp 1.000.000",
                transactionDate = "24 Oct 2025",
                transactionDateRaw = Instant.now(),
                isConversionPending = false
            ),
        ),
        TransactionsUi.Item(
            TransactionItemUi(
                transactionId = "4",
                transactionName = "Salary",
                transactionTypeBackground = Orange700,
                transactionCategoryIcon = R.drawable.ic_salary_icon,
                transactionAmount = "+ Rp 1.000.000",
                transactionDate = "24 Oct 2025",
                transactionDateRaw = Instant.now(),
                isConversionPending = true
            ),
        ),
    )

    val loadStates = LoadStates(
        refresh = LoadState.NotLoading(endOfPaginationReached = true),
        prepend = LoadState.NotLoading(endOfPaginationReached = true),
        append = LoadState.NotLoading(endOfPaginationReached = true)
    )

    val pagingData = PagingData.from(
        data = sampleItems,
        sourceLoadStates = loadStates
    )

    // Turn into LazyPagingItems for preview
    val lazyPagingItems = flowOf(pagingData).collectAsLazyPagingItems()

    JetSpendingTheme {
        TransactionListContent(
            currencyCode = AppCurrency.IDR,
            onSelectCurrency = {},
            onBackClick = {},
            navigateToDetail = {},
            searchQuery = "",
            onFilterBtnClicked = {},
            onQueryChange = {},
            onSelectSheet = {},
            onCheckRate = {},
            transactions = lazyPagingItems,
            sheetState = rememberModalBottomSheetState(),
            activeSheet = FilterBottomSheetType.None,
            uiState = UiState.Idle,
            hasActiveFilters = true,
            transactionFilterState = TransactionFilterState(
                timeFilter = TimeFilterOption.LAST_7_DAYS,
                timeFilterOptions = listOf(
                    FilterTimeOptionUI(TimeFilterOption.LAST_7_DAYS, R.string.last_7_days),
                    FilterTimeOptionUI(TimeFilterOption.THIS_MONTH, R.string.this_month),
                    FilterTimeOptionUI(TimeFilterOption.PICK_DATE, R.string.pick_date)
                ),
                displayFromAmount = "Rp 0",
                displayToAmount = "Rp 0",
                fromAmountInput = "",
                toAmountInput = ""
            ),
            transactionFilterAction =
                object : TransactionFilterAction {
                    override fun onFilterOptionClicked(option: TimeFilterOption) {}

                    override fun onDateSelected(millis: Long?, isFrom: Boolean) {}

                    override fun onSetSelectedDate() {}

                    override fun onResetSelectedDate() {}

                    override fun onNavigateToFilter() {}

                    override fun onNavigateToAmount(isFrom: Boolean) {}

                    override fun onAmountCardClicked(isFrom: Boolean) {}

                    override fun onAmountChanged(amount: String, isFrom: Boolean) {}

                    override fun onTypeChange(type: TransactionType) {}

                    override fun onCategorySelected(categoryId: String) {}

                    override fun onSetAmount(isFrom: Boolean) {}

                    override fun onApplyFilter() {}

                    override fun onResetFilter() {}
                }
        )
    }
}