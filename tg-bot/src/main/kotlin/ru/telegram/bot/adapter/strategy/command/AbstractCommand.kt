package ru.telegram.bot.adapter.strategy.command

import org.springframework.context.ApplicationEventPublisher
import org.telegram.telegrambots.extensions.bots.commandbot.commands.BotCommand
import org.telegram.telegrambots.meta.api.objects.User
import org.telegram.telegrambots.meta.api.objects.chat.Chat
import org.telegram.telegrambots.meta.generics.TelegramClient
import ru.telegram.bot.adapter.event.TgStepMessageEvent
import ru.telegram.bot.adapter.repository.UsersRepository
import ru.telegram.bot.adapter.utils.CommonUtils.currentStepCode

/**
 * One class for one tg-bot command
 * To create new command extend from this class
 */
abstract class AbstractCommand(
    botCommand: ru.telegram.bot.adapter.dto.enums.BotCommand,
    private val usersRepository: UsersRepository,
    private val applicationEventPublisher: ApplicationEventPublisher
) : BotCommand(botCommand.command, botCommand.desc), BotCommands {

    fun classStepCode() = this.currentStepCode("Command")

    override fun execute(telegramClient: TelegramClient, user: User, chat: Chat, arguments: Array<out String>) {
        prepare(user, chat, arguments)

        val chatId = chat.id

        usersRepository.updateUserStep(chatId, classStepCode())

        applicationEventPublisher.publishEvent(
            TgStepMessageEvent(chatId = chatId, stepCode = classStepCode())
        )
    }
}