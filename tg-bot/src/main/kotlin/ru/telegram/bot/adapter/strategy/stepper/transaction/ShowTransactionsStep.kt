package ru.telegram.bot.adapter.strategy.stepper.transaction

import org.springframework.stereotype.Component
import ru.telegram.bot.adapter.dto.enums.StepCode
import ru.telegram.bot.adapter.repository.ChatContextRepository
import ru.telegram.bot.adapter.strategy.stepper.common.Step

@Component
class ShowTransactionsStep(
    private val chatContextRepository: ChatContextRepository
) : Step {

    override fun getNextStep(chatId: Long): StepCode? {
        return if (chatContextRepository.getUser(chatId)?.flowContext == StepCode.DELETE_TRANSACTION.name) StepCode.ASK_DELETE_TRANSACTION
        else StepCode.FINAL
    }
}