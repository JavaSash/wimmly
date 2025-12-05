package ru.telegram.bot.adapter.event

import org.telegram.telegrambots.meta.api.objects.CallbackQuery
import ru.telegram.bot.adapter.dto.enums.StepCode

class TgReceivedCallbackEvent(
    val chatId: Long,
    val stepCode: StepCode,
    val callback: CallbackQuery
)
