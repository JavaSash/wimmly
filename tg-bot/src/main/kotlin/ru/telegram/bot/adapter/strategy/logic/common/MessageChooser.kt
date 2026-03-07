package ru.telegram.bot.adapter.strategy.logic.common

import org.telegram.telegrambots.meta.api.objects.message.Message

/**
 * Обработчик пользовательского ввода
 * Исполняется только как реакция на сообщение\действие пользователя
 * То есть для шагов отчётов, где нет пользовательского ввода (только вызов команды) логика будет в Step классе,
 * @see [ru.telegram.bot.adapter.strategy.stepper.transaction.CreateTransactionStep] и шаги отчётов
 */
interface MessageChooser: Chooser {
    fun execute(chatId: Long, message: Message)
}