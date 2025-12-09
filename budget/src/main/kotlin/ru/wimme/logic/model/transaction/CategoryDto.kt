package ru.wimme.logic.model.transaction

data class CategoryDto(
    val code: String,
    val description: String,
    val type: TransactionType
)
