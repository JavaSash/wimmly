package ru.telegram.bot.adapter.strategy.message.transaction

import org.springframework.stereotype.Component
import ru.telegram.bot.adapter.dto.MarkupDataDto
import ru.telegram.bot.adapter.service.MessageWriter
import ru.telegram.bot.adapter.strategy.dto.AskYesNoDto
import ru.telegram.bot.adapter.strategy.message.common.AbstractSendMessage

@Component
class AskCommentMessage(messageWriter: MessageWriter) : AbstractSendMessage<AskYesNoDto>(messageWriter) {

    override fun inlineButtons(chatId: Long, data: AskYesNoDto?): List<MarkupDataDto> {
        val accept = data!!.accept
        return listOf(
            MarkupDataDto(rowPos = 0, text = accept.first()),
            MarkupDataDto(rowPos = 1, text = accept.last()),
        )
    }
}