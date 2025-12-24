package ru.telegram.bot.adapter.strategy.message

import org.springframework.stereotype.Component
import ru.telegram.bot.adapter.service.MessageWriter
import ru.telegram.bot.adapter.strategy.dto.ButtonResponseDto
import ru.telegram.bot.adapter.strategy.message.common.AbstractSendMessage

@Component
class ButtonResponseMessage(messageWriter: MessageWriter) : AbstractSendMessage<ButtonResponseDto>(messageWriter)