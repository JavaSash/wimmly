package ru.telegram.bot.adapter.strategy.dto

data class ShowTransactionsDto(
    val transactions: List<TransactionItem>,
): DataModel

data class TransactionItem(
    val displayId: Long,
    val formattedDate: String,
    val category: String,
    val type: String,
    val amount: String,
    val comment: String?
)