package ru.telegram.bot.adapter.strategy.logic.common

import org.telegram.telegrambots.meta.api.objects.CallbackQuery
import ru.telegram.bot.adapter.dto.enums.ExecuteStatus

/**
 * Implement only for buttons to get user answer (callback) for button
 */
interface CallbackChooser : Chooser {
    fun execute(chatId: Long, callbackQuery: CallbackQuery): ExecuteStatus
}