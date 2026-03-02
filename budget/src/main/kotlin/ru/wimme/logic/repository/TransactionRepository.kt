package ru.wimme.logic.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import ru.wimme.logic.model.entity.TransactionEntity
import ru.wimme.logic.model.transaction.TransactionType
import java.time.Instant
import java.util.*

@Repository
interface TransactionRepository: JpaRepository<TransactionEntity, UUID> {

    fun findAllByUserId(userId: String): List<TransactionEntity>

    fun findAllByUserIdAndCreatedAtBetween(userId: String, from: Instant, to: Instant): List<TransactionEntity>

    fun findAllByUserIdAndTypeAndCategoryAndCreatedAtBetween(
        userId: String,
        type: TransactionType,
        category: String,
        dateFrom: Instant,
        dateTo: Instant
    ): List<TransactionEntity>
}