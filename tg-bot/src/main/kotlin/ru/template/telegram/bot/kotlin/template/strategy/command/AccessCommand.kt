package ru.template.telegram.bot.kotlin.template.strategy.command

import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Component
import ru.template.telegram.bot.kotlin.template.dto.enums.BotCommand
import ru.template.telegram.bot.kotlin.template.repository.UsersRepository

@Component
class AccessCommand(
    usersRepository: UsersRepository,
    applicationEventPublisher: ApplicationEventPublisher
) : AbstractCommand(BotCommand.ACCESS, usersRepository, applicationEventPublisher)