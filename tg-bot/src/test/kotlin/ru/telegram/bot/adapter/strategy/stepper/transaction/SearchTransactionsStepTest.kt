package ru.telegram.bot.adapter.strategy.stepper.transaction

import org.junit.jupiter.api.BeforeEach
import ru.telegram.bot.adapter.dto.enums.StepCode
import ru.telegram.bot.adapter.strategy.stepper.common.StepBasicTest

class SearchTransactionsStepTest: StepBasicTest() {
    @BeforeEach
    fun setup() {
        step = SearchTransactionsStep()
        nextStep = StepCode.ASK_TRANSACTION_TYPE
    }
}