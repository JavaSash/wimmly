package ru.telegram.bot.adapter.strategy.message

import org.springframework.stereotype.Component
import ru.telegram.bot.adapter.component.MessageWriter
import ru.telegram.bot.adapter.strategy.dto.ButtonResponseDto

@Component
class ButtonResponseMessage(messageWriter: MessageWriter) : AbstractSendMessage<ButtonResponseDto>(messageWriter)