package ru.telegram.bot.adapter.strategy.command.common

import mu.KLogging
import org.springframework.context.ApplicationEventPublisher
import org.telegram.telegrambots.extensions.bots.commandbot.commands.BotCommand
import org.telegram.telegrambots.meta.api.objects.User
import org.telegram.telegrambots.meta.api.objects.chat.Chat
import org.telegram.telegrambots.meta.generics.TelegramClient
import ru.telegram.bot.adapter.event.TgStepMessageEvent
import ru.telegram.bot.adapter.repository.UsersRepository
import ru.telegram.bot.adapter.service.UserService
import ru.telegram.bot.adapter.utils.CommonUtils.currentStepCode

/**
 * One class for one tg-bot command
 * To create new command extend from this class
 */
abstract class AbstractCommand(
    botCommand: ru.telegram.bot.adapter.dto.enums.BotCommand,
    private val usersRepository: UsersRepository,
    private val applicationEventPublisher: ApplicationEventPublisher,
    private val userService: UserService
) : BotCommand(botCommand.command, botCommand.desc), BotCommands {

    companion object : KLogging()

    fun classStepCode() = this.currentStepCode("Command")

    override fun execute(telegramClient: TelegramClient, user: User, chat: Chat, arguments: Array<out String>) {
        prepare(user, chat, arguments)

        val chatId = chat.id

        usersRepository.updateUserStep(chatId, classStepCode())

        applicationEventPublisher.publishEvent(
            TgStepMessageEvent(chatId = chatId, stepCode = classStepCode())
        )
    }

    final override fun prepare(user: User, chat: Chat, arguments: Array<out String>) {
        val chatId = chat.id

        if (chatId <= 0) {
            // todo        if (chatId < 0) usersRepository.updateUserStep(chatId, StepCode.NOT_SUPPORTED)
            logger.warn { ">>> Group chat id $chatId is not supported" }
            return
        }
        /**
         * Check is user exist in bot DB and in backend DB
         */
        if (!usersRepository.isUserExist(chatId)) {
            usersRepository.createUser(chatId)
            userService.syncUserToBackend(chatId, user)
        } else {
            if (!userService.isExist(chatId)) {
                userService.syncUserToBackend(chatId, user)
            }
        }
        doPrepare(user, chat, arguments)
    }

    /**
     * Hook for child class logic
     */
    protected open fun doPrepare(user: User, chat: Chat, arguments: Array<out String>) {
    }
}