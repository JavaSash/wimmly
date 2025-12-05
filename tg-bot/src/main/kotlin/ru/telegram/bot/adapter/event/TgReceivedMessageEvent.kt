package ru.telegram.bot.adapter.event

import org.telegram.telegrambots.meta.api.objects.message.Message
import ru.telegram.bot.adapter.dto.enums.StepCode

class TgReceivedMessageEvent(
    val chatId: Long,
    val stepCode: StepCode,
    val message: Message
)
