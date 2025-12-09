package ru.telegram.bot.adapter.api

import org.springframework.stereotype.Component
import org.telegram.telegrambots.extensions.bots.commandbot.CommandLongPollingTelegramBot
import org.telegram.telegrambots.longpolling.BotSession
import org.telegram.telegrambots.longpolling.interfaces.LongPollingUpdateConsumer
import org.telegram.telegrambots.longpolling.starter.AfterBotRegistration
import org.telegram.telegrambots.longpolling.starter.SpringLongPollingBot
import org.telegram.telegrambots.meta.api.objects.Update
import org.telegram.telegrambots.meta.generics.TelegramClient
import ru.telegram.bot.adapter.config.BotProperty
import ru.telegram.bot.adapter.service.ReceiverService
import ru.telegram.bot.adapter.strategy.command.common.AbstractCommand

/*
Базовый класс Телеграм АПИ, который отправляет и принимает сообщения.
в примере есть проперти, который нужно задать через BotFather
 */
@Component
class TelegramConsumer(
    private val botProperty: BotProperty,
    private val receiverService: ReceiverService,
    commands: List<AbstractCommand>,
    telegramClient: TelegramClient
) : SpringLongPollingBot, CommandLongPollingTelegramBot(telegramClient, true, { botProperty.username }) {


    init {
        registerAll(*commands.toTypedArray())
    }

    override fun getBotToken() = botProperty.token

    override fun getUpdatesConsumer(): LongPollingUpdateConsumer {
        return this
    }

    override fun processNonCommandUpdate(update: Update) {
        receiverService.execute(update)
    }

    @AfterBotRegistration
    fun afterRegistration(botSession: BotSession) {
        println("Registered bot running state is: " + botSession.isRunning)
    }

}