package ru.telegram.bot.adapter.strategy.stepper.transaction

import org.springframework.stereotype.Component
import ru.telegram.bot.adapter.dto.enums.StepCode
import ru.telegram.bot.adapter.repository.ChatContextRepository
import ru.telegram.bot.adapter.repository.TransactionDraftRepository
import ru.telegram.bot.adapter.strategy.stepper.common.Step
import java.time.Instant

@Component
class AskDateStep(
    private val chatContextRepository: ChatContextRepository,
    private val transactionDraftRepository: TransactionDraftRepository,
) : Step {
    override fun getNextStep(chatId: Long): StepCode? {
        val user = chatContextRepository.getUser(chatId)
        return if (user?.accept == true) {
            StepCode.ENTER_DATE
        } else {
            transactionDraftRepository.updateTransactionDate(chatId, Instant.now()) // todo вынест в Chooser эту логику, не относится к шагам
            StepCode.ASK_COMMENT
        }
    }
}