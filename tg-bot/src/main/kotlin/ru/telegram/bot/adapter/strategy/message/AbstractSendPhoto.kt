package ru.telegram.bot.adapter.strategy.message

import ru.telegram.bot.adapter.service.MessageWriter
import ru.telegram.bot.adapter.strategy.dto.DataModel
import ru.telegram.bot.adapter.strategy.message.common.AbstractSendMessage
import java.io.ByteArrayInputStream

abstract class AbstractSendPhoto<T: DataModel>(messageWriter: MessageWriter): AbstractSendMessage<T>(messageWriter) {

    abstract fun file(data: T?): ByteArrayInputStream?

}