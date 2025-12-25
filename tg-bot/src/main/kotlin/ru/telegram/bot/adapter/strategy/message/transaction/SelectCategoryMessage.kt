package ru.telegram.bot.adapter.strategy.message.transaction

import mu.KLogging
import org.springframework.stereotype.Component
import ru.telegram.bot.adapter.dto.MarkupDataDto
import ru.telegram.bot.adapter.service.MessageWriter
import ru.telegram.bot.adapter.strategy.dto.SelectCategoryDto
import ru.telegram.bot.adapter.strategy.message.common.AbstractSendMessage
import ru.telegram.bot.adapter.utils.Constants.Transaction.INCOME

@Component
class SelectCategoryMessage(messageWriter: MessageWriter) : AbstractSendMessage<SelectCategoryDto>(messageWriter) {

    companion object : KLogging()

    /**
     * @param data - income msg from MessageContext.getMessage()
     * form transaction categories and add it to ftl template
     * @return string data of .ftl file
     */
    override fun message(data: SelectCategoryDto?): String {
        logger.info { "$$$ SelectCategoryMessage.message data: $data" }

        val transactionType = data?.txType ?: INCOME
        val title = if (transactionType == INCOME) "дохода" else "расхода"

        return "💸 <b>Выберите категорию $title:</b>"
    }

    override fun inlineButtons(chatId: Long, data: SelectCategoryDto?): List<MarkupDataDto> {
        val categories = data?.categories ?: emptyList()

        // Создаем кнопки - по 2 в ряд
        return categories.mapIndexed { index, category ->
            MarkupDataDto(
                rowPos = index / 2, // 0, 0, 1, 1, 2, 2...
                text = category.code
            ).also { logger.info { "$$$ Created MarkupDataDto: text=${it.text}" } }
        }
    }
}