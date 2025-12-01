package ru.wimmly.logic.model.entity

import jakarta.persistence.*
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
    val type: ru.wimmly.logic.model.transaction.TransactionType,
    @Column(name = "user_id", nullable = false)
    val userId: String,
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    val category: ru.wimmly.logic.model.transaction.TransactionCategory,
    @Column(nullable = false)
    val amount: BigDecimal,
    @Column(nullable = true)
    val comment: String?
) : BaseEntity()
