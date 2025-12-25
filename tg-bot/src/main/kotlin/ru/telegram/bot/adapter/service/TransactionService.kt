package ru.telegram.bot.adapter.service

import org.springframework.stereotype.Service
import ru.telegram.bot.adapter.client.TransactionClient
import ru.telegram.bot.adapter.domain.tables.pojos.Users
import ru.telegram.bot.adapter.dto.budget.TransactionRq

@Service
class TransactionService(
    private val txClient: TransactionClient
) {

    fun addTransaction(user: Users): String = txClient.addTransaction(
            TransactionRq(
                type = user.transactionType!!,
                userId = user.id.toString(),
                category = user.category!!,
                amount = user.amount!!,
                comment = user.comment,
                date = user.transactionDate
            )
        )
}