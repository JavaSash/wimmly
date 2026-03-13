package ru.telegram.bot.adapter.strategy.stepper.common

import mu.KLogging
import org.springframework.stereotype.Component
import ru.telegram.bot.adapter.dto.enums.StepCode
import ru.telegram.bot.adapter.service.ErrorService

@Component
class ErrorStep(
    private val errorService: ErrorService
) : Step {

    companion object : KLogging()

    override fun getNextStep(chatId: Long): StepCode? = errorService.getStepBeforeError(chatId)
}