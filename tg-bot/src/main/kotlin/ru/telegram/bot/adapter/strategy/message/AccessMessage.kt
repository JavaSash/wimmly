package ru.telegram.bot.adapter.strategy.message

import org.springframework.stereotype.Component
import ru.telegram.bot.adapter.service.MessageWriter
import ru.telegram.bot.adapter.strategy.dto.DataModel
import ru.telegram.bot.adapter.strategy.message.common.AbstractSendMessage

@Component
class AccessMessage(messageWriter: MessageWriter) : AbstractSendMessage<DataModel>(messageWriter) {

    override fun message(data: DataModel?): String {
        return "" // not use, this is only for test isPermitted
    }

    override fun isPermitted(chatId: Long): Boolean {
        return false
    }
}