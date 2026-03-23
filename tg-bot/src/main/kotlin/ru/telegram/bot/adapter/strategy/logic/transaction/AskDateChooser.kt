package ru.telegram.bot.adapter.strategy.logic.transaction

import mu.KLogging
import org.springframework.stereotype.Component
import org.telegram.telegrambots.meta.api.objects.CallbackQuery
import ru.telegram.bot.adapter.dto.enums.ExecuteStatus
import ru.telegram.bot.adapter.repository.ChatContextRepository
import ru.telegram.bot.adapter.repository.TransactionDraftRepository
import ru.telegram.bot.adapter.strategy.logic.common.CallbackChooser
import ru.telegram.bot.adapter.utils.Constants.Button.NO
import ru.telegram.bot.adapter.utils.Constants.Button.YES
import java.time.Instant

@Component
class AskDateChooser(
    private val chatContextRepository: ChatContextRepository,
    private val transactionDraftRepository: TransactionDraftRepository
) : CallbackChooser {

    companion object : KLogging()

    override fun execute(chatId: Long, callbackQuery: CallbackQuery): ExecuteStatus {
        logger.info { "$$$ AskDateChooser.message data: ${callbackQuery.data}" }
        return when (callbackQuery.data) {
            YES -> {
                chatContextRepository.updateAccept(chatId, true)
                ExecuteStatus.FINAL
            }

            NO -> {
                chatContextRepository.updateAccept(chatId, false)
                val now = Instant.now()
                transactionDraftRepository.updateTransactionDate(chatId, now)
                logger.info { "$$$ Save trx date $now for chat: $chatId" }
                ExecuteStatus.FINAL
            }

            else -> ExecuteStatus.NOTHING
        }
    }
}