package ru.telegram.bot.adapter.strategy.logic.transaction

import mu.KLogging
import org.springframework.stereotype.Component
import org.telegram.telegrambots.meta.api.objects.CallbackQuery
import ru.telegram.bot.adapter.dto.enums.ExecuteStatus
import ru.telegram.bot.adapter.repository.ChatContextRepository
import ru.telegram.bot.adapter.strategy.logic.common.CallbackChooser
import ru.telegram.bot.adapter.utils.Constants.Button.NO
import ru.telegram.bot.adapter.utils.Constants.Button.YES

@Component
class AskDeleteTransactionChooser(
    private val chatContextRepository: ChatContextRepository
): CallbackChooser {

    companion object : KLogging()

    override fun execute(chatId: Long, callbackQuery: CallbackQuery): ExecuteStatus {
        logger.info { "$$$ AskDeleteTransactionChooser.message data: ${callbackQuery.data}" }
        return when (callbackQuery.data) {
            YES -> {
                chatContextRepository.updateAccept(chatId, true)
                ExecuteStatus.FINAL
            }

            NO -> {
                chatContextRepository.updateAccept(chatId, false)
                ExecuteStatus.FINAL
            }

            else -> ExecuteStatus.NOTHING
        }
    }
}