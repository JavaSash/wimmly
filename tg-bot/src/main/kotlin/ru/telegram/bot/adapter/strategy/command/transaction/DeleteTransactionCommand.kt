package ru.telegram.bot.adapter.strategy.command.transaction

import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Component
import org.telegram.telegrambots.meta.api.objects.User
import org.telegram.telegrambots.meta.api.objects.chat.Chat
import ru.telegram.bot.adapter.dto.enums.BotCommand
import ru.telegram.bot.adapter.dto.enums.StepCode
import ru.telegram.bot.adapter.repository.ChatContextRepository
import ru.telegram.bot.adapter.repository.SearchContextRepository
import ru.telegram.bot.adapter.repository.TransactionDraftRepository
import ru.telegram.bot.adapter.service.UserService
import ru.telegram.bot.adapter.strategy.command.common.AbstractCommand

/**
 * Ввод номера транзакции
 * Вывод информации о транзакции
 * Подтверждение удаления
 */
@Component
class DeleteTransactionCommand(
    chatContextRepository: ChatContextRepository,
    userService: UserService,
    applicationEventPublisher: ApplicationEventPublisher,
    transactionDraftRepository: TransactionDraftRepository,
    searchContextRepository: SearchContextRepository
): AbstractCommand(
    BotCommand.DELETE_TRANSACTION, chatContextRepository, applicationEventPublisher, userService,
    transactionDraftRepository,
    searchContextRepository
) {

    override fun doPrepare(user: User, chat: Chat, arguments: Array<out String>) {
        chatContextRepository.updateFlowContext(
            chat.id,
            StepCode.DELETE_TRANSACTION.name
        ) // mark user flow to choose correct step after common steps
    }
}