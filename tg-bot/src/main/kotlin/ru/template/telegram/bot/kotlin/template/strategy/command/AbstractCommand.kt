package ru.template.telegram.bot.kotlin.template.strategy.command

import org.springframework.context.ApplicationEventPublisher
import org.telegram.telegrambots.extensions.bots.commandbot.commands.BotCommand
import org.telegram.telegrambots.meta.api.objects.User
import org.telegram.telegrambots.meta.api.objects.chat.Chat
import org.telegram.telegrambots.meta.generics.TelegramClient
import ru.template.telegram.bot.kotlin.template.dto.enums.BotCommand
import ru.template.telegram.bot.kotlin.template.event.TelegramStepMessageEvent
import ru.template.telegram.bot.kotlin.template.repository.UsersRepository
import ru.template.telegram.bot.kotlin.template.utils.CommonUtils.currentStepCode


abstract class AbstractCommand(
    botCommand: ru.template.telegram.bot.kotlin.template.dto.enums.BotCommand,
    private val usersRepository: UsersRepository,
    private val applicationEventPublisher: ApplicationEventPublisher
) : BotCommand(botCommand.command, botCommand.desc), BotCommands {

    fun classStepCode() = this.currentStepCode("Command")

    override fun execute(telegramClient: TelegramClient, user: User, chat: Chat, arguments: Array<out String>) {
        prepare(user, chat, arguments)

        val chatId = chat.id

        usersRepository.updateUserStep(chatId, classStepCode())

        applicationEventPublisher.publishEvent(
            TelegramStepMessageEvent(chatId = chatId, stepCode = classStepCode())
        )
    }


}