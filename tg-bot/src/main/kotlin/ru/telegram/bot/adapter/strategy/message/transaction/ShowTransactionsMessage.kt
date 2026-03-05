package ru.telegram.bot.adapter.strategy.message.transaction

import mu.KLogging
import org.springframework.stereotype.Component
import ru.telegram.bot.adapter.dto.enums.StepCode
import ru.telegram.bot.adapter.service.MessageWriter
import ru.telegram.bot.adapter.strategy.dto.ShowTransactionsDto
import ru.telegram.bot.adapter.strategy.message.common.AbstractSendMessage

@Component
class ShowTransactionsMessage(messageWriter: MessageWriter) : AbstractSendMessage<ShowTransactionsDto>(messageWriter) {
    companion object : KLogging()

    /**
     * @param data - income msg from MessageContext.getMessage()
     * form show transactions data by filters and add it to ftl template
     * @return string data of .ftl file
     */
    override fun message(data: ShowTransactionsDto?): String {
        logger.info { "$$$ ShowTransactionsMessage.message data: $data" }

        val templateData = mapOf(
            "data" to data
        )

        return messageWriter.process(StepCode.SHOW_TRANSACTIONS, templateData)
    }
}
