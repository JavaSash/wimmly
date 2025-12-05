package ru.telegram.bot.adapter.strategy.logic

import org.springframework.stereotype.Component
import org.telegram.telegrambots.meta.api.objects.message.Message
import ru.telegram.bot.adapter.repository.UsersRepository

@Component
class UserInfoChooser(private val usersRepository: UsersRepository) : MessageChooser {

    override fun execute(chatId: Long, message: Message) {
        usersRepository.updateText(chatId, message.text)
    }
}