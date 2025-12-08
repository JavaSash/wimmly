package ru.telegram.bot.adapter.strategy

import mu.KLogging
import org.springframework.stereotype.Component
import org.telegram.telegrambots.meta.api.objects.CallbackQuery
import org.telegram.telegrambots.meta.api.objects.message.Message
import ru.telegram.bot.adapter.dto.enums.ExecuteStatus
import ru.telegram.bot.adapter.dto.enums.StepCode
import ru.telegram.bot.adapter.strategy.logic.CallbackChooser
import ru.telegram.bot.adapter.strategy.logic.MessageChooser

// Основная бизнес логика
@Component
class LogicContext(
    private val telegramCallbackChooser: Map<StepCode, CallbackChooser>,
    private val telegramMessageChooser: Map<StepCode, MessageChooser>
) {
    companion object: KLogging()

    fun execute(chatId: Long, message: Message, stepCode: StepCode) {
        logger.info { "$$$ LogicContext.execute with params\nchat: $chatId\nmessage: $message\nstep: $stepCode" }
        telegramMessageChooser[stepCode]?.execute(chatId = chatId, message = message)
    }

    fun execute(chatId: Long, callbackQuery: CallbackQuery, stepCode: StepCode): ExecuteStatus {
        logger.info { "$$$ LogicContext.execute with callback and params\nchat: $chatId\ncallback: $callbackQuery\nstep: $stepCode" }
        return telegramCallbackChooser[stepCode]
            ?.execute(chatId = chatId, callbackQuery = callbackQuery)
            ?: throw IllegalStateException("Callback not found")
    }
}
