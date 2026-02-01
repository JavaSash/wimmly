package ru.telegram.bot.adapter.strategy.command

import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Component
import ru.telegram.bot.adapter.dto.enums.BotCommand
import ru.telegram.bot.adapter.repository.UsersRepository
import ru.telegram.bot.adapter.service.UserService
import ru.telegram.bot.adapter.strategy.command.common.AbstractCommand

@Component
class ContactCommand(
    usersRepository: UsersRepository,
    applicationEventPublisher: ApplicationEventPublisher,
    userService: UserService
) : AbstractCommand(BotCommand.CONTACT, usersRepository, applicationEventPublisher, userService)