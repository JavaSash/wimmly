package ru.wimme.logic.repository

import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import ru.wimme.logic.model.entity.TransactionEntity
import ru.wimme.logic.model.report.Balance
import ru.wimme.logic.model.transaction.TransactionType
import java.time.Instant
import java.util.*

@Repository
interface TransactionRepository: JpaRepository<TransactionEntity, UUID> {

    fun findAllByUserId(userId: String): List<TransactionEntity>

    fun findAllByUserIdAndCreatedAtBetween(userId: String, from: Instant, to: Instant): List<TransactionEntity>

    fun findAllByUserIdAndTypeAndCategory(
        userId: String,
        type: TransactionType,
        category: String,
        pageable: Pageable
    ): List<TransactionEntity>

    @Query("SELECT COUNT(t) > 0 FROM TransactionEntity t WHERE t.userId = :userId AND t.displayId = :displayId")
    fun isExistByUserIdAndDisplayId(userId: String, displayId: Long) : Boolean

    fun findByUserIdAndDisplayId(userId: String, displayId: Long) : List<TransactionEntity>

    fun deleteByUserIdAndDisplayId(userId: String, displayId: Long)

    @Query(
        """
        SELECT new ru.wimme.logic.model.report.Balance(
            COALESCE(SUM(
                CASE 
                    WHEN t.type = 'INCOME' THEN t.amount
                    WHEN t.type = 'EXPENSE' THEN -t.amount
                END
            ),0),
            COALESCE(SUM(
                CASE 
                    WHEN t.type = 'INCOME' 
                    AND t.createdAt >= :periodStart 
                    THEN t.amount 
                END
            ),0),
            COALESCE(SUM(
                CASE 
                    WHEN t.type = 'EXPENSE' 
                    AND t.createdAt >= :periodStart 
                    THEN t.amount 
                END
            ),0)
        )
        FROM TransactionEntity t
        WHERE t.userId = :userId
        """
    )
    fun getBalance(userId: String, periodStart: Instant): Balance

    @Query(
        """
    SELECT new ru.wimme.logic.model.report.Balance(
        COALESCE(SUM(
            CASE 
                WHEN t.type = 'INCOME' THEN t.amount
                WHEN t.type = 'EXPENSE' THEN -t.amount
            END
        ),0),
        COALESCE(SUM(
            CASE 
                WHEN t.type = 'INCOME' THEN t.amount
            END
        ),0),
        COALESCE(SUM(
            CASE 
                WHEN t.type = 'EXPENSE' THEN t.amount
            END
        ),0)
    )
    FROM TransactionEntity t
    WHERE t.userId = :userId
    AND t.createdAt >= :from
    AND t.createdAt < :to
    """
    )
    fun getBalanceForPeriod(
        userId: String,
        from: Instant,
        to: Instant
    ): Balance
}