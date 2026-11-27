package com.kevinfreyap.jetspending.ui.screen.report.state

data class ChartBarItemState(
    val id: String,
    val label: String,
    val heightFraction: Float,
    val formattedAmount: String,
    val hasValue: Boolean,
    val isHighlighted: Boolean
)
