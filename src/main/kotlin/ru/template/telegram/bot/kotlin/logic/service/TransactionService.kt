package ru.template.telegram.bot.kotlin.logic.service

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import ru.template.telegram.bot.kotlin.logic.exception.NotFoundException
import ru.template.telegram.bot.kotlin.logic.model.TransactionRq
import ru.template.telegram.bot.kotlin.logic.model.entity.TransactionEntity
import ru.template.telegram.bot.kotlin.logic.repository.TransactionRepository
import java.util.*

@Service
class TransactionService(
    private val txRepo: TransactionRepository
) {

    @Transactional
    fun create(request: TransactionRq): TransactionEntity = txRepo.save(
        TransactionEntity(
            id = UUID.randomUUID(),
            type = request.type,
            userId = request.userId,
            category = request.category,
            amount = request.amount,
            comment = request.comment
        )
    )

    fun getById(id: UUID): TransactionEntity =
        txRepo.findById(id)
            .orElseThrow { NotFoundException("Transaction not found: $id") }

    fun getUserTransactions(userId: String): List<TransactionEntity> = txRepo.findAllByUserId(userId)

    @Transactional
    fun delete(id: UUID) {
        if (!txRepo.existsById(id)) throw IllegalArgumentException("Transaction not found: $id")
        txRepo.deleteById(id)
    }

    @Transactional
    fun update(id: UUID, request: TransactionRq): TransactionEntity {
        val tx = txRepo.findById(id)
            .orElseThrow { NotFoundException("Transaction not found: $id") }

        return txRepo.save(tx.copy(
            type = request.type,
            category = request.category,
            amount = request.amount,
            comment = request.comment
        ))
    }
}