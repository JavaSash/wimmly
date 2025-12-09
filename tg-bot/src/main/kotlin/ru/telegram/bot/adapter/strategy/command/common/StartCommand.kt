package ru.telegram.bot.adapter.strategy.command.common

import mu.KLogging
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Component
import org.telegram.telegrambots.meta.api.objects.User
import org.telegram.telegrambots.meta.api.objects.chat.Chat
import org.telegram.telegrambots.meta.generics.TelegramClient
import ru.telegram.bot.adapter.dto.enums.BotCommand
import ru.telegram.bot.adapter.dto.enums.StepCode
import ru.telegram.bot.adapter.event.TgStepMessageEvent
import ru.telegram.bot.adapter.repository.UsersRepository
import ru.telegram.bot.adapter.strategy.command.report.BalanceCommand

/**
 * Logic of processing Start command
 */
@Component
class StartCommand(
    private val usersRepository: UsersRepository,
    private val balanceCommand: BalanceCommand,
    private val applicationEventPublisher: ApplicationEventPublisher
) : AbstractCommand(BotCommand.START, usersRepository, applicationEventPublisher) {

    companion object : KLogging()

    override fun execute(telegramClient: TelegramClient, user: User, chat: Chat, arguments: Array<out String>) {
        logger.info { "$$$ StartCommand.execute for user: $user and chat: $chat" }

        val chatId = chat.id

        if (usersRepository.isUserExist(chatId)) {
            // Для существующего пользователя - показываем баланс
            balanceCommand.execute(telegramClient, user, chat, arguments)
        } else {
            // Для нового пользователя - стандартный START
            prepare(user, chat, arguments)

            applicationEventPublisher.publishEvent(
                TgStepMessageEvent(
                    chatId = chatId,
                    stepCode = StepCode.START
                )
            )
        }
    }

    override fun prepare(user: User, chat: Chat, arguments: Array<out String>) {
        logger.info { "$$$ StartCommand.prepare for user: $user and chat: $chat with arguments: $arguments" }

        val chatId = chat.id
        if (usersRepository.isUserExist(chatId)) {
            usersRepository.updateUserStep(chatId, StepCode.START)
        } else usersRepository.createUser(chatId)
    }
}
