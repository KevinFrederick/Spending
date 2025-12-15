package com.kevinfreyap.jetspending.ui.screen.list

import android.content.res.Configuration
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.paging.PagingData
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.kevinfreyap.domain.model.TimeFilterOption
import com.kevinfreyap.domain.model.TransactionType
import com.kevinfreyap.jetspending.R
import com.kevinfreyap.jetspending.ui.components.BottomSheetDateRange
import com.kevinfreyap.jetspending.ui.components.BottomSheetFilter
import com.kevinfreyap.jetspending.ui.components.SearchBarWithFilter
import com.kevinfreyap.jetspending.ui.components.ViewTopBar
import com.kevinfreyap.jetspending.ui.components.ViewTransactionItem
import com.kevinfreyap.jetspending.ui.components.ViewTransactionItemPlaceholder
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
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import java.time.Instant

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionListScreen(
    onBackClick: () -> Unit,
    navigateToDetail: (String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: TransactionListViewModel = hiltViewModel()
) {
    val transactions = viewModel.transactions.collectAsLazyPagingItems()

    val query by viewModel.query.collectAsState()
    val filter by viewModel.filter.collectAsState()
    val filterState by viewModel.filterState.collectAsState()
    val uiState by viewModel.uiState.collectAsState()

    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true
    )
    val scope = rememberCoroutineScope()
    var activeSheet by remember { mutableStateOf<FilterBottomSheetType>(FilterBottomSheetType.None) }

    fun closeBottomSheet() {
        scope.launch {
            sheetState.hide()
        }.invokeOnCompletion {
            if (!sheetState.isVisible) {
                activeSheet = FilterBottomSheetType.None
            }
        }
    }

    val filterActions = remember(viewModel) {

        object : TransactionFilterAction {

            override fun onFilterOptionClicked(option: TimeFilterOption) {
                viewModel.onTimeFilterOptionClicked(option)
                if (option == TimeFilterOption.PICK_DATE){
                    viewModel.initializeDateRange()
                    activeSheet = FilterBottomSheetType.DateFilter
                }
            }

            override fun onFromDateSelected(millis: Long?) {
                viewModel.onFromDateSelected(millis)
            }

            override fun onToDateSelected(millis: Long?) {
                viewModel.onToDateSelected(millis)
            }

            override fun onSetSelectedDate() {
                viewModel.onSetDateRange()
                activeSheet = FilterBottomSheetType.Filter
            }

            override fun onResetSelectedDate() {
                viewModel.onResetDateRange()
            }

            override fun onNavigateToFilter() {
                viewModel.checkSelectedDate()
                activeSheet = FilterBottomSheetType.Filter
            }

            override fun onFromAmountChanged(amount: String) {
                viewModel.onFromRawAmountChange(amount)
            }

            override fun onToAmountChanged(amount: String) {
                viewModel.onToRawAmountChange(amount)
            }

            override fun onTypeChange(type: TransactionType) {
                viewModel.onTypeChange(type)
            }

            override fun onCategorySelected(categoryId: String) {
                viewModel.onCategorySelected(categoryId)
            }

            override fun onFromPositiveBtnClicked() {
                viewModel.onFromAmountPositiveBtnClicked()
            }

            override fun onToPositiveBtnClicked() {
                viewModel.onToAmountPositiveBtnClicked()
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
        transactions = transactions,
        searchQuery = query,
        sheetState = sheetState,
        activeSheet = activeSheet,
        onQueryChange = {

        },
        onFilterBtnClicked = {
            viewModel.initializeFilter()
            activeSheet = FilterBottomSheetType.Filter
        },
        onSelectSheet = { sheet ->
            activeSheet = sheet
        },
        navigateToDetail = navigateToDetail,
        onBackClick = onBackClick,
        transactionFilterState = filterState,
        transactionFilterAction = filterActions,
        uiState = uiState,
        hasActiveFilters = filter.hasActiveFilters,
        modifier = modifier,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionListContent(
    transactions: LazyPagingItems<TransactionsUi>,
    searchQuery: String,
    sheetState: SheetState,
    activeSheet: FilterBottomSheetType,
    onQueryChange: (String) -> Unit,
    onFilterBtnClicked: () -> Unit,
    onSelectSheet: (FilterBottomSheetType) -> Unit,
    navigateToDetail: (String) -> Unit,
    onBackClick: () -> Unit,
    transactionFilterState: TransactionFilterState,
    transactionFilterAction: TransactionFilterAction,
    uiState: UiState<Unit>,
    hasActiveFilters: Boolean,
    modifier: Modifier = Modifier
) {
    Scaffold(
        topBar = {
            ViewTopBar(
                title = stringResource(R.string.transactions),
                onBackClick = onBackClick
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

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier
                    .weight(1f)
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
                            )
                        }

                        null -> {
                            ViewTransactionItemPlaceholder()
                        }
                    }
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
                        if (targetState == FilterBottomSheetType.DateFilter) {
                            (slideInHorizontally { it } + fadeIn()) togetherWith
                            (slideOutHorizontally { -it } + fadeOut())
                        } else {
                            // Going "Back" to Main Filter
                            (slideInHorizontally { -it } + fadeIn()) togetherWith
                            (slideOutHorizontally { it } + fadeOut())
                        }
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
                transactionDateRaw = Instant.now()
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
                transactionDateRaw = Instant.now()
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
                transactionDateRaw = Instant.now()
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
                transactionDateRaw = Instant.now()
            ),
        ),
    )

    val pagingData = PagingData.from(sampleItems)

    // Turn into LazyPagingItems for preview
    val lazyPagingItems = flowOf(pagingData).collectAsLazyPagingItems()

    JetSpendingTheme {
        TransactionListContent(
            onBackClick = {},
            navigateToDetail = {},
            searchQuery = "",
            onFilterBtnClicked = {},
            onQueryChange = {},
            onSelectSheet = {},
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

                    override fun onFromDateSelected(millis: Long?) {}

                    override fun onToDateSelected(millis: Long?) {}

                    override fun onSetSelectedDate() {}

                    override fun onResetSelectedDate() {}

                    override fun onNavigateToFilter() {}

                    override fun onFromAmountChanged(amount: String) {}

                    override fun onToAmountChanged(amount: String) {}

                    override fun onTypeChange(type: TransactionType) {}

                    override fun onCategorySelected(categoryId: String) {}

                    override fun onFromPositiveBtnClicked() {}

                    override fun onToPositiveBtnClicked() {}

                    override fun onApplyFilter() {}

                    override fun onResetFilter() {}
                }
        )
    }
}