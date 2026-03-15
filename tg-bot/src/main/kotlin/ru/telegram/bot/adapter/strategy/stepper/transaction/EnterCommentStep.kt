package ru.telegram.bot.adapter.strategy.stepper.transaction

import mu.KLogging
import org.springframework.stereotype.Component
import ru.telegram.bot.adapter.dto.enums.StepCode
import ru.telegram.bot.adapter.service.ErrorService
import ru.telegram.bot.adapter.strategy.stepper.common.Step

@Component
class EnterCommentStep(
    private val errorService: ErrorService
) : Step {

    companion object : KLogging()

    override fun getNextStep(chatId: Long): StepCode? {
        return errorService.resolveNextStep(chatId = chatId, onSuccessStep = StepCode.CREATE_TRANSACTION)
    }
}