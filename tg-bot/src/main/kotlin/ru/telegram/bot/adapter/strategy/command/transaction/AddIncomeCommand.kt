package ru.telegram.bot.adapter.strategy.command.transaction

import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Component
import org.telegram.telegrambots.meta.api.objects.User
import org.telegram.telegrambots.meta.api.objects.chat.Chat
import ru.telegram.bot.adapter.dto.enums.BotCommand
import ru.telegram.bot.adapter.dto.enums.StepCode
import ru.telegram.bot.adapter.repository.UsersRepository
import ru.telegram.bot.adapter.strategy.command.common.AbstractCommand
import ru.telegram.bot.adapter.utils.Constants.Transaction.INCOME

@Component
class AddIncomeCommand(
    private val usersRepository: UsersRepository,
    private val applicationEventPublisher: ApplicationEventPublisher
) : AbstractCommand(BotCommand.ADD_INCOME, usersRepository, applicationEventPublisher) {

    override fun prepare(user: User, chat: Chat, arguments: Array<out String>) {
        val chatId = chat.id
        if (usersRepository.isUserExist(chatId)) {
            updateIncome(chatId)
        } else {
            usersRepository.createUser(chatId)
            updateIncome(chatId)
        }
    }

    private fun updateIncome(chatId: Long) {
        usersRepository.updateUserStep(chatId, StepCode.ADD_INCOME)
        usersRepository.updateTransactionType(chatId, INCOME)
    }
}