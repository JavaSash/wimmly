package ru.telegram.bot.adapter.dto.budget

data class TransactionSearchRq(
    /**
     * For search trx
     */
    val type: String?,
    val userId: String,
    /**
     * For search trx
     */
    val category: String?,
    val limit: Int,
    /**
     * Only for search before delete
     */
    val displayId: Long?
)