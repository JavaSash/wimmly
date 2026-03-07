package ru.telegram.bot.adapter.strategy.logic.transaction

import mu.KLogging
import org.springframework.stereotype.Component
import org.telegram.telegrambots.meta.api.objects.CallbackQuery
import ru.telegram.bot.adapter.dto.enums.ExecuteStatus
import ru.telegram.bot.adapter.repository.SearchContextRepository
import ru.telegram.bot.adapter.strategy.logic.common.CallbackChooser
import ru.telegram.bot.adapter.utils.Constants.Transaction.EXPENSE
import ru.telegram.bot.adapter.utils.Constants.Transaction.INCOME

@Component
class AskTransactionTypeChooser(
    private val searchContextRepository: SearchContextRepository,
) : CallbackChooser {

    companion object : KLogging()

    override fun execute(chatId: Long, callbackQuery: CallbackQuery): ExecuteStatus {
        logger.info { "$$$ AskTransactionTypeChooser.message data: ${callbackQuery.data}" }
        return when (callbackQuery.data) {
            INCOME -> {
                searchContextRepository.updateTransactionType(chatId, INCOME)
                ExecuteStatus.FINAL
            }

            EXPENSE -> {
                searchContextRepository.updateTransactionType(chatId, EXPENSE)
                ExecuteStatus.FINAL
            }

            else -> ExecuteStatus.NOTHING
        }
    }
}