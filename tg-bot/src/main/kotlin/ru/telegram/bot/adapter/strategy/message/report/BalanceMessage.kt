package ru.telegram.bot.adapter.strategy.message.report

import mu.KLogging
import org.springframework.stereotype.Component
import ru.telegram.bot.adapter.service.MessageWriter
import ru.telegram.bot.adapter.dto.enums.StepCode
import ru.telegram.bot.adapter.strategy.dto.BalanceDto
import ru.telegram.bot.adapter.strategy.message.common.AbstractSendMessage
import ru.telegram.bot.adapter.utils.formatMoney

@Component
class BalanceMessage(messageWriter: MessageWriter) : AbstractSendMessage<BalanceDto>(messageWriter) {

    companion object : KLogging()

    /**
     * @param data - income msg from MessageContext.getMessage()
     * form user's balance data and add it to ftl template
     * @return string data of .ftl file
     */
    override fun message(data: BalanceDto?): String {
        logger.info { "$$$ BalanceMessage.message data: $data" }

        // Формируем данные для шаблона
        val templateData = mapOf(
            "balance" to data?.balance?.formatMoney(groupThousands = true),
            "income" to data?.income?.formatMoney(groupThousands = true),
            "expense" to data?.expense?.formatMoney(groupThousands = true),
        )

        return messageWriter.process(StepCode.BALANCE, templateData)
    }
}
