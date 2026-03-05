package ru.telegram.bot.adapter.dto.budget

data class TransactionSearchRq(
    val type: String,
    val userId: String,
    val category: String,
    val limit: Int
)