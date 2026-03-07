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
import ru.telegram.bot.adapter.repository.ChatContextRepository
import ru.telegram.bot.adapter.repository.SearchContextRepository
import ru.telegram.bot.adapter.repository.TransactionDraftRepository
import ru.telegram.bot.adapter.service.UserService
import ru.telegram.bot.adapter.strategy.command.report.BalanceCommand

/**
 * Logic of processing Start command
 */
@Component
class StartCommand(
    chatContextRepository: ChatContextRepository,
    private val balanceCommand: BalanceCommand,
    private val applicationEventPublisher: ApplicationEventPublisher,
    userService: UserService,
    transactionDraftRepository: TransactionDraftRepository,
    searchContextRepository: SearchContextRepository
) : AbstractCommand(
    BotCommand.START,
    chatContextRepository,
    applicationEventPublisher,
    userService,
    transactionDraftRepository,
    searchContextRepository
) {

    companion object : KLogging()

    override fun execute(telegramClient: TelegramClient, user: User, chat: Chat, arguments: Array<out String>) {
        logger.info { "$$$ StartCommand.execute for user: $user and chat: $chat" }
        val chatId = chat.id

        if (chatContextRepository.isUserExist(chatId)) {
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
}
