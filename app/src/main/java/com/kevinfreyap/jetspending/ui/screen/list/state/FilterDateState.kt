package com.kevinfreyap.jetspending.ui.screen.list.state

import com.kevinfreyap.domain.model.TimeFilterOption
import com.kevinfreyap.jetspending.ui.helper.date.toFullDateShortMonth
import java.time.Instant

data class FilterDateState(
    val selectedOption: TimeFilterOption = TimeFilterOption.ALL_TIME,
    val customStartDate: Instant? = null,
    val customEndDate: Instant? = null
) {
    val customStartDateText: String?
        get() = customStartDate?.toFullDateShortMonth()

    val customEndDateText: String?
        get() = customEndDate?.toFullDateShortMonth()

    val isCustomRangeInvalid: Boolean
        get() = selectedOption == TimeFilterOption.PICK_DATE &&
                customStartDate != null &&
                customEndDate != null &&
                customStartDate.isAfter(customEndDate)
}
