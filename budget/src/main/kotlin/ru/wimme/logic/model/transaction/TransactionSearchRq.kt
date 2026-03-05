package ru.wimme.logic.model.transaction

data class TransactionSearchRq(
    val type: TransactionType,
    val userId: String,
    val category: String,
    val limit: Int
)