package ru.telegram.bot.adapter.strategy.stepper.report

import org.junit.jupiter.api.BeforeEach
import ru.telegram.bot.adapter.dto.enums.StepCode
import ru.telegram.bot.adapter.strategy.stepper.common.StepBasicTest

class ReportWeekStepTest: StepBasicTest() {

    @BeforeEach
    fun setup() {
        step = ReportWeekStep()
        nextStep = StepCode.FINAL
    }
}
