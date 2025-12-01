package ru.wimmly.logic.model.entity

import jakarta.persistence.*
import ru.wimmly.logic.model.transaction.TransactionCategory
import ru.wimmly.logic.model.transaction.TransactionType
import java.math.BigDecimal
import java.util.*

@Entity
@Table(name = "transactions")
data class TransactionEntity(
    @Id
    @GeneratedValue
    val id: UUID? = null,
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    val type: TransactionType,
    @Column(name = "user_id", nullable = false)
    val userId: String,
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    val category: TransactionCategory,
    @Column(nullable = false)
    val amount: BigDecimal,
    @Column(nullable = true)
    val comment: String?
) : BaseEntity()
