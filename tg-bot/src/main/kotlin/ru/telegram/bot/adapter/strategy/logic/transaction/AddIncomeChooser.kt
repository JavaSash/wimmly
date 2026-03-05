package ru.telegram.bot.adapter.strategy.logic.transaction

import org.springframework.stereotype.Component
import org.telegram.telegrambots.meta.api.objects.message.Message
import ru.telegram.bot.adapter.dto.enums.StepCode
import ru.telegram.bot.adapter.repository.ChatContextRepository
import ru.telegram.bot.adapter.repository.TransactionDraftRepository
import ru.telegram.bot.adapter.strategy.logic.common.MessageChooser
import ru.telegram.bot.adapter.utils.Constants.Transaction.INCOME

@Component
class AddIncomeChooser(
    private val chatContextRepository: ChatContextRepository,
    private val transactionDraftRepository: TransactionDraftRepository
) : MessageChooser {

    override fun execute(chatId: Long, message: Message) {
        transactionDraftRepository.updateTransactionType(chatId, INCOME)
        chatContextRepository.updateUserStep(chatId, StepCode.SELECT_CATEGORY)
    }
}