package ru.telegram.bot.adapter.strategy.message.transaction

import org.springframework.stereotype.Component
import ru.telegram.bot.adapter.service.MessageWriter
import ru.telegram.bot.adapter.strategy.dto.DataModel
import ru.telegram.bot.adapter.strategy.message.common.AbstractSendMessage

@Component
class EnterAmountMessage(messageWriter: MessageWriter) : AbstractSendMessage<DataModel>(messageWriter)