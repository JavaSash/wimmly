package ru.template.telegram.bot.kotlin.template.event

import org.telegram.telegrambots.meta.api.objects.CallbackQuery
import ru.template.telegram.bot.kotlin.template.dto.enums.StepCode

class TelegramReceivedCallbackEvent(
    val chatId: Long,
    val stepCode: StepCode,
    val callback: CallbackQuery
)
