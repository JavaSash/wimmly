package ru.telegram.bot.adapter.strategy

import mu.KLogging
import org.springframework.stereotype.Component
import ru.telegram.bot.adapter.dto.enums.StepCode
import ru.telegram.bot.adapter.strategy.stepper.Step
// Выбор следующего этапа
@Component
class StepContext(private val step: List<Step>) {

    companion object:KLogging()

    fun next(chatId: Long, stepCode: StepCode): StepCode? {
        logger.info { "$$$ StepContext.next step $stepCode for chat $chatId" }
        return step.firstOrNull { it.isAvailableForCurrentStep(stepCode) }?.getNextStep(chatId)
    }

}
