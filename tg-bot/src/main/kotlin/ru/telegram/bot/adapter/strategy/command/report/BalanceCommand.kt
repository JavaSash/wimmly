package ru.telegram.bot.adapter.strategy.command.report

import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Component
import org.telegram.telegrambots.meta.api.objects.User
import org.telegram.telegrambots.meta.api.objects.chat.Chat
import ru.telegram.bot.adapter.dto.enums.BotCommand
import ru.telegram.bot.adapter.dto.enums.StepCode
import ru.telegram.bot.adapter.repository.UsersRepository
import ru.telegram.bot.adapter.service.UserService
import ru.telegram.bot.adapter.strategy.command.common.AbstractCommand

@Component
class BalanceCommand(
    private val usersRepository: UsersRepository,
    applicationEventPublisher: ApplicationEventPublisher,
    userService: UserService
) : AbstractCommand(BotCommand.BALANCE, usersRepository, applicationEventPublisher, userService) {

    override fun doPrepare(user: User, chat: Chat, arguments: Array<out String>) {
        usersRepository.updateUserStep(chat.id, StepCode.BALANCE)
    }
}