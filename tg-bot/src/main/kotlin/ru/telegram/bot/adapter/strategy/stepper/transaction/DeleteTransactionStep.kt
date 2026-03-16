package ru.telegram.bot.adapter.strategy.stepper.transaction

import org.springframework.stereotype.Component
import ru.telegram.bot.adapter.dto.enums.StepCode
import ru.telegram.bot.adapter.service.ErrorService
import ru.telegram.bot.adapter.strategy.stepper.common.Step

@Component
class DeleteTransactionStep(private val errorService: ErrorService) : Step {
    override fun getNextStep(chatId: Long): StepCode? =
        errorService.resolveNextStep(chatId = chatId, onSuccessStep = StepCode.SHOW_TRANSACTIONS)
}