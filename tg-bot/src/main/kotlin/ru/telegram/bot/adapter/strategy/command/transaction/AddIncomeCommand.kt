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
import ru.telegram.bot.adapter.utils.Constants.Transaction.INCOME

@Component
class AddIncomeCommand(
    chatContextRepository: ChatContextRepository,
    userService: UserService,
    applicationEventPublisher: ApplicationEventPublisher,
    transactionDraftRepository: TransactionDraftRepository,
    searchContextRepository: SearchContextRepository
) : AbstractCommand(
    BotCommand.ADD_INCOME, chatContextRepository, applicationEventPublisher, userService,
    transactionDraftRepository,
    searchContextRepository
) {

    override fun doPrepare(user: User, chat: Chat, arguments: Array<out String>) {
        val chatId = chat.id
        transactionDraftRepository.updateTransactionType(chatId, INCOME)
        chatContextRepository.updateFlowContext(chatId, StepCode.ADD_INCOME.name)
    }
}