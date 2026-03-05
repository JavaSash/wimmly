package ru.telegram.bot.adapter.strategy.command.report

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

@Component
class ReportYearCommand(
    chatContextRepository: ChatContextRepository,
    applicationEventPublisher: ApplicationEventPublisher,
    userService: UserService,
    transactionDraftRepository: TransactionDraftRepository,
    searchContextRepository: SearchContextRepository
) : AbstractCommand(
    BotCommand.REPORT_YEAR, chatContextRepository, applicationEventPublisher, userService,
    transactionDraftRepository,
    searchContextRepository
) {

    override fun doPrepare(user: User, chat: Chat, arguments: Array<out String>) {
        chatContextRepository.updateUserStep(chat.id, StepCode.REPORT_YEAR)
    }
}