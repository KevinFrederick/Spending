package com.kevinfreyap.jetspending.ui.screen.list

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.paging.PagingData
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.kevinfreyap.jetspending.R
import com.kevinfreyap.jetspending.ui.components.SearchBarWithFilter
import com.kevinfreyap.jetspending.ui.components.ViewTopBar
import com.kevinfreyap.jetspending.ui.components.ViewTransactionItem
import com.kevinfreyap.jetspending.ui.components.ViewTransactionItemPlaceholder
import com.kevinfreyap.jetspending.ui.model.TransactionItemUi
import com.kevinfreyap.jetspending.ui.model.TransactionsUi
import com.kevinfreyap.jetspending.ui.theme.Green500
import com.kevinfreyap.jetspending.ui.theme.JetSpendingTheme
import com.kevinfreyap.jetspending.ui.theme.Orange700
import com.kevinfreyap.jetspending.ui.theme.Theme
import kotlinx.coroutines.flow.flowOf
import java.time.Instant

@Composable
fun TransactionListScreen(
    onBackClick: () -> Unit,
    navigateToDetail: (String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: TransactionListViewModel = hiltViewModel()
) {
    val query by viewModel.query.collectAsState()
    val transactions = viewModel.transactions.collectAsLazyPagingItems()
    
    TransactionListContent(
        onBackClick = onBackClick,
        navigateToDetail = navigateToDetail,
        transactions = transactions,
        onFilterClick = {

        },
        searchQuery = query,
        onQueryChange = {

        },
        modifier = modifier,
    )
}

@Composable
fun TransactionListContent(
    onBackClick: () -> Unit,
    navigateToDetail: (String) -> Unit,
    transactions: LazyPagingItems<TransactionsUi>,
    searchQuery: String,
    onQueryChange: (String) -> Unit,
    onFilterClick: () -> Unit,
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
                onFilterClick = onFilterClick
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

    }
}

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
            onQueryChange = {},
            onFilterClick = {},
            transactions = lazyPagingItems,
        )
    }
}