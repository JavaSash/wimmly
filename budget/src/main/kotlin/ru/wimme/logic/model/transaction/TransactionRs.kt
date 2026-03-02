package ru.wimme.logic.model.transaction

import ru.wimme.logic.model.entity.TransactionEntity
import java.math.BigDecimal
import java.time.Instant
import java.util.*

data class TransactionRs(
    val id: UUID,
    val displayId: Long,
    val type: TransactionType,
    val userId: String,
    val category: String,
    val amount: BigDecimal,
    val comment: String?,
    val createdAt: Instant?
) {
    companion object {
        fun fromEntity(e: TransactionEntity) =
            TransactionRs(
                id = e.id!!,
                displayId = e.displayId,
                type = e.type,
                userId = e.userId,
                category = e.category,
                amount = e.amount,
                comment = e.comment,
                createdAt = e.createdAt
            )
    }
}
