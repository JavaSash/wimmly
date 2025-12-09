package ru.telegram.bot.adapter.strategy.message.common

import org.springframework.stereotype.Component
import ru.telegram.bot.adapter.component.MessageWriter
import ru.telegram.bot.adapter.strategy.dto.DataModel

@Component
class StartMessage(messageWriter: MessageWriter) : AbstractSendMessage<DataModel>(messageWriter)