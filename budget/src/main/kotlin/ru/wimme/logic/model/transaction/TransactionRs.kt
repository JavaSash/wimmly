package ru.wimme.logic.model.transaction

import ru.wimme.logic.model.entity.TransactionEntity
import java.math.BigDecimal
import java.util.*

data class TransactionRs(
    val id: UUID,
    val type: TransactionType,
    val userId: String,
    val category: TransactionCategory,
    val amount: BigDecimal,
    val comment: String?
) {
    companion object {
        fun fromEntity(e: TransactionEntity) =
            TransactionRs(
                id = e.id!!,
                type = e.type,
                userId = e.userId,
                category = e.category,
                amount = e.amount,
                comment = e.comment
            )
    }
}
