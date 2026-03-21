package ru.telegram.bot.adapter.strategy.stepper.transaction

import org.springframework.stereotype.Component
import ru.telegram.bot.adapter.dto.enums.StepCode
import ru.telegram.bot.adapter.repository.ChatContextRepository
import ru.telegram.bot.adapter.strategy.stepper.common.Step

@Component
class AskCommentStep(
    private val chatContextRepository: ChatContextRepository
) : Step {
    override fun getNextStep(chatId: Long): StepCode? {
        val user = chatContextRepository.getUser(chatId)
        return when (user?.accept) {
            true -> StepCode.ENTER_COMMENT
            false -> StepCode.CREATE_TRANSACTION
            else -> null // todo ERROR step?
        }
    }
}