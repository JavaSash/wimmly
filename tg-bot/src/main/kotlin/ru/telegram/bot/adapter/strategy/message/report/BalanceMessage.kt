package ru.telegram.bot.adapter.strategy.message.report

import org.springframework.stereotype.Component
import ru.telegram.bot.adapter.component.MessageWriter
import ru.telegram.bot.adapter.strategy.dto.DataModel
import ru.telegram.bot.adapter.strategy.message.common.AbstractSendMessage

@Component
class BalanceMessage(messageWriter: MessageWriter) : AbstractSendMessage<DataModel>(messageWriter)