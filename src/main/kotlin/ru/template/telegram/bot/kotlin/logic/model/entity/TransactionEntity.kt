package ru.template.telegram.bot.kotlin.logic.model.entity

import ru.template.telegram.bot.kotlin.logic.model.transaction.TransactionCategory
import ru.template.telegram.bot.kotlin.logic.model.transaction.TransactionType
import java.util.*
import jakarta.persistence.*
import java.math.BigDecimal

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
