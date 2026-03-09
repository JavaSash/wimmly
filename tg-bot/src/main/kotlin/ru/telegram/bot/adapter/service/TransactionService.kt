package ru.telegram.bot.adapter.service

import org.springframework.stereotype.Service
import ru.telegram.bot.adapter.client.TransactionClient
import ru.telegram.bot.adapter.domain.tables.tables.pojos.TransactionDraft
import ru.telegram.bot.adapter.dto.budget.TransactionRq
import ru.telegram.bot.adapter.dto.budget.TransactionRs
import ru.telegram.bot.adapter.dto.budget.TransactionSearchRq

@Service
class TransactionService(
    private val txClient: TransactionClient
) {

    fun addTransaction(trxDraft: TransactionDraft): String = txClient.addTransaction(
        TransactionRq(
            type = trxDraft.type!!,
            userId = trxDraft.chatId.toString(),
            category = trxDraft.category!!,
            amount = trxDraft.amount!!,
            comment = trxDraft.comment,
            date = trxDraft.date
        )
    )

    fun getTransactionsWithFilters(rq: TransactionSearchRq): List<TransactionRs> =
        txClient.getTransactionsWithFilters(rq)

    fun isExist(chatId: Long, displayId: Long): Boolean = txClient.isExist(chatId, displayId)

    fun getUserTransactionByDisplayId(chatId: Long, displayId: Long): TransactionRs? =
        txClient.getTransactionsWithFilters(
            rq = TransactionSearchRq(
                type = null,
                userId = chatId.toString(),
                category = null,
                limit = 1, // stub, not used in search
                displayId = displayId
            )
        ).firstOrNull()

    fun removeTransaction(chatId: Long, trxId: Long) = txClient.deleteByDisplayId(userId = chatId, displayId = trxId)
}