package ru.telegram.bot.adapter.strategy.command.report

import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Component
import ru.telegram.bot.adapter.dto.enums.BotCommand
import ru.telegram.bot.adapter.repository.ChatContextRepository
import ru.telegram.bot.adapter.repository.SearchContextRepository
import ru.telegram.bot.adapter.repository.TransactionDraftRepository
import ru.telegram.bot.adapter.service.UserService
import ru.telegram.bot.adapter.strategy.command.common.AbstractCommand

@Component
class BalanceCommand(
    chatContextRepository: ChatContextRepository,
    applicationEventPublisher: ApplicationEventPublisher,
    userService: UserService,
    transactionDraftRepository: TransactionDraftRepository,
    searchContextRepository: SearchContextRepository
) : AbstractCommand(
    BotCommand.BALANCE, chatContextRepository, applicationEventPublisher, userService,
    transactionDraftRepository,
    searchContextRepository
)