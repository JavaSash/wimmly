package ru.telegram.bot.adapter.strategy.command.common

import mu.KLogging
import org.springframework.context.ApplicationEventPublisher
import org.telegram.telegrambots.extensions.bots.commandbot.commands.BotCommand
import org.telegram.telegrambots.meta.api.objects.User
import org.telegram.telegrambots.meta.api.objects.chat.Chat
import org.telegram.telegrambots.meta.generics.TelegramClient
import ru.telegram.bot.adapter.event.TgStepMessageEvent
import ru.telegram.bot.adapter.repository.ChatContextRepository
import ru.telegram.bot.adapter.repository.SearchContextRepository
import ru.telegram.bot.adapter.repository.TransactionDraftRepository
import ru.telegram.bot.adapter.service.UserService
import ru.telegram.bot.adapter.utils.CommonUtils.currentStepCode

/**
 * One class for one bot command
 * To create new command extend from this class
 */
abstract class AbstractCommand(
    botCommand: ru.telegram.bot.adapter.dto.enums.BotCommand,
    protected val chatContextRepository: ChatContextRepository, // todo перенести все репы в 1 сервис по работе с контекстом диалога
    private val applicationEventPublisher: ApplicationEventPublisher,
    private val userService: UserService,
    protected val transactionDraftRepository: TransactionDraftRepository,
    protected val searchContextRepository: SearchContextRepository
) : BotCommand(botCommand.command, botCommand.desc), BotCommands {

    companion object : KLogging()

    fun classStepCode() = this.currentStepCode("Command")

    override fun execute(telegramClient: TelegramClient, user: User, chat: Chat, arguments: Array<out String>) {
        prepare(user, chat, arguments)
        applicationEventPublisher.publishEvent(
            TgStepMessageEvent(chatId = chat.id, stepCode = classStepCode())
        )
    }

    /**
     * Check is user exist in bot and backend DB and register if not
     */
    final override fun prepare(user: User, chat: Chat, arguments: Array<out String>) {
        val chatId = chat.id
        if (chatId <= 0) {
            logger.warn { "$$$ Group chat id $chatId is not supported" }
            return // todo throw exc and go to StepCode.NOT_SUPPORTED
        }
        if (chatContextRepository.isUserExist(chatId)) {
            if (!userService.isExist(chatId)) {
                userService.syncUserToBackend(chatId, user)
            }
        } else {
            chatContextRepository.createUser(chatId)
            transactionDraftRepository.createTransactionDraft(chatId)
            searchContextRepository.createSearchContext(chatId)
            userService.syncUserToBackend(chatId, user)
        }
        doPrepare(user, chat, arguments)
    }

    /**
     * Hook for child class logic
     */
    protected open fun doPrepare(user: User, chat: Chat, arguments: Array<out String>) {
    }
}