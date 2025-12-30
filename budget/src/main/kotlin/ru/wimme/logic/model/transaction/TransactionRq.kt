package ru.wimme.logic.model.transaction

import java.math.BigDecimal
import java.time.Instant

data class TransactionRq(
    val type: TransactionType,
    val userId: String,
    val category: String,
    val amount: BigDecimal,
    val comment: String? = null,
    val date: Instant?
)