package ru.telegram.bot.adapter.api

import mu.KLogging
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

/**
 * Consumes updates from Tg
 * @param botProperty produce bot properties
 * @param receiverService processing non commands updates
 * @see [SpringLongPollingBot] - Spring long polling integration
 * @see [CommandLongPollingTelegramBot] - basic implementation for processing commands by long polling
 */
@Component
class TelegramConsumer(
    private val botProperty: BotProperty,
    private val receiverService: ReceiverService,
    commands: List<AbstractCommand>,
    telegramClient: TelegramClient
) : SpringLongPollingBot, CommandLongPollingTelegramBot(telegramClient, true, { botProperty.username }) {

    companion object : KLogging()

    /**
     * Register bot commands
     */
    init {
        registerAll(*commands.toTypedArray())
    }

    override fun getBotToken() = botProperty.token

    /**
     * This class will process income updates
     */
    override fun getUpdatesConsumer(): LongPollingUpdateConsumer {
        return this
    }

    /**
     * For process non commands updates (message or push of button)
     */
    override fun processNonCommandUpdate(update: Update) {
        receiverService.execute(update)
    }

    @AfterBotRegistration
    fun afterRegistration(botSession: BotSession) {
        logger.info { "$$$ Registered bot running state is: " + botSession.isRunning }
    }

}