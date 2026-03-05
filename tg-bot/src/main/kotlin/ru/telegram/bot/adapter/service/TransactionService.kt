package ru.telegram.bot.adapter.service

import org.springframework.stereotype.Service
import ru.telegram.bot.adapter.client.TransactionClient
import ru.telegram.bot.adapter.domain.tables.tables.pojos.TransactionDraft
import ru.telegram.bot.adapter.dto.budget.TransactionRq

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
}