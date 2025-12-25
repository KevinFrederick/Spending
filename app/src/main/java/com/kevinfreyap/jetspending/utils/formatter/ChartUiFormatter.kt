package com.kevinfreyap.jetspending.utils.formatter

import com.kevinfreyap.domain.model.ChartData
import com.kevinfreyap.domain.model.PeriodSelectorOption
import com.kevinfreyap.jetspending.ui.model.ChartDataUi

object ChartUiFormatter {
    fun mapChartDomainToUi (chart: ChartData, period: PeriodSelectorOption): ChartDataUi {
        return ChartDataUi(
            index = chart.index,
            xLabel = when(period) {
                PeriodSelectorOption.WEEKLY,
                PeriodSelectorOption.YEARLY -> formatFromUppercaseToCapitalize(chart.xLabel.take(3))
                PeriodSelectorOption.MONTHLY -> chart.xLabel
            }
        )
    }

    private fun formatFromUppercaseToCapitalize(string: String): String {
        return string.lowercase().replaceFirstChar { it.uppercase() }
    }
}