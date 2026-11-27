package com.kevinfreyap.jetspending.ui.screen.list

import android.content.res.Configuration.UI_MODE_NIGHT_NO
import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.LoadState
import androidx.paging.LoadStates
import androidx.paging.PagingData
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.kevinfreyap.domain.model.TransactionType
import com.kevinfreyap.jetspending.R
import com.kevinfreyap.jetspending.ui.action.TransactionListAction
import com.kevinfreyap.jetspending.ui.components.ViewTopBar
import com.kevinfreyap.jetspending.ui.components.ViewPageStatus
import com.kevinfreyap.jetspending.ui.components.ViewPrimaryButton
import com.kevinfreyap.jetspending.ui.components.ViewSearchBar
import com.kevinfreyap.jetspending.ui.components.ViewTransactionItem
import com.kevinfreyap.jetspending.ui.components.ViewTransactionItemPlaceholder
import com.kevinfreyap.jetspending.ui.model.TransactionsUI
import com.kevinfreyap.jetspending.ui.navigation.TransactionListNavigation
import com.kevinfreyap.jetspending.ui.screen.list.components.FilterBottomSheet
import com.kevinfreyap.jetspending.ui.screen.list.state.FilterState
import com.kevinfreyap.jetspending.ui.state.TransactionItemState
import com.kevinfreyap.jetspending.ui.state.TransactionListState
import com.kevinfreyap.jetspending.ui.theme.JetSpendingTheme
import com.kevinfreyap.jetspending.ui.theme.Theme
import com.kevinfreyap.jetspending.utils.shimmerEffect
import kotlinx.coroutines.flow.flowOf

@Composable
fun TransactionListScreen(
    onNavigate: (TransactionListNavigation) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: TransactionListViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val transactions = viewModel.transactions.collectAsLazyPagingItems()

    TransactionListContent(
        state = state,
        transactions = transactions,
        onAction = viewModel::onAction,
        onNavigate = onNavigate,
        modifier = modifier,
    )
}

@Composable
private fun TransactionListContent(
    state: TransactionListState,
    transactions: LazyPagingItems<TransactionsUI>,
    onAction: (TransactionListAction) -> Unit,
    onNavigate: (TransactionListNavigation) -> Unit,
    modifier: Modifier = Modifier
) {
    val isRefreshLoading = transactions.loadState.refresh is LoadState.Loading
    val isRefreshError = transactions.loadState.refresh is LoadState.Error
    val isAppendLoading = transactions.loadState.append is LoadState.Loading

    Scaffold(
        topBar = {
            ViewTopBar(
                title = stringResource(R.string.screen_transactions),
                isLoading = isRefreshLoading,
                actionButton = {
                    IconButton(
                        onClick = {
                            onNavigate(
                                TransactionListNavigation.NavigateToAddTransaction
                            )
                        }
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.ic_add_24),
                            contentDescription = stringResource(R.string.cd_add_transaction)
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        Column (
            modifier = modifier
                .padding(innerPadding)
                .padding(
                    start = 24.dp,
                    end = 24.dp,
                    bottom = 16.dp
                )
                .fillMaxSize()
        ) {
            ViewSearchBar(
                query = state.query,
                placeholder = stringResource(R.string.placeholder_search_transactions),
                onQueryChanged = {
                    onAction(
                        TransactionListAction.OnQueryChange(it)
                    )
                },
                trailingIcon = {
                    IconButton(
                        onClick = {
                            onAction(
                                TransactionListAction.OnToggleFilterSheet(
                                    isVisible = true
                                )
                            )
                        },
                        modifier = Modifier
                            .size(42.dp)
                    ) {
                        BadgedBox(
                            badge = {
                                if (state.filterState.hasActiveFilter) {
                                    Badge(
                                        containerColor = Theme.custom.statusWarning,
                                        modifier = Modifier
                                            .size(6.dp)
                                            .offset(
                                                x = 2.dp,
                                                y = (-2).dp
                                            )
                                    )
                                }
                            }
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.ic_tune_24),
                                contentDescription = stringResource(R.string.cd_transaction_filter),
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                },
            )

            Spacer(modifier = Modifier.height(16.dp))

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f)
            ) {
                when {
                    // Initial shimmer loading
                    isRefreshLoading && transactions.itemCount == 0 -> {
                        ShimmerLoading()
                    }

                    // Error
                    isRefreshError && transactions.itemCount == 0 -> {
                        val error = (transactions.loadState.refresh as LoadState.Error).error
                        StatusError(
                            errorMessage = error.message,
                            action = { transactions.retry() }
                        )
                    }

                    // No Result
                    transactions.loadState.refresh is LoadState.NotLoading && transactions.itemCount == 0 -> {
                        StatusEmpty(
                            state = state,
                            onAction = onAction,
                            onNavigateToAddTransaction = {
                                onNavigate(
                                    TransactionListNavigation.NavigateToAddTransaction
                                )
                            }
                        )
                    }

                    // Success
                    else -> {
                        StatusSuccess(
                            transactions = transactions,
                            isAppendLoading = isAppendLoading,
                            onNavigateToDetail = { transactionId ->
                                onNavigate(
                                    TransactionListNavigation.NavigateToTransactionDetail(
                                        transactionId
                                    )
                                )
                            }
                        )
                    }
                }
            }
        }
    }

    if(state.isFilterSheetVisible) {
        FilterBottomSheet(
            state = state.filterState,
            categories = state.categories,
            onDismiss = {
                onAction(
                    TransactionListAction.OnToggleFilterSheet(
                        isVisible = false
                    )
                )
            },
            onApply = { filter ->
                onAction(
                    TransactionListAction.OnApplyFilter(filter)
                )
                onAction(
                    TransactionListAction.OnToggleFilterSheet(
                        isVisible = false
                    )
                )
            },
        )
    }
}

@Composable
private fun ShimmerLoading() {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        repeat(2) { index ->
            Text(
                text = "",
                style = MaterialTheme.typography.titleSmall,
                color = Color.Transparent,
                modifier = Modifier
                    .clearAndSetSemantics {}
                    .fillMaxWidth(0.3f)
                    .padding(
                        top = if (index > 0) 16.dp else 4.dp,
                    )
                    .clip(RoundedCornerShape(50))
                    .shimmerEffect(
                        shimmerColor = Theme.custom.shimmerColor
                    )
            )

            repeat(5) {
                ViewTransactionItemPlaceholder()
            }
        }

    }
}

@Composable
private fun StatusSuccess(
    transactions: LazyPagingItems<TransactionsUI>,
    onNavigateToDetail: (String) -> Unit,
    isAppendLoading: Boolean,
) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(4.dp),
        modifier = Modifier
            .fillMaxSize()
    ) {
        items(
            count = transactions.itemCount,
            key = { index ->
                when(
                    val item = transactions[index]
                ) {
                    is TransactionsUI.Header -> "header_${item.header}_$index"
                    is TransactionsUI.Item -> "item_${item.transaction.transactionId}"
                    null -> "placeholder_$index"
                }
            },
            contentType = { index ->
                when(transactions[index]) {
                    is TransactionsUI.Header -> "header"
                    is TransactionsUI.Item -> "item"
                    null -> "placeholder"
                }
            }
        ) { index ->
            when(
                val item = transactions[index]
            ) {
                is TransactionsUI.Header -> {
                    Text(
                        text = item.header,
                        style = MaterialTheme.typography.titleSmall,
                        color = Theme.custom.primaryTextColor,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(
                                top = if (index > 0) 16.dp else 4.dp,
                            )
                    )
                }
                is TransactionsUI.Item -> {
                    ViewTransactionItem(
                        state = item.transaction,
                        onClick = onNavigateToDetail
                    )
                }
                null -> {
                    ViewTransactionItemPlaceholder()
                }
            }
        }

        if (isAppendLoading) {
            item {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}

@Composable
private fun StatusError(
    errorMessage: String?,
    action: () -> Unit,
) {
    ViewPageStatus(
        title = stringResource(R.string.error_something_went_wrong),
        message = errorMessage ?: stringResource(R.string.error_failed_to_load_transactions),
        icon = {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(64.dp)
                    .background(
                        color = MaterialTheme.colorScheme.errorContainer,
                        shape = CircleShape
                    )
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_priority_high_24),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.error,
                    modifier = Modifier
                        .size(32.dp)
                )
            }
        },
        actionBtn = {
            ViewPrimaryButton(
                text = stringResource(R.string.btn_retry),
                onClick = action,
                icon = {
                    Icon(
                        painter = painterResource(R.drawable.ic_refresh_24),
                        contentDescription = null,
                    )
                },
                modifier = Modifier
                    .fillMaxWidth(0.5f)
            )
        }
    )
}

@Composable
private fun StatusEmpty(
    state: TransactionListState,
    onAction: (TransactionListAction) -> Unit,
    onNavigateToAddTransaction: () -> Unit
) {
    @DrawableRes val icon: Int
    @StringRes val title: Int
    @StringRes val message: Int
    @StringRes val btnText: Int
    @DrawableRes val btnIcon: Int

    val action: () -> Unit

    when {
        state.query.isNotBlank() -> {
            icon = R.drawable.ic_search_off_24
            title = R.string.search_no_result
            message = R.string.search_message_query_no_match
            btnText = R.string.btn_clear_query
            btnIcon = R.drawable.ic_backspace_24
            action = {
                onAction(TransactionListAction.OnQueryChange(""))
            }
        }
        state.filterState.hasActiveFilter -> {
            icon = R.drawable.ic_search_off_24
            title = R.string.search_no_result
            message = R.string.search_message_filter_no_match
            btnText = R.string.btn_clear_filter
            btnIcon = R.drawable.ic_filter_list_off_24
            action = {
                onAction(TransactionListAction.OnApplyFilter(FilterState()))
            }
        }
        else -> {
            icon = R.drawable.app_icon_wallet
            title = R.string.empty_transactions
            message = R.string.empty_transactions_message
            btnText = R.string.btn_add_transaction
            btnIcon = R.drawable.ic_add_24
            action = onNavigateToAddTransaction
        }
    }

    ViewPageStatus(
        title = stringResource(title),
        message = stringResource(message),
        icon = {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(64.dp)
                    .background(
                        color = Theme.custom.highlightContainer,
                        shape = CircleShape
                    )
            ) {
                Icon(
                    painter = painterResource(icon),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier
                        .size(32.dp)
                )
            }
        },
        actionBtn = {
            ViewPrimaryButton(
                text = stringResource(btnText),
                onClick = action,
                icon = {
                    Icon(
                        painter = painterResource(btnIcon),
                        contentDescription = null,
                        modifier = Modifier
                            .size(16.dp)
                    )
                },
                modifier = Modifier
            )
        }
    )
}

@Preview(
    showBackground = true,
    device = Devices.PIXEL_9_PRO,
    backgroundColor = 0xFFFAFCFB,
    uiMode = UI_MODE_NIGHT_NO
)
@Preview(
    showBackground = true,
    device = Devices.PIXEL_9_PRO,
    backgroundColor = 0xFF121214,
    uiMode = UI_MODE_NIGHT_YES
)
@Composable
private fun TransactionListContentPreview() {
    // 1. Create Sample Preview Items (Headers + Items)
    val sampleItems = listOf(
        TransactionsUI.Header("December 2026"),
        TransactionsUI.Item(
            TransactionItemState(
                transactionId = "1",
                transactionType = TransactionType.SPENDING,
                transactionCategoryIcon = R.drawable.ic_food_icon,
                transactionName = "Lunch",
                transactionCategoryName = R.string.category_food,
                transactionDate = "18 Dec 2026",
                transactionMonthYear = "November 2026",
                transactionOriginalAmount = "70.00",
                transactionConvertedAmount = null,
                transactionSelectedCurrency = "USD",
                transactionOriginalCurrency = "USD"
            )
        ),
        TransactionsUI.Item(
            TransactionItemState(
                transactionId = "2",
                transactionType = TransactionType.INCOME,
                transactionCategoryIcon = R.drawable.ic_salary_icon,
                transactionName = "Salary",
                transactionCategoryName = R.string.category_salary,
                transactionDate = "15 Dec 2026",
                transactionMonthYear = "November 2026",
                transactionOriginalAmount = "3.500.00",
                transactionConvertedAmount = null,
                transactionSelectedCurrency = "USD",
                transactionOriginalCurrency = "USD"
            )
        ),
        TransactionsUI.Header("November 2026"),
        TransactionsUI.Item(
            TransactionItemState(
                transactionId = "3",
                transactionType = TransactionType.SPENDING,
                transactionCategoryIcon = R.drawable.ic_shopping_icon,
                transactionName = "Shopping",
                transactionCategoryName = R.string.category_shopping,
                transactionDate = "28 Nov 2026",
                transactionMonthYear = "November 2026",
                transactionOriginalAmount = "150.00",
                transactionConvertedAmount = null,
                transactionSelectedCurrency = "USD",
                transactionOriginalCurrency = "USD"
            )
        )
    )

    // 2. Define Mock LoadStates
    val loadStates = LoadStates(
        refresh = LoadState.NotLoading(endOfPaginationReached = true),
        prepend = LoadState.NotLoading(endOfPaginationReached = true),
        append = LoadState.NotLoading(endOfPaginationReached = true)
    )

    // 3. Create static PagingData & collect as LazyPagingItems
    val pagingData = PagingData.from(
        data = sampleItems,
        sourceLoadStates = loadStates
    )
    val lazyPagingItems = flowOf(pagingData).collectAsLazyPagingItems()

    JetSpendingTheme {
        TransactionListContent(
            state = TransactionListState(),
            transactions = lazyPagingItems,
            onAction = {},
            onNavigate = {}
        )
    }
}

@Preview(
    showBackground = true,
    device = Devices.PIXEL_9_PRO,
    backgroundColor = 0xFFFAFCFB,
    uiMode = UI_MODE_NIGHT_NO
)
@Preview(
    showBackground = true,
    device = Devices.PIXEL_9_PRO,
    backgroundColor = 0xFF121214,
    uiMode = UI_MODE_NIGHT_YES
)
@Composable
private fun TransactionListContentPreview_Loading() {
    val loadStates = LoadStates(
        refresh = LoadState.Loading,
        prepend = LoadState.NotLoading(endOfPaginationReached = true),
        append = LoadState.NotLoading(endOfPaginationReached = true)
    )

    val pagingData = PagingData.from(
        data = emptyList<TransactionsUI>(),
        sourceLoadStates = loadStates
    )

    val lazyPagingItems = flowOf(pagingData).collectAsLazyPagingItems()

    JetSpendingTheme {
        TransactionListContent(
            state = TransactionListState(),
            transactions = lazyPagingItems,
            onAction = {},
            onNavigate = {}
        )
    }
}

@Preview(
    showBackground = true,
    device = Devices.PIXEL_9_PRO,
    backgroundColor = 0xFFFAFCFB,
    uiMode = UI_MODE_NIGHT_NO
)
@Preview(
    showBackground = true,
    device = Devices.PIXEL_9_PRO,
    backgroundColor = 0xFF121214,
    uiMode = UI_MODE_NIGHT_YES
)
@Composable
private fun TransactionListContentPreview_NoResult() {
    val loadStates = LoadStates(
        refresh = LoadState.NotLoading(endOfPaginationReached = true),
        prepend = LoadState.NotLoading(endOfPaginationReached = true),
        append = LoadState.NotLoading(endOfPaginationReached = true)
    )

    val pagingData = PagingData.from(
        data = emptyList<TransactionsUI>(),
        sourceLoadStates = loadStates
    )

    val lazyPagingItems = flowOf(pagingData).collectAsLazyPagingItems()

    JetSpendingTheme {
        TransactionListContent(
            state = TransactionListState(
                query = "a"
            ),
            transactions = lazyPagingItems,
            onAction = {},
            onNavigate = {}
        )
    }
}