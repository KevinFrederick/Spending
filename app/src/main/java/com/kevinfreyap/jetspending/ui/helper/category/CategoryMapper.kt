package com.kevinfreyap.jetspending.ui.helper.category

import com.kevinfreyap.domain.model.Category
import com.kevinfreyap.jetspending.R
import com.kevinfreyap.jetspending.ui.model.CategoryUI

fun Category.toCategoryUI(): CategoryUI {
    return when(this.id) {
        "CAT_GIFT" -> {
            CategoryUI(
                id = this.id,
                sortOrder = this.sortOrder,
                name = R.string.category_gift,
                iconRes = R.drawable.ic_gift_icon
            )
        }
        "CAT_INCOMING" -> {
            CategoryUI(
                id = this.id,
                sortOrder = this.sortOrder,
                name = R.string.category_incoming,
                iconRes = R.drawable.ic_incoming_icon
            )
        }
        "CAT_INVESTMENT" -> {
            CategoryUI(
                id = this.id,
                sortOrder = this.sortOrder,
                name = R.string.category_investment,
                iconRes = R.drawable.ic_investment_icon
            )
        }
        "CAT_SALARY" -> {
            CategoryUI(
                id = this.id,
                sortOrder = this.sortOrder,
                name = R.string.category_salary,
                iconRes = R.drawable.ic_salary_icon
            )
        }
        "CAT_EDUCATION" -> {
            CategoryUI(
                id = this.id,
                sortOrder = this.sortOrder,
                name = R.string.category_education,
                iconRes = R.drawable.ic_school_icon
            )
        }
        "CAT_ENTERTAINMENT" -> {
            CategoryUI(
                id = this.id,
                sortOrder = this.sortOrder,
                name = R.string.category_entertainment,
                iconRes = R.drawable.ic_entertainment_icon
            )
        }
        "CAT_FOOD" -> {
            CategoryUI(
                id = this.id,
                sortOrder = this.sortOrder,
                name = R.string.category_food,
                iconRes = R.drawable.ic_food_icon
            )
        }
        "CAT_GROCERY" -> {
            CategoryUI(
                id = this.id,
                sortOrder = this.sortOrder,
                name = R.string.category_grocery,
                iconRes = R.drawable.ic_grocery_icon
            )
        }
        "CAT_HEALTHCARE" -> {
            CategoryUI(
                id = this.id,
                sortOrder = this.sortOrder,
                name = R.string.category_healthcare,
                iconRes = R.drawable.ic_healthcare_icon
            )
        }
        "CAT_HOUSEHOLD" -> {
            CategoryUI(
                id = this.id,
                sortOrder = this.sortOrder,
                name = R.string.category_household,
                iconRes = R.drawable.ic_home_24
            )
        }
        "CAT_PAYMENT_TRANSFER" -> {
            CategoryUI(
                id = this.id,
                sortOrder = this.sortOrder,
                name = R.string.category_payment_transfer,
                iconRes = R.drawable.ic_payment_icon
            )
        }
        "CAT_SHOPPING" -> {
            CategoryUI(
                id = this.id,
                sortOrder = this.sortOrder,
                name = R.string.category_shopping,
                iconRes = R.drawable.ic_shopping_icon
            )
        }
        "CAT_TRANSPORTATION" -> {
            CategoryUI(
                id = this.id,
                sortOrder = this.sortOrder,
                name = R.string.category_transportation,
                iconRes = R.drawable.ic_transportation_icon
            )
        }
        "CAT_OTHER" -> {
            CategoryUI(
                id = this.id,
                sortOrder = this.sortOrder,
                name = R.string.category_others,
                iconRes = R.drawable.ic_others_icon
            )
        }
        else -> {
            CategoryUI(
                id = this.id,
                sortOrder = this.sortOrder,
                name = R.string.category_others,
                iconRes = R.drawable.ic_others_icon
            )
        }
    }
}