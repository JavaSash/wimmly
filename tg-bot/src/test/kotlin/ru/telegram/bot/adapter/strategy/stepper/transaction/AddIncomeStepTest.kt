package ru.telegram.bot.adapter.strategy.stepper.transaction

import org.junit.jupiter.api.BeforeEach
import ru.telegram.bot.adapter.dto.enums.StepCode
import ru.telegram.bot.adapter.strategy.stepper.common.StepBasicTest

class AddIncomeStepTest: StepBasicTest() {
    @BeforeEach
    fun setup() {
        step = AddIncomeStep()
        nextStep = StepCode.SELECT_CATEGORY
    }
}