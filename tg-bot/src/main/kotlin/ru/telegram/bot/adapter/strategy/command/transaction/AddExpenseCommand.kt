package ru.telegram.bot.adapter.strategy.command.transaction

import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Component
import org.telegram.telegrambots.meta.api.objects.User
import org.telegram.telegrambots.meta.api.objects.chat.Chat
import ru.telegram.bot.adapter.dto.enums.BotCommand
import ru.telegram.bot.adapter.dto.enums.StepCode
import ru.telegram.bot.adapter.repository.UsersRepository
import ru.telegram.bot.adapter.service.UserService
import ru.telegram.bot.adapter.strategy.command.common.AbstractCommand
import ru.telegram.bot.adapter.utils.Constants.Transaction.EXPENSE

@Component
class AddExpenseCommand(
    private val usersRepository: UsersRepository,
    private val userService: UserService,
    private val applicationEventPublisher: ApplicationEventPublisher
) : AbstractCommand(BotCommand.ADD_EXPENSE, usersRepository, applicationEventPublisher) {

    override fun prepare(user: User, chat: Chat, arguments: Array<out String>) {
        val chatId = chat.id
        if (usersRepository.isUserExist(chatId)) {
            updateExpense(chatId)
        } else {
            usersRepository.createUser(chatId)
            userService.syncUserToBackend(chatId, user)
            updateExpense(chatId)
        }
    }

    private fun updateExpense(chatId: Long) {
        usersRepository.updateUserStep(chatId, StepCode.ADD_EXPENSE)
        usersRepository.updateTransactionType(chatId, EXPENSE)
    }
}