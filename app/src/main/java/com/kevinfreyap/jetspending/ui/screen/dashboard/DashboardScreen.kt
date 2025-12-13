package com.kevinfreyap.jetspending.ui.screen.dashboard

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.kevinfreyap.jetspending.R
import com.kevinfreyap.jetspending.ui.components.RecentTransactions
import com.kevinfreyap.jetspending.ui.components.SummaryCard
import com.kevinfreyap.jetspending.ui.components.ViewDateSelector
import com.kevinfreyap.jetspending.ui.components.ViewTopBar
import com.kevinfreyap.jetspending.ui.model.TransactionItemUi
import com.kevinfreyap.jetspending.ui.theme.Blue500
import com.kevinfreyap.jetspending.ui.theme.Green500
import com.kevinfreyap.jetspending.ui.theme.JetSpendingTheme
import java.time.Instant

@Composable
fun DashboardScreen(
    navigateToAddTransaction: () -> Unit,
    navigateToTransactionList: () -> Unit,
    navigateToDetail: (String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: DashboardViewModel = hiltViewModel()
) {
    val latestTransactions by viewModel.latestTransactions.collectAsState()

    DashboardContent(
        navigateToAddTransaction = navigateToAddTransaction,
        navigateToTransactionList = navigateToTransactionList,
        navigateToDetail = navigateToDetail,
        latestTransactions = latestTransactions,
        modifier = modifier
    )
}

@Composable
fun DashboardContent(
    navigateToAddTransaction: () -> Unit,
    navigateToTransactionList: () -> Unit,
    navigateToDetail: (String) -> Unit,
    latestTransactions: List<TransactionItemUi>,
    modifier: Modifier = Modifier
) {
    Scaffold(
        topBar = {
            ViewTopBar(
                title = stringResource(R.string.dashboard),
                onCurrencyIconClick = {}
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = navigateToAddTransaction,
                containerColor = Blue500,
                contentColor = Color.White,
                shape = RoundedCornerShape(16.dp)
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_add_24),
                    contentDescription = stringResource(R.string.add_transaction),
                )
            }
        },
    ) { innerPadding ->
        Column(
            modifier = modifier
                .padding(innerPadding)
                .padding(
                    top = 8.dp,
                    bottom = 16.dp,
                    start = 16.dp,
                    end = 16.dp
                )
                .verticalScroll(rememberScrollState())
        ) {
            SummaryCard(
                totalBalance = "Rp 100.000",
                dateSelectorSlot = {
                    ViewDateSelector(
                        centerText = "December",
                        onPreviousClick = {  },
                        onPreviousBtnEnabled = true,
                        onNextClick = {  },
                        onNextBtnEnabled = true,
                    )
                }
            )

            Spacer(
                modifier = Modifier.height(16.dp)
            )

            RecentTransactions(
                transactions = latestTransactions,
                navigateToTransactionList = navigateToTransactionList,
                navigateToDetail = navigateToDetail,
            )

            Spacer(
                modifier = Modifier.height(32.dp)
            )
        }
    }
}

@Preview(
    showBackground = true,
    device = Devices.PIXEL_9_PRO
)
@Composable
fun DashboardContentPreview(){
    JetSpendingTheme {
        DashboardContent(
            navigateToAddTransaction = {},
            navigateToTransactionList = {},
            navigateToDetail = {},
            latestTransactions = listOf(
                TransactionItemUi(
                    transactionId = "1",
                    transactionName = "Salary",
                    transactionAmount = "+ Rp 1.000.000",
                    transactionDate = "24 November 2025",
                    transactionDateRaw = Instant.now(),
                    transactionTypeBackground = Green500,
                    transactionCategoryIcon = R.drawable.ic_salary_icon,
                ),
                TransactionItemUi(
                    transactionId = "2",
                    transactionName = "Salary",
                    transactionAmount = "+ Rp 1.000.000",
                    transactionDate = "24 November 2025",
                    transactionDateRaw = Instant.now(),
                    transactionTypeBackground = Green500,
                    transactionCategoryIcon = R.drawable.ic_salary_icon,
                ),
                TransactionItemUi(
                    transactionId = "3",
                    transactionName = "Salary",
                    transactionAmount = "+ Rp 1.000.000",
                    transactionDate = "24 November 2025",
                    transactionDateRaw = Instant.now(),
                    transactionTypeBackground = Green500,
                    transactionCategoryIcon = R.drawable.ic_salary_icon,
                )
            )
        )
    }
}