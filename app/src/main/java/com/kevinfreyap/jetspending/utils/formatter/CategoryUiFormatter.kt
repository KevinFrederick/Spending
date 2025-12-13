package com.kevinfreyap.jetspending.utils.formatter

import com.kevinfreyap.domain.model.Category
import com.kevinfreyap.jetspending.R
import com.kevinfreyap.jetspending.ui.model.CategoryUI

object CategoryUiFormatter {
    fun mapCategoryDomainToUi(domain: Category): CategoryUI {
        return CategoryUI(
            id = domain.id,
            name = domain.name,
            sortOrder = domain.sortOrder,
            iconRes = mapIconIdToDrawable(domain.iconId)
        )
    }

    fun mapIconIdToDrawable(iconId: String): Int {
        return when(iconId){
            "GIFT_ICON" -> R.drawable.ic_gift_icon
            "INCOMING_ICON" -> R.drawable.ic_incoming_icon
            "INVESTMENT_ICON" -> R.drawable.ic_investment_icon
            "SALARY_ICON" -> R.drawable.ic_salary_icon
            "EDUCATION_ICON" -> R.drawable.ic_school_icon
            "ENTERTAINMENT_ICON" -> R.drawable.ic_entertainment_icon
            "FOOD_ICON" -> R.drawable.ic_food_icon
            "GROCERY_ICON" -> R.drawable.ic_grocery_icon
            "HEALTHCARE_ICON" -> R.drawable.ic_healthcare_icon
            "HOUSEHOLD_ICON" -> R.drawable.ic_home_24
            "PAYMENT_ICON" -> R.drawable.ic_payment_icon
            "SHOPPING_ICON" -> R.drawable.ic_shopping_icon
            "TRANSPORTATION_ICON" -> R.drawable.ic_transportation_icon
            "OTHER_ICON" -> R.drawable.ic_others_icon
            else -> R.drawable.ic_others_icon
        }
    }
}