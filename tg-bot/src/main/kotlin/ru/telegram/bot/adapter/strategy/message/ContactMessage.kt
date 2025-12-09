package ru.telegram.bot.adapter.strategy.message

import org.springframework.stereotype.Component
import ru.telegram.bot.adapter.component.MessageWriter
import ru.telegram.bot.adapter.dto.ReplyMarkupDto
import ru.telegram.bot.adapter.strategy.dto.ContactDto
import ru.telegram.bot.adapter.strategy.message.common.AbstractSendMessage

@Component
class ContactMessage(messageWriter: MessageWriter): AbstractSendMessage<ContactDto>(messageWriter) {

    override fun replyButtons(
        chatId: Long,
        data: ContactDto?
    ): List<ReplyMarkupDto> {
        return listOf(ReplyMarkupDto(text = "Send contact info", requestContact = true))
    }
}