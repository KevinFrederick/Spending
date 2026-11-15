package com.kevinfreyap.jetspending.ui.screen.dashboard

import android.content.res.Configuration
import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.kevinfreyap.domain.model.TransactionType
import com.kevinfreyap.jetspending.R
import com.kevinfreyap.jetspending.ui.components.ViewTopBar
import com.kevinfreyap.jetspending.ui.components.v2.ViewCurrencyCard
import com.kevinfreyap.jetspending.ui.components.v2.ViewCurrencyCardPlaceholder
import com.kevinfreyap.jetspending.ui.components.v2.ViewPrimaryButton
import com.kevinfreyap.jetspending.ui.components.v2.ViewPrimaryButtonPlaceholder
import com.kevinfreyap.jetspending.ui.helper.date.DayGreeting
import com.kevinfreyap.jetspending.ui.navigation.DashboardNavigation
import com.kevinfreyap.jetspending.ui.screen.dashboard.components.MonthlySummary
import com.kevinfreyap.jetspending.ui.screen.dashboard.components.MonthlySummaryPlaceholder
import com.kevinfreyap.jetspending.ui.screen.dashboard.components.RecentTransactionSection
import com.kevinfreyap.jetspending.ui.screen.dashboard.components.RecentTransactionSectionPlaceholder
import com.kevinfreyap.jetspending.ui.state.DashboardState
import com.kevinfreyap.jetspending.ui.state.MonthlySummaryState
import com.kevinfreyap.jetspending.ui.state.SummaryState
import com.kevinfreyap.jetspending.ui.state.TransactionItemState
import com.kevinfreyap.jetspending.ui.state.UiState
import com.kevinfreyap.jetspending.ui.theme.JetSpendingTheme
import com.kevinfreyap.jetspending.ui.theme.Theme
import com.kevinfreyap.jetspending.utils.shimmerEffect

@Composable
fun DashboardScreen(
    onNavigate: (DashboardNavigation) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: DashboardViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsState()

    DashboardContent(
        state = state,
        onNavigate = onNavigate,
        modifier = modifier
    )
}

@Composable
fun DashboardContent(
    state: UiState<DashboardState>,
    onNavigate: (DashboardNavigation) -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        topBar = {
            ViewTopBar(
                title = stringResource(R.string.overview),
            )
        }
    ) { innerPadding ->
        Column(
            verticalArrangement = Arrangement.spacedBy(20.dp),
            modifier = modifier
                .padding(innerPadding)
                .padding(
                    top = 8.dp,
                    bottom = 16.dp,
                    start = 24.dp,
                    end = 24.dp
                )
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            when(state) {
                UiState.Idle -> {}
                UiState.Loading -> DashboardPlaceholder()
                is UiState.Success -> DashboardSuccess(
                    state = state.data,
                    onNavigate = onNavigate
                )
                is UiState.ValidationErrors -> {}
                is UiState.Failure -> {}
            }
        }
    }
}

@Composable
fun DashboardSuccess(
    state: DashboardState,
    onNavigate: (DashboardNavigation) -> Unit
) {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Text(
            text = stringResource(state.greetings.greetingRes, state.userName),
            style = MaterialTheme.typography.bodyMedium,
            color = Theme.custom.secondaryTextColor
        )

        Text(
            text = state.date,
            style = MaterialTheme.typography.bodySmall,
            color = Theme.custom.secondaryTextColor
        )
    }

    ViewCurrencyCard(
        topLabel = {
            Text(
                text = stringResource(R.string.total_balance),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.primary
            )
        },
        centerValue = {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Text(
                    text = state.totalBalance,
                    style = MaterialTheme.typography.displaySmall,
                    color = Theme.custom.primaryTextColor,
                    modifier = Modifier.alignByBaseline()
                )

                Text(
                    text = state.currency,
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.alignByBaseline()
                )
            }
        },
        bottomInformation = {
            AnimatedContent(
                targetState = state.isRatesMissing,
                label = "BalanceSubtitleTransition"
            ) { missingRates ->
                if (missingRates) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.ic_warning_amber_24),
                            contentDescription = null,
                            tint = Theme.custom.warning,
                            modifier = Modifier
                                .size(16.dp)
                                .offset(y = -(1).dp)
                        )
                        Text(
                            text = stringResource(R.string.warning_rates_missing),
                            style = MaterialTheme.typography.bodySmall,
                            color = Theme.custom.warning,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                    }
                } else {
                    Text(
                        text = stringResource(R.string.subtitle_message_balance),
                        style = MaterialTheme.typography.bodySmall,
                        color = Theme.custom.secondaryTextColor,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        },
        containerColor = Theme.custom.highlightContainer
    )

    MonthlySummary(
        state = state.monthlySummary
    )

    RecentTransactionSection(
        transactions = state.recentTransactions,
        onTransactionClicked = { transactionId ->
            onNavigate(DashboardNavigation.NavigateToTransactionDetail(transactionId))
        },
        onSeeAllTransactionClicked = {
            onNavigate(DashboardNavigation.NavigateToTransactionList)
        }
    )

    ViewPrimaryButton(
        text = stringResource(R.string.add_transaction),
        onClick = {
            onNavigate(DashboardNavigation.NavigateToAddTransaction)
        },
        icon = {
            Icon(
                painter = painterResource(R.drawable.ic_add_24),
                contentDescription = null
            )
        },
        modifier = Modifier
            .fillMaxWidth()
    )
}

@Composable
private fun DashboardPlaceholder() {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Text(
            text = "",
            style = MaterialTheme.typography.bodyMedium,
            color = Color.Transparent,
            modifier = Modifier
                .clearAndSetSemantics {}
                .fillMaxWidth(0.5f)
                .clip(RoundedCornerShape(50))
                .shimmerEffect(
                    shimmerColor = Theme.custom.shimmerColor
                )
        )

        Text(
            text = "",
            style = MaterialTheme.typography.bodySmall,
            color = Color.Transparent,
            modifier = Modifier
                .clearAndSetSemantics {}
                .fillMaxWidth(0.5f)
                .clip(RoundedCornerShape(50))
                .shimmerEffect(
                    shimmerColor = Theme.custom.shimmerColor
                )
        )
    }

    ViewCurrencyCardPlaceholder()
    MonthlySummaryPlaceholder()
    RecentTransactionSectionPlaceholder()
    ViewPrimaryButtonPlaceholder()
}

@Preview(
    showBackground = true,
    device = Devices.PIXEL_9_PRO,
    uiMode = Configuration.UI_MODE_NIGHT_NO
)
@Composable
fun DashboardContentPreview(){
    JetSpendingTheme {
        DashboardContent(
            state = UiState.Success(
                DashboardState(
                    userName = "Alex",
                    greetings = DayGreeting.MORNING,
                    date = "September, 2026",
                    totalBalance = "3,160.00",
                    isRatesMissing = false,
                    currency = "USD",
                    monthlySummary = MonthlySummaryState(
                        incomeSummary = SummaryState(
                            transactionType = TransactionType.INCOME,
                            value = "1,800.00",
                            currency = "USD",
                            month = "This Month"
                        ),
                        spendingSummary = SummaryState(
                            transactionType = TransactionType.SPENDING,
                            value = "900.00",
                            currency = "USD",
                            month = "This Month"
                        )
                    ),
                    recentTransactions = listOf(
                        TransactionItemState(
                            transactionId = "1",
                            transactionType = TransactionType.SPENDING,
                            transactionCategoryIcon = R.drawable.ic_food_icon,
                            transactionName = "Lunch",
                            transactionCategoryName = R.string.category_food,
                            transactionDate = "18 Sep 2026",
                            transactionOriginalAmount = "70.00",
                            transactionConvertedAmount = null,
                            transactionSelectedCurrency = "USD",
                            transactionOriginalCurrency = "IDR"
                        ),
                        TransactionItemState(
                            transactionId = "2",
                            transactionType = TransactionType.INCOME,
                            transactionCategoryIcon = R.drawable.ic_food_icon,
                            transactionName = "Salary",
                            transactionCategoryName = R.string.category_incoming,
                            transactionDate = "18 Sep 2026",
                            transactionOriginalAmount = "710.00",
                            transactionConvertedAmount = null,
                            transactionSelectedCurrency = "USD",
                            transactionOriginalCurrency = "IDR"
                        ),
                        TransactionItemState(
                            transactionId = "1",
                            transactionType = TransactionType.SPENDING,
                            transactionCategoryIcon = R.drawable.ic_food_icon,
                            transactionName = "Lunch",
                            transactionCategoryName = R.string.category_food,
                            transactionDate = "18 Sep 2026",
                            transactionOriginalAmount = "70.00",
                            transactionConvertedAmount = null,
                            transactionSelectedCurrency = "USD",
                            transactionOriginalCurrency = "IDR"
                        ),
                    )
                )
            ),
            onNavigate = {}
        )
    }
}

@Preview(
    showBackground = true,
    device = Devices.PIXEL_9_PRO,
    uiMode = Configuration.UI_MODE_NIGHT_NO
)
@Composable
fun DashboardContentPlaceholderPreview() {
    JetSpendingTheme {
        DashboardContent(
            state = UiState.Loading,
            onNavigate = {}
        )
    }
}