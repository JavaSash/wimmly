package ru.wimme.logic.model.entity

import jakarta.persistence.*
import ru.wimme.logic.model.transaction.TransactionType
import java.math.BigDecimal
import java.time.Instant
import java.util.*

@Entity
@Table(name = "transactions")
data class TransactionEntity(
    @Id // todo сделать составной ПК user_id+display_id?
    @GeneratedValue
    val id: UUID? = null,
    @Column(name = "display_id", nullable = false)
    val displayId : Long,
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
