package ru.telegram.bot.adapter.strategy.logic.common

import org.telegram.telegrambots.meta.api.objects.message.Message


interface MessageChooser: Chooser {
    fun execute(chatId: Long, message: Message)
}