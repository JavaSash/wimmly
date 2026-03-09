package ru.wimme.logic.model.transaction

data class TransactionSearchRq(
    /**
     * For search trx
     */
    val type: TransactionType?,
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