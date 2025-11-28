package ru.template.telegram.bot.kotlin.logic.model.transaction

import java.math.BigDecimal

data class TransactionRq(
    val type: TransactionType,
    val userId: String,
    val category: TransactionCategory,
    val amount: BigDecimal,
    val comment: String? = null
)