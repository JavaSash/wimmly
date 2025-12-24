package ru.telegram.bot.adapter.strategy.stepper.report

import org.springframework.stereotype.Component
import ru.telegram.bot.adapter.dto.enums.StepCode
import ru.telegram.bot.adapter.strategy.stepper.common.Step

@Component
class ReportMonthStep : Step {
    override fun getNextStep(chatId: Long): StepCode? {
        return StepCode.FINAL
    }
}