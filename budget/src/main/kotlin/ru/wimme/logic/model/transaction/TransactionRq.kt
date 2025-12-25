package ru.wimme.logic.model.transaction

import java.math.BigDecimal
import java.time.LocalDate

data class TransactionRq(
    val type: TransactionType,
    val userId: String,
    val category: String,
    val amount: BigDecimal,
    val comment: String? = null,
    val date: LocalDate?
)