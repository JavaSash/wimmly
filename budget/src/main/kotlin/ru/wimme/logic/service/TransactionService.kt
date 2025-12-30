package ru.wimme.logic.service

import mu.KLogging
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import ru.wimme.logic.exception.NotFoundException
import ru.wimme.logic.model.entity.TransactionEntity
import ru.wimme.logic.model.transaction.TransactionRq
import ru.wimme.logic.repository.TransactionRepository
import java.time.Instant
import java.util.*

@Service
class TransactionService(
    private val txRepo: TransactionRepository
) {

    companion object : KLogging()

    @Transactional
    fun create(request: TransactionRq): TransactionEntity {
        logger.info { "$$$ TransactionService.create tx for rq: $request" }
        return txRepo.save(
            TransactionEntity(
                id = UUID.randomUUID(),
                type = request.type,
                userId = request.userId,
                category = request.category,
                amount = request.amount.setScale(2),
                comment = request.comment,
                createdAt = request.date ?: Instant.now()
            )
        )
    }

    fun getById(id: UUID): TransactionEntity =
        txRepo.findById(id)
            .orElseThrow { NotFoundException("Transaction not found: $id") }

    fun getUserTransactions(userId: String, from: Instant? = null, to: Instant? = null): List<TransactionEntity> =
        if (from != null && to != null) txRepo.findAllByUserIdAndCreatedAtBetween(userId =  userId, from = from, to = to)
            else txRepo.findAllByUserId(userId)

    @Transactional
    fun delete(id: UUID) {
        if (!txRepo.existsById(id)) throw IllegalArgumentException("Transaction not found: $id")
        txRepo.deleteById(id)
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
}