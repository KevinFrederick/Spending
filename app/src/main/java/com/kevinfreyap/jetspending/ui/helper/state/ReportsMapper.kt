package com.kevinfreyap.jetspending.ui.helper.state

import java.time.format.TextStyle
import com.kevinfreyap.domain.model.AppCurrency
import com.kevinfreyap.domain.model.PeriodBucket
import com.kevinfreyap.domain.model.PeriodCategoryPercentage
import com.kevinfreyap.domain.model.PeriodReportData
import com.kevinfreyap.domain.model.PeriodSelectorOption
import com.kevinfreyap.domain.model.TransactionType
import com.kevinfreyap.jetspending.R
import com.kevinfreyap.jetspending.ui.helper.category.toCategoryUI
import com.kevinfreyap.jetspending.ui.helper.currency.toCompactChartAmount
import com.kevinfreyap.jetspending.ui.helper.currency.toCurrencyString
import com.kevinfreyap.jetspending.ui.helper.other.toLocalePercentageText
import com.kevinfreyap.jetspending.ui.screen.report.state.CategoryPercentageItemState
import com.kevinfreyap.jetspending.ui.screen.report.state.ChartBarItemState
import com.kevinfreyap.jetspending.ui.screen.report.state.ChartUiState
import com.kevinfreyap.jetspending.ui.screen.report.state.ReportQueryFilter
import com.kevinfreyap.jetspending.ui.screen.report.state.ReportTotalSummaryState
import com.kevinfreyap.jetspending.ui.screen.report.state.ReportsContentState
import java.math.BigDecimal
import java.util.Locale

fun PeriodReportData.toReportsContentState(
    filter: ReportQueryFilter,
    selectedCurrency: AppCurrency
): ReportsContentState {
        val formattedNet = when {
            net > BigDecimal.ZERO -> "+${net.toCurrencyString(selectedCurrency)}"
            else -> net.toCurrencyString(selectedCurrency)
        }

        val isNetPositive = net > BigDecimal.ZERO
        val isNetNegative = net < BigDecimal.ZERO

        return ReportsContentState(
            summaryState = ReportTotalSummaryState(
                totalNet = formattedNet,
                totalIncome = totalIncome.toCurrencyString(selectedCurrency),
                totalSpending = totalExpense.toCurrencyString(selectedCurrency),
                selectedCurrency = selectedCurrency,
                isNetPositive = isNetPositive,
                isNetNegative = isNetNegative,
                hasSpending = totalExpense > BigDecimal.ZERO,
                hasIncome = totalIncome > BigDecimal.ZERO
            ),
            chartState = buckets.toChartUiState(
                selectedType = filter.selectedType,
                selectedPeriod = filter.selectedPeriod,
                selectedCurrency = selectedCurrency,
                selectedBarId = filter.selectedBarId,
            ),
            categoriesState = categories.toCategoriesItemState(
                selectedType = filter.selectedType,
                selectedCurrency = selectedCurrency
            ),
            hasMissingRates = hasMissingRates
        )
    }

fun List<PeriodCategoryPercentage>.toCategoriesItemState(
    selectedType: TransactionType,
    selectedCurrency: AppCurrency
): List<CategoryPercentageItemState> {
    return this
        .filter { it.type == selectedType }
        .map { categoryPercentage ->
            CategoryPercentageItemState(
                categoryUI = categoryPercentage.category.toCategoryUI(),
                percentage = categoryPercentage.percentage,
                percentageText = categoryPercentage.percentage.toLocalePercentageText(),
                amount = categoryPercentage.amount.toCurrencyString(selectedCurrency),
                selectedCurrency = selectedCurrency
            )
        }
}

fun List<PeriodBucket>.toChartUiState(
    selectedType: TransactionType,
    selectedPeriod: PeriodSelectorOption,
    selectedCurrency: AppCurrency,
    selectedBarId: String?,
    locale: Locale = Locale.getDefault()
): ChartUiState {
    if (isEmpty()) return ChartUiState()

    val amounts = map { bucket ->
        if (selectedType == TransactionType.SPENDING) bucket.expense else bucket.income
    }

    val maxAmountDecimal = amounts.maxOrNull() ?: BigDecimal.ZERO
    val hasAnyData = maxAmountDecimal > BigDecimal.ZERO

    val titleRes = when (selectedPeriod) {
        PeriodSelectorOption.WEEKLY -> {
            if (selectedType == TransactionType.SPENDING) R.string.label_chart_title_weekly_expense
            else R.string.label_chart_title_weekly_income
        }
        PeriodSelectorOption.MONTHLY -> {
            if (selectedType == TransactionType.SPENDING) R.string.label_chart_title_monthly_expense
            else R.string.label_chart_title_monthly_income
        }
        PeriodSelectorOption.YEARLY -> {
            if (selectedType == TransactionType.SPENDING) R.string.label_chart_title_yearly_expense
            else R.string.label_chart_title_yearly_income
        }
    }

    val bars = zip(amounts).map { (bucket, amount) ->
        val heightFraction = if (maxAmountDecimal > BigDecimal.ZERO) {
            (amount.toDouble() / maxAmountDecimal.toDouble()).toFloat().coerceIn(0f, 1f)
        } else {
            0f
        }

        val label = when(selectedPeriod) {
            PeriodSelectorOption.WEEKLY -> {
                bucket.startDate.dayOfWeek.getDisplayName(TextStyle.SHORT, locale)
            }
            PeriodSelectorOption.MONTHLY -> {
                "${bucket.startDate.dayOfMonth}-${bucket.endDate.dayOfMonth}"
            }
            PeriodSelectorOption.YEARLY -> {
                bucket.startDate.month.getDisplayName(TextStyle.SHORT, locale)
            }
        }

        ChartBarItemState(
            id = bucket.id,
            label = label,
            heightFraction = heightFraction,
            formattedAmount = amount.toCompactChartAmount(selectedCurrency),
            hasValue = amount > BigDecimal.ZERO,
            isHighlighted = bucket.id == selectedBarId
        )
    }

    return ChartUiState(
        titleRes = titleRes,
        maxAmount = maxAmountDecimal.toCurrencyString(selectedCurrency),
        bars = bars,
        selectedCurrency = selectedCurrency,
        hasAnyData = hasAnyData
    )
}