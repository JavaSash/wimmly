package ru.telegram.bot.adapter.strategy.message.transaction

import mu.KLogging
import org.springframework.stereotype.Component
import ru.telegram.bot.adapter.dto.MarkupDataDto
import ru.telegram.bot.adapter.service.MessageWriter
import ru.telegram.bot.adapter.strategy.dto.DataModel
import ru.telegram.bot.adapter.strategy.message.common.AbstractSendMessage
import ru.telegram.bot.adapter.utils.Constants.Transaction.EXPENSE
import ru.telegram.bot.adapter.utils.Constants.Transaction.INCOME

@Component
class AskTransactionTypeMessage(messageWriter: MessageWriter) : AbstractSendMessage<DataModel>(messageWriter) {

    companion object : KLogging()

    override fun message(data: DataModel?): String {
        return "📊 <b>Выберите тип транзакции:</b>" // todo to ftl ?
    }

    override fun inlineButtons(chatId: Long, data: DataModel?): List<MarkupDataDto> {
        val buttons = mutableListOf<MarkupDataDto>()

        buttons.add(
            MarkupDataDto(
                rowPos = 0,
                text = INCOME
            )
        )

        buttons.add(
            MarkupDataDto(
                rowPos = 0,
                text = EXPENSE
            )
        )

        logger.info { "$$$ Created buttons: ${buttons.first().text}, ${buttons.last().text}" }

        return buttons
    }
}
