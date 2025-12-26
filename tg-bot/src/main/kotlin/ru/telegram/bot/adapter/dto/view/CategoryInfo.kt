package ru.telegram.bot.adapter.dto.view

import java.math.BigDecimal

data class CategoryInfo(
    val name: String,
    val amount: BigDecimal,
    val formattedAmount: String,
    val percentage: String,
    val isExpense: Boolean = false
)
