package ru.telegram.bot.adapter.strategy.stepper.common

import org.springframework.stereotype.Component
import ru.telegram.bot.adapter.dto.enums.StepCode
import ru.telegram.bot.adapter.service.ErrorService

@Component
class ErrorStep(
    private val errorService: ErrorService
) : Step {

    override fun getNextStep(chatId: Long): StepCode? = errorService.getStepBeforeError(chatId)
}