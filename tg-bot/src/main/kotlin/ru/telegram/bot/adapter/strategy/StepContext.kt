package ru.telegram.bot.adapter.strategy

import org.springframework.stereotype.Component
import ru.telegram.bot.adapter.dto.enums.StepCode
import ru.telegram.bot.adapter.strategy.stepper.Step
// Выбор следующего этапа
@Component
class StepContext(private val step: List<Step>) {

    fun next(chatId: Long, stepCode: StepCode): StepCode? {
        return step.firstOrNull { it.isAvailableForCurrentStep(stepCode) }?.getNextStep(chatId)
    }

}
