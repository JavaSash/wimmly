package ru.telegram.bot.adapter.strategy.message.common

import mu.KLogging
import org.springframework.stereotype.Component
import ru.telegram.bot.adapter.dto.enums.StepCode
import ru.telegram.bot.adapter.service.MessageWriter
import ru.telegram.bot.adapter.strategy.dto.ErrorDto

@Component
class ErrorMessage(messageWriter: MessageWriter) : AbstractSendMessage<ErrorDto>(messageWriter) {
    companion object : KLogging()

    /**
     * @param data - income msg from MessageContext.getMessage()
     * @return string data of .ftl file
     */
    override fun message(data: ErrorDto?): String {
        logger.info { "$$$ ErrorMessage.message data: $data" }

        // Формируем данные для шаблона
        val templateData = mapOf(
            "errorMsg" to data?.errorMsg,
        )

        return messageWriter.process(StepCode.ERROR, templateData)
    }
}