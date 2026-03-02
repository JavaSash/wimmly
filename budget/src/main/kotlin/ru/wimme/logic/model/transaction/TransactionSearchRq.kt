package ru.wimme.logic.model.transaction

import java.time.Instant

data class TransactionSearchRq(
    val type: TransactionType,
    val userId: String,
    val category: String,
    val dateFrom: Instant,
    val dateTo: Instant
)