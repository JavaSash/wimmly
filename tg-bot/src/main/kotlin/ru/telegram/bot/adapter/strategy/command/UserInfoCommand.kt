package ru.telegram.bot.adapter.strategy.command

import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Component
import ru.telegram.bot.adapter.dto.enums.BotCommand
import ru.telegram.bot.adapter.repository.UsersRepository
import ru.telegram.bot.adapter.strategy.command.common.AbstractCommand

@Component
class UserInfoCommand(
    usersRepository: UsersRepository,
    applicationEventPublisher: ApplicationEventPublisher
) : AbstractCommand(BotCommand.USER_INFO, usersRepository, applicationEventPublisher)
