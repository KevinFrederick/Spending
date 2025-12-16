package com.kevinfreyap.jetspending.ui.model

sealed class FilterBottomSheetType {
    data object None: FilterBottomSheetType()
    data object Filter: FilterBottomSheetType()
    data object DateFilter: FilterBottomSheetType()
    data object AmountFrom: FilterBottomSheetType()
    data object AmountTo: FilterBottomSheetType()
}
