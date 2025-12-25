package ru.telegram.bot.adapter.strategy.logic.transaction

import org.springframework.stereotype.Component
import org.telegram.telegrambots.meta.api.objects.message.Message
import ru.telegram.bot.adapter.dto.enums.StepCode
import ru.telegram.bot.adapter.repository.UsersRepository
import ru.telegram.bot.adapter.strategy.logic.common.MessageChooser

@Component
class EnterCommentChooser(
    private val usersRepository: UsersRepository
) : MessageChooser {
    override fun execute(chatId: Long, message: Message) {
        usersRepository.updateComment(chatId, message.text)
        usersRepository.updateAccept(chatId, false) // set to default to use in another buttons
        usersRepository.updateUserStep(chatId, StepCode.CREATE_TRANSACTION)
    }
}