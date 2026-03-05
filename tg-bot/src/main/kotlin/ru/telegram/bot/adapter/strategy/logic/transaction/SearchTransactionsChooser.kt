package ru.telegram.bot.adapter.strategy.logic.transaction

import mu.KLogging
import org.springframework.stereotype.Component
import org.telegram.telegrambots.meta.api.objects.message.Message
import ru.telegram.bot.adapter.dto.enums.StepCode
import ru.telegram.bot.adapter.repository.ChatContextRepository
import ru.telegram.bot.adapter.strategy.logic.common.MessageChooser

@Component
class SearchTransactionsChooser(
    private val chatContextRepository: ChatContextRepository
) : MessageChooser {

    companion object : KLogging()

    override fun execute(chatId: Long, message: Message) {
        logger.info { "$$$ SearchTransactionsChooser.execute" }
        chatContextRepository.updateUserStep(chatId, StepCode.ASK_TRANSACTION_TYPE)
    }
}