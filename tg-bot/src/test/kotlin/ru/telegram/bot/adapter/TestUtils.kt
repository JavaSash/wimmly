package ru.telegram.bot.adapter

import org.telegram.telegrambots.meta.api.objects.CallbackQuery
import org.telegram.telegrambots.meta.api.objects.message.Message
import ru.telegram.bot.adapter.TestConstants.Chat.CHAT_ID
import ru.telegram.bot.adapter.TestConstants.Tx.AMOUNT_100
import ru.telegram.bot.adapter.TestConstants.Tx.COMMENT
import ru.telegram.bot.adapter.TestConstants.Tx.FOOD_CATEGORY
import ru.telegram.bot.adapter.TestConstants.Tx.TRX_ID
import ru.telegram.bot.adapter.domain.tables.tables.pojos.ChatContext
import ru.telegram.bot.adapter.domain.tables.tables.pojos.SearchContext
import ru.telegram.bot.adapter.domain.tables.tables.pojos.TransactionDraft
import ru.telegram.bot.adapter.dto.budget.TransactionRs
import ru.telegram.bot.adapter.dto.enums.StepCode
import ru.telegram.bot.adapter.utils.Constants.Transaction.EXPENSE
import java.time.Instant
import java.util.*

fun formSearchContext(type: String = EXPENSE) =
    SearchContext(
        chatId = CHAT_ID,
        trxId = TRX_ID,
        type = type,
        category = FOOD_CATEGORY
    )

fun formTransactionRs(type: String = EXPENSE, category: String = FOOD_CATEGORY) =
    TransactionRs(
        id = UUID.randomUUID(),
        displayId = 1L,
        type = type,
        userId = CHAT_ID.toString(),
        category = category,
        amount = AMOUNT_100,
        comment = COMMENT,
        createdAt = Instant.now()
    )

fun formChatContext(flowContext: String = StepCode.SEARCH_TRANSACTIONS.name) =
    ChatContext(
        id = CHAT_ID,
        stepCode = null,
        text = null,
        accept = null,
        flowContext = flowContext,
        errorMsg = null,
        errorStep = null
    )

fun formTransactionDraft(type: String = EXPENSE) =
    TransactionDraft(
        chatId = CHAT_ID,
        type = type,
        category = null,
        amount = null,
        date = null,
        comment = null
    )

fun formCallbackQuery(data: String) =
    CallbackQuery().apply {
        this.data = data
    }

fun formMessage(text: String? = null): Message =
    Message.builder()
        .text(text)
        .build()