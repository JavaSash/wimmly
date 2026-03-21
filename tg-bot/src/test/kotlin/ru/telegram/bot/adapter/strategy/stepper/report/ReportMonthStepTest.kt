package ru.telegram.bot.adapter.strategy.stepper.report

import org.junit.jupiter.api.BeforeEach
import ru.telegram.bot.adapter.dto.enums.StepCode
import ru.telegram.bot.adapter.strategy.stepper.common.StepBasicTest

class ReportMonthStepTest: StepBasicTest() {

    @BeforeEach
    fun setup() {
        step = ReportMonthStep()
        nextStep = StepCode.FINAL
    }
}
