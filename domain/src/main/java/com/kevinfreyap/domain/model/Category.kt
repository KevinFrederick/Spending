package com.kevinfreyap.domain.model

enum class Category(
    val id: String,
    val type: TransactionType,
    val sortOrder: Int
) {
    // Expenses
    FOOD_DINING(
        id = "CAT_FOOD",
        type = TransactionType.SPENDING,
        sortOrder = 1
    ),
    GROCERIES(
        id = "CAT_GROCERY",
        type = TransactionType.SPENDING,
        sortOrder = 2
    ),
    TRANSPORT(
        id = "CAT_TRANSPORTATION",
        type = TransactionType.SPENDING,
        sortOrder = 3
    ),
    BILLS_UTILITIES(
        id = "CAT_BILLS_UTILITIES",
        type = TransactionType.SPENDING,
        sortOrder = 4
    ),
    SHOPPING(
        id = "CAT_SHOPPING",
        type = TransactionType.SPENDING,
        sortOrder = 5
    ),
    BILLS_SUBSCRIPTIONS(
        id = "CAT_BILLS_SUBSCRIPTIONS",
        type = TransactionType.SPENDING,
        sortOrder = 6
    ),
    HEALTH_MEDICAL(
        id = "CAT_HEALTH_MEDICAL",
        type = TransactionType.SPENDING,
        sortOrder = 7
    ),
    ENTERTAINMENT(
        id = "CAT_ENTERTAINMENT",
        type = TransactionType.SPENDING,
        sortOrder = 8
    ),
    TRAVEL(
        id = "CAT_TRAVEL",
        type = TransactionType.SPENDING,
        sortOrder = 9
    ),
    OTHER_EXPENSE(
        id = "CAT_OTHER_EXPENSE",
        type = TransactionType.SPENDING,
        sortOrder = 49
    ),

    // Income
    SALARY(
        id = "CAT_SALARY",
        type = TransactionType.INCOME,
        sortOrder = 51
    ),
    INVESTMENTS(
        id = "CAT_INVESTMENT",
        type = TransactionType.INCOME,
        sortOrder = 52
    ),
    OTHER_INCOME(
        id = "CAT_OTHER_INCOME",
        type = TransactionType.INCOME,
        sortOrder = 99
    );

    companion object {
        fun allSorted(): List<Category> =
            entries.sortedBy { it.sortOrder }

        fun forType(type: TransactionType): List<Category> =
            entries
                .filter { it.type == type }
                .sortedBy { it.sortOrder }

        fun fromId(
            id: String?,
        ): Category? =
            entries.find { it.id == id }

        fun fromIdOrDefault(
            id: String?,
            type: TransactionType
        ): Category =
            fromId(id) ?: if (type == TransactionType.INCOME) OTHER_INCOME else OTHER_EXPENSE
    }
}