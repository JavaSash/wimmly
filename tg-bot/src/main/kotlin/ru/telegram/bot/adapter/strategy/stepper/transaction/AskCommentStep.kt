package ru.telegram.bot.adapter.strategy.stepper.transaction

import org.springframework.stereotype.Component
import ru.telegram.bot.adapter.dto.enums.StepCode
import ru.telegram.bot.adapter.repository.ChatContextRepository
import ru.telegram.bot.adapter.repository.TransactionDraftRepository
import ru.telegram.bot.adapter.strategy.stepper.common.Step

@Component
class AskCommentStep(
    private val chatContextRepository: ChatContextRepository,
    private val transactionDraftRepository: TransactionDraftRepository,
) : Step {
    override fun getNextStep(chatId: Long): StepCode? {
        val user = chatContextRepository.getUser(chatId)
        return if (user?.accept == true) {
            StepCode.ENTER_COMMENT
        } else {
            transactionDraftRepository.updateComment(chatId, comment = null) // todo вынести в Chooser логику
            StepCode.CREATE_TRANSACTION
        }
    }
}