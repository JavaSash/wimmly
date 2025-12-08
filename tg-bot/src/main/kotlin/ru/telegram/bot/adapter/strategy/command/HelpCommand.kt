package ru.telegram.bot.adapter.strategy.command

import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Component
import org.telegram.telegrambots.meta.api.objects.User
import org.telegram.telegrambots.meta.api.objects.chat.Chat
import ru.telegram.bot.adapter.dto.enums.BotCommand
import ru.telegram.bot.adapter.dto.enums.StepCode
import ru.telegram.bot.adapter.repository.UsersRepository

@Component
class HelpCommand(
    private val usersRepository: UsersRepository,
    private val applicationEventPublisher: ApplicationEventPublisher
) : AbstractCommand(BotCommand.HELP, usersRepository, applicationEventPublisher) {

    override fun prepare(user: User, chat: Chat, arguments: Array<out String>) {
        val chatId = chat.id
        if (usersRepository.isUserExist(chatId)) {
            usersRepository.updateUserStep(chatId, StepCode.HELP)
        } else usersRepository.createUser(chatId)
    }
}