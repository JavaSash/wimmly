package ru.telegram.bot.adapter.strategy.stepper.transaction

import org.junit.jupiter.api.BeforeEach
import ru.telegram.bot.adapter.dto.enums.StepCode
import ru.telegram.bot.adapter.strategy.stepper.common.StepBasicTest

class AskTransactionTypeStepTest: StepBasicTest() {
    @BeforeEach
    fun setup() {
        step = AskTransactionTypeStep()
        nextStep = StepCode.SELECT_CATEGORY
    }
}
