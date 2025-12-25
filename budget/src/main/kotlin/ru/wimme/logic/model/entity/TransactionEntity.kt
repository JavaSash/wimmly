package ru.wimme.logic.model.entity

import jakarta.persistence.*
import ru.wimme.logic.model.transaction.TransactionType
import java.math.BigDecimal
import java.time.Instant
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
    @Column(nullable = false)
    val category: String,
    @Column(nullable = false)
    val amount: BigDecimal,
    @Column(nullable = true)
    val comment: String?,
    @Column(name = "created_at", nullable = false)
    val createdAt: Instant = Instant.now(),
) : BaseEntity()
