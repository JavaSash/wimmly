package ru.telegram.bot.adapter.dto.budget

import java.math.BigDecimal

data class TransactionRq(
    val type: String,
    val userId: String,
    val category: String,
    val amount: BigDecimal,
    val comment: String? = null
)