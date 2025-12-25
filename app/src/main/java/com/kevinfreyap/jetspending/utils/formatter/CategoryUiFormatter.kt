package com.kevinfreyap.jetspending.utils.formatter

import androidx.compose.ui.graphics.Color
import com.kevinfreyap.domain.model.Category
import com.kevinfreyap.domain.model.TransactionType
import com.kevinfreyap.jetspending.R
import com.kevinfreyap.jetspending.ui.model.CategoryUI
import com.kevinfreyap.jetspending.ui.theme.Green500
import com.kevinfreyap.jetspending.ui.theme.Orange700

object CategoryUiFormatter {
    fun mapCategoryDomainToUi(domain: Category): CategoryUI {
        return CategoryUI(
            id = domain.id,
            name = mapCategoryNameToString(domain.id),
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

    fun mapCategoryNameToString(catId: String): Int {
        return when(catId) {
            "CAT_GIFT" -> R.string.category_gift
            "CAT_INCOMING" -> R.string.category_incoming
            "CAT_INVESTMENT" -> R.string.category_investment
            "CAT_SALARY" -> R.string.category_salary
            "CAT_EDUCATION" -> R.string.category_education
            "CAT_ENTERTAINMENT" -> R.string.category_entertainment
            "CAT_FOOD" -> R.string.category_food
            "CAT_GROCERY" -> R.string.category_grocery
            "CAT_HEALTHCARE" -> R.string.category_healthcare
            "CAT_HOUSEHOLD" -> R.string.category_household
            "CAT_PAYMENT_TRANSFER" -> R.string.category_payment_transfer
            "CAT_SHOPPING" -> R.string.category_shopping
            "CAT_TRANSPORTATION" -> R.string.category_transportation
            "CAT_OTHER" -> R.string.category_others
            else -> R.string.category_others
        }
    }

    fun getBackgroundColor(transactionType: TransactionType): Color {
        return when (transactionType) {
            TransactionType.INCOME -> Green500
            TransactionType.SPENDING -> Orange700
        }
    }
}