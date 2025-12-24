package ru.telegram.bot.adapter.strategy.message.common

import org.springframework.stereotype.Component
import ru.telegram.bot.adapter.service.MessageWriter
import ru.telegram.bot.adapter.strategy.dto.DataModel

@Component
class FinalMessage(messageWriter: MessageWriter) : AbstractSendMessage<DataModel>(messageWriter)