package ru.template.telegram.bot.kotlin.api

import org.springframework.web.bind.annotation.*
import ru.template.telegram.bot.kotlin.logic.model.transaction.TransactionRq
import ru.template.telegram.bot.kotlin.logic.model.transaction.TransactionRs
import ru.template.telegram.bot.kotlin.logic.service.TransactionService
import java.util.*

@RestController
@RequestMapping("/api/transaction")
class TransactionApi(
    private val transactionService: TransactionService
) {

    @PostMapping
    fun create(@RequestBody request: TransactionRq): TransactionRs = TransactionRs.fromEntity(transactionService.create(request))

    @GetMapping("/{id}")
    fun getById(@PathVariable id: UUID): TransactionRs = TransactionRs.fromEntity(transactionService.getById(id)) // todo check by userId

    @GetMapping("/user/{userId}")
    fun getUserTransactions(@PathVariable userId: String): List<TransactionRs> =
        transactionService.getUserTransactions(userId)
            .map { TransactionRs.fromEntity(it) }

    @PutMapping("/{id}")
    fun update(@PathVariable id: UUID, @RequestBody request: TransactionRq): TransactionRs =
        TransactionRs.fromEntity(transactionService.update(id, request))

    @DeleteMapping("/{id}")
    fun delete(@PathVariable id: UUID) = transactionService.delete(id)
}