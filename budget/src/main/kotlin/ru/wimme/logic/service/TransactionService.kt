package ru.wimme.logic.service

import mu.KLogging
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import ru.wimme.logic.exception.NotFoundException
import ru.wimme.logic.model.entity.TransactionEntity
import ru.wimme.logic.model.report.Balance
import ru.wimme.logic.model.transaction.TransactionRq
import ru.wimme.logic.model.transaction.TransactionRs
import ru.wimme.logic.model.transaction.TransactionSearchRq
import ru.wimme.logic.repository.TransactionRepository
import java.time.Instant
import java.util.*

@Service
class TransactionService(
    private val txRepo: TransactionRepository,
    private val userTrxSeqService: UserTransactionSeqService
) {

    companion object : KLogging()

    @Transactional
    fun create(rq: TransactionRq): TransactionEntity {
        logger.info { "$$$ TransactionService.create tx for rq: $rq" }
        return txRepo.save(
            TransactionEntity(
                id = UUID.randomUUID(),
                displayId = userTrxSeqService.getNextSeq(rq.userId),
                type = rq.type,
                userId = rq.userId,
                category = rq.category,
                amount = rq.amount.setScale(2),
                comment = rq.comment,
                createdAt = rq.date ?: Instant.now()
            )
        )
    }

    fun getById(id: UUID): TransactionEntity =
        txRepo.findById(id)
            .orElseThrow { NotFoundException("Transaction not found: $id") }

    fun getUserTransactions(userId: String, from: Instant? = null, to: Instant? = null): List<TransactionEntity> =
        if (from != null && to != null) txRepo.findAllByUserIdAndCreatedAtBetween(userId = userId, from = from, to = to)
        else txRepo.findAllByUserId(userId)

    fun getBalance(userId: String, periodStart: Instant): Balance =
        txRepo.getBalance(userId = userId, periodStart = periodStart)

    fun getBalanceForPeriod(userId: String, from: Instant, to: Instant): Balance =
        txRepo.getBalanceForPeriod(userId = userId, from = from, to = to)

    @Transactional
    fun delete(id: UUID) {
        if (!txRepo.existsById(id)) throw IllegalArgumentException("Transaction not found: $id")
        txRepo.deleteById(id)
    }

    @Transactional
    fun delete(userId: Long, displayId: Long) {
        if (!isExist(
                userId = userId,
                displayId = displayId
            )
        ) throw IllegalArgumentException("Transaction not found: $displayId for user: $userId")
        txRepo.deleteByUserIdAndDisplayId(userId = userId.toString(), displayId = displayId)
    }

    @Transactional
    fun update(id: UUID, request: TransactionRq): TransactionEntity {
        val tx = txRepo.findById(id)
            .orElseThrow { NotFoundException("Transaction not found: $id") }

        return txRepo.save(
            tx.copy(
                type = request.type,
                category = request.category,
                amount = request.amount.setScale(2),
                comment = request.comment,
                createdAt = request.date ?: Instant.now()
            )
        )
    }

    fun findTransactionsWithFilters(rq: TransactionSearchRq): List<TransactionRs> {
        logger.info { "$$$ TransactionService.findTransactionsWithFilters tx for rq: $rq" }
        val transactions = rq.displayId?.let { displayId ->
            txRepo.findByUserIdAndDisplayId(userId = rq.userId, displayId = displayId)
                .map { TransactionRs.fromEntity(it) }
        }
            ?: txRepo.findAllByUserIdAndTypeAndCategory(
                userId = rq.userId,
                type = rq.type!!,
                category = rq.category!!,
                PageRequest.of(0, rq.limit, Sort.by("createdAt").descending())
            ).map { TransactionRs.fromEntity(it) }

        return transactions.also { logger.info { "$$$ Found transactions: $it" } }
    }

    fun isExist(userId: Long, displayId: Long): Boolean =
        txRepo.isExistByUserIdAndDisplayId(userId = userId.toString(), displayId = displayId)
}