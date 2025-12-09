package ru.telegram.bot.adapter.strategy.logic

import org.telegram.telegrambots.meta.api.objects.CallbackQuery
import ru.telegram.bot.adapter.dto.enums.ExecuteStatus
import ru.telegram.bot.adapter.strategy.logic.common.Chooser

interface CallbackChooser : Chooser {
    fun execute(chatId: Long, callbackQuery: CallbackQuery): ExecuteStatus
}