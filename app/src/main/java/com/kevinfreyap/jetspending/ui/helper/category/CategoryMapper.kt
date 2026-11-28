package com.kevinfreyap.jetspending.ui.helper.category

import com.kevinfreyap.domain.model.Category
import com.kevinfreyap.jetspending.R
import com.kevinfreyap.jetspending.ui.model.CategoryUI

fun Category.toCategoryUI(): CategoryUI {
    return when(this) {
        Category.FOOD_DINING -> {
            CategoryUI(
                id = this.id,
                sortOrder = this.sortOrder,
                name = R.string.category_food,
                iconRes = R.drawable.ic_food_icon,
                type = this.type
            )
        }
        Category.GROCERIES -> {
            CategoryUI(
                id = this.id,
                sortOrder = this.sortOrder,
                name = R.string.category_grocery,
                iconRes = R.drawable.ic_grocery_icon,
                type = this.type
            )
        }
        Category.TRANSPORT -> {
            CategoryUI(
                id = this.id,
                sortOrder = this.sortOrder,
                name = R.string.category_transportation,
                iconRes = R.drawable.ic_transportation_24,
                type = this.type
            )
        }
        Category.BILLS_UTILITIES -> {
            CategoryUI(
                id = this.id,
                sortOrder = this.sortOrder,
                name = R.string.category_bills_utilities,
                iconRes = R.drawable.ic_bills_24,
                type = this.type
            )
        }
        Category.SHOPPING -> {
            CategoryUI(
                id = this.id,
                sortOrder = this.sortOrder,
                name = R.string.category_shopping,
                iconRes = R.drawable.ic_shopping_icon,
                type = this.type
            )
        }
        Category.BILLS_SUBSCRIPTIONS -> {
            CategoryUI(
                id = this.id,
                sortOrder = this.sortOrder,
                name = R.string.category_subscriptions,
                iconRes = R.drawable.ic_subscriptions_24,
                type = this.type
            )
        }
        Category.HEALTH_MEDICAL -> {
            CategoryUI(
                id = this.id,
                sortOrder = this.sortOrder,
                name = R.string.category_health_medical,
                iconRes = R.drawable.ic_healthcare_icon,
                type = this.type
            )
        }
        Category.ENTERTAINMENT -> {
            CategoryUI(
                id = this.id,
                sortOrder = this.sortOrder,
                name = R.string.category_entertainment,
                iconRes = R.drawable.ic_entertainment_24,
                type = this.type
            )
        }
        Category.TRAVEL -> {
            CategoryUI(
                id = this.id,
                sortOrder = this.sortOrder,
                name = R.string.category_travel,
                iconRes = R.drawable.ic_flight_24,
                type = this.type
            )
        }
        Category.OTHER_EXPENSE -> {
            CategoryUI(
                id = this.id,
                sortOrder = this.sortOrder,
                name = R.string.category_other_expense,
                iconRes = R.drawable.ic_others_icon,
                type = this.type
            )
        }

        Category.SALARY -> {
            CategoryUI(
                id = this.id,
                sortOrder = this.sortOrder,
                name = R.string.category_salary,
                iconRes = R.drawable.ic_salary_24,
                type = this.type
            )
        }
        Category.INVESTMENTS -> {
            CategoryUI(
                id = this.id,
                sortOrder = this.sortOrder,
                name = R.string.category_investment,
                iconRes = R.drawable.ic_investment_icon,
                type = this.type
            )
        }
        Category.OTHER_INCOME -> {
            CategoryUI(
                id = this.id,
                sortOrder = this.sortOrder,
                name = R.string.category_other_income,
                iconRes = R.drawable.ic_others_icon,
                type = this.type
            )
        }
    }
}