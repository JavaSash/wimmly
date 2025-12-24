package ru.telegram.bot.adapter.strategy.message

import org.springframework.stereotype.Component
import ru.telegram.bot.adapter.dto.MarkupDataDto
import ru.telegram.bot.adapter.service.MessageWriter
import ru.telegram.bot.adapter.strategy.dto.ButtonRequestDto
import ru.telegram.bot.adapter.strategy.dto.DataModel
import ru.telegram.bot.adapter.strategy.message.common.AbstractSendMessage

@Component
class ButtonRequestMessage<T : DataModel>(messageWriter: MessageWriter) : AbstractSendMessage<ButtonRequestDto>(messageWriter) {

    override fun inlineButtons(chatId: Long, data: ButtonRequestDto?): List<MarkupDataDto> {
        val accept = data!!.accept
        return listOf(
            MarkupDataDto(0, accept.first()),
            MarkupDataDto(1, accept.last()),
            MarkupDataDto(1, accept.last())
        )
    }

}