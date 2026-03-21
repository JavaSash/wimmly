package ru.telegram.bot.adapter.strategy.message.transaction

import org.springframework.stereotype.Component
import ru.telegram.bot.adapter.service.MessageWriter
import ru.telegram.bot.adapter.strategy.message.common.YesNoMessage

@Component
class AskDateMessage(messageWriter: MessageWriter) : YesNoMessage(messageWriter)