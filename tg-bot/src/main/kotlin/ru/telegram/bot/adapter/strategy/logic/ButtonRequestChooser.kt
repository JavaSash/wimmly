package ru.telegram.bot.adapter.strategy.logic

import org.springframework.stereotype.Component
import org.telegram.telegrambots.meta.api.objects.CallbackQuery
import ru.telegram.bot.adapter.dto.enums.ExecuteStatus
import ru.telegram.bot.adapter.repository.UsersRepository

@Component
class ButtonRequestChooser(private val usersRepository: UsersRepository) : CallbackChooser {

    override fun execute(chatId: Long, callbackQuery: CallbackQuery): ExecuteStatus {
        usersRepository.updateAccept(chatId, callbackQuery.data)
        return ExecuteStatus.FINAL
    }
}