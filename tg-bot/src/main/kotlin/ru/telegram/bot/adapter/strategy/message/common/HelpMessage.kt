package ru.telegram.bot.adapter.strategy.message.common

import org.springframework.stereotype.Component
import ru.telegram.bot.adapter.component.MessageWriter
import ru.telegram.bot.adapter.strategy.dto.DataModel

/**
 * Response class for command
 * What receive client when call bot-command
 */
@Component
class HelpMessage(messageWriter: MessageWriter) : AbstractSendMessage<DataModel>(messageWriter)