package ru.telegram.bot.adapter.dto.budget

import java.time.Instant

data class TransactionSearchRq(
    val type: String,
    val userId: String,
    val category: String,
    val dateFrom: Instant,
    val dateTo: Instant
)